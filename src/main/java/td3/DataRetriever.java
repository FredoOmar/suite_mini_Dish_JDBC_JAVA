package td3;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    public Dish findDishById(int id) {
        Dish dish = null;
        String dishQuery = "SELECT id, name, dishType::text, price FROM Dish WHERE id = ?";
        String ingredientsQuery =
                "SELECT i.id, i.name, i.price, i.category::text, di.unit::text, di.quantity_required " +
                        "FROM Ingredient i " +
                        "JOIN dish_ingredients di ON i.id = di.id_ingredient " +
                        "WHERE di.id_dish = ?";

        Connection conn = null;
        try {
            conn = DBconnection.getDBConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(dishQuery)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    dish = new Dish();
                    dish.setId(rs.getInt("id"));
                    dish.setName(rs.getString("name"));
                    dish.setDishType(DishTypeEnum.valueOf(rs.getString("dishType")));

                    // CORRECTION PostgreSQL: Utiliser getBigDecimal() pour les types NUMERIC
                    BigDecimal price = rs.getBigDecimal("price");
                    if (price != null) {
                        dish.setPrice(price.doubleValue());
                    }
                }
            }
            if (dish != null) {
                List<Ingredients> ingredientsList = new ArrayList<>();

                try (PreparedStatement pstmt = conn.prepareStatement(ingredientsQuery)) {
                    pstmt.setInt(1, id);
                    ResultSet rs = pstmt.executeQuery();

                    while (rs.next()) {
                        Ingredients ingredient = new Ingredients();
                        ingredient.setId(rs.getInt("id"));
                        ingredient.setName(rs.getString("name"));
                        ingredient.setPrice(rs.getDouble("price"));
                        ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                        ingredient.setRequired_quantity(rs.getDouble("quantity_required"));
                        ingredient.setDish(dish);
                        ingredientsList.add(ingredient);
                    }
                }

                dish.setIngredients(ingredientsList);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du plat: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return dish;
    }
    public List<Ingredients> findIngredients(int page, int size) {
        List<Ingredients> ingredients = new ArrayList<>();
        int offset = page * size;

        String query = "SELECT id, name, price, category::text FROM Ingredient ORDER BY id LIMIT ? OFFSET ?";
        Connection conn = null;
        try {
            conn = DBconnection.getDBConnection();

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, size);
                pstmt.setInt(2, offset);

                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    Ingredients ingredient = new Ingredients();
                    ingredient.setId(rs.getInt("id"));
                    ingredient.setName(rs.getString("name"));
                    ingredient.setPrice(rs.getDouble("price"));
                    ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                    ingredients.add(ingredient);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des ingrédients: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return ingredients;
    }

    public List<Ingredients> createIngredients(List<Ingredients> newIngredients) {
        List<Ingredients> createdIngredients = new ArrayList<>();

        String checkQuery = "SELECT COUNT(*) FROM Ingredient WHERE name = ?";
        String insertQuery = "INSERT INTO Ingredient (name, price, category) VALUES (?, ?, ?::ingredient_category) RETURNING id";

        Connection conn = null;
        try {
            conn = DBconnection.getDBConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                for (Ingredients ingredient : newIngredients) {
                    checkStmt.setString(1, ingredient.getName());
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        conn.rollback();
                        throw new DuplicateIngredientException(
                                "L'ingrédient '" + ingredient.getName() + "' existe déjà dans la base de données"
                        );
                    }
                }
            }
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                for (Ingredients ingredient : newIngredients) {
                    insertStmt.setString(1, ingredient.getName());
                    insertStmt.setDouble(2, ingredient.getPrice());
                    insertStmt.setString(3, ingredient.getCategory().name());

                    ResultSet rs = insertStmt.executeQuery();
                    if (rs.next()) {
                        ingredient.setId(rs.getInt("id"));
                        createdIngredients.add(ingredient);
                    }
                }
            }

            conn.commit();
            System.out.println(createdIngredients.size() + " ingrédient(s) créé(s) avec succès");

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Erreur lors de la création des ingrédients: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la création des ingrédients", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return createdIngredients;
    }

    public Dish saveDish(Dish dishToSave) {
        String checkQuery = "SELECT id FROM Dish WHERE id = ?";
        String insertQuery = "INSERT INTO Dish (name, dishType, price) VALUES (?, ?::dish_type, ?) RETURNING id";
        String updateQuery = "UPDATE Dish SET name = ?, dishType = ?::dish_type, price = ? WHERE id = ?";
        String deleteIngredientsQuery = "DELETE FROM dish_ingredients WHERE id_dish = ?";
        String insertIngredientQuery = "INSERT INTO dish_ingredients (id_dish, id_ingredient, unit, quantity_required) VALUES (?, ?, ?::unit_type, ?)";

        Connection conn = null;
        try {
            conn = DBconnection.getDBConnection();
            conn.setAutoCommit(false);
            boolean exists = false;
            if (dishToSave.getId() > 0) {
                try (PreparedStatement pstmt = conn.prepareStatement(checkQuery)) {
                    pstmt.setInt(1, dishToSave.getId());
                    ResultSet rs = pstmt.executeQuery();
                    exists = rs.next();
                }
            }

            if (!exists) {
                try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                    pstmt.setString(1, dishToSave.getName());
                    pstmt.setString(2, dishToSave.getDishType().name());
                    Double price = dishToSave.getPrice();
                    if (price != null && price > 0) {
                        pstmt.setDouble(3, price);
                    } else {
                        pstmt.setNull(3, Types.NUMERIC);
                    }
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        dishToSave.setId(rs.getInt("id"));
                    }
                }
            } else {
                try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                    pstmt.setString(1, dishToSave.getName());
                    pstmt.setString(2, dishToSave.getDishType().name());
                    Double price = dishToSave.getPrice();
                    if (price != null && price > 0) {
                        pstmt.setDouble(3, price);
                    } else {
                        pstmt.setNull(3, Types.NUMERIC);
                    }

                    pstmt.setInt(4, dishToSave.getId());
                    pstmt.executeUpdate();
                }
            }
            try (PreparedStatement pstmt = conn.prepareStatement(deleteIngredientsQuery)) {
                pstmt.setInt(1, dishToSave.getId());
                pstmt.executeUpdate();
            }
            if (dishToSave.getIngredients() != null && !dishToSave.getIngredients().isEmpty()) {
                try (PreparedStatement pstmt = conn.prepareStatement(insertIngredientQuery)) {
                    for (Ingredients ingredient : dishToSave.getIngredients()) {
                        pstmt.setInt(1, dishToSave.getId());
                        pstmt.setInt(2, ingredient.getId());
                        pstmt.setString(3, "Kg");
                        double quantity = ingredient.getRequired_quantity() > 0 ?
                                ingredient.getRequired_quantity() : 1.0;
                        pstmt.setDouble(4, quantity);

                        pstmt.executeUpdate();
                    }
                }
            }

            conn.commit();
            System.out.println("Plat sauvegardé avec succès: " + dishToSave.getName());

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Erreur lors de la sauvegarde du plat: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la sauvegarde du plat", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return dishToSave;
    }

    public List<Ingredients> findIngredientsByCriteria(String ingredientName, CategoryEnum category, String dishName, int page, int size) {
        List<Ingredients> ingredients = new ArrayList<>();
        int offset = page * size;

        StringBuilder query = new StringBuilder(
                "SELECT DISTINCT i.id, i.name, i.price, i.category::text " +
                        "FROM Ingredient i " +
                        "LEFT JOIN dish_ingredients di ON i.id = di.id_ingredient " +
                        "LEFT JOIN Dish d ON di.id_dish = d.id " +
                        "WHERE 1=1"
        );

        List<Object> parameters = new ArrayList<>();
        if (ingredientName != null && !ingredientName.trim().isEmpty()) {
            query.append(" AND i.name ILIKE ?");
            parameters.add("%" + ingredientName + "%");
        }

        if (category != null) {
            query.append(" AND i.category = ?::ingredient_category");
            parameters.add(category.name());
        }

        if (dishName != null && !dishName.trim().isEmpty()) {
            query.append(" AND d.name ILIKE ?");
            parameters.add("%" + dishName + "%");
        }

        query.append(" ORDER BY i.id LIMIT ? OFFSET ?");

        Connection conn = null;
        try {
            conn = DBconnection.getDBConnection();

            try (PreparedStatement pstmt = conn.prepareStatement(query.toString())) {

                int paramIndex = 1;
                for (Object param : parameters) {
                    pstmt.setObject(paramIndex++, param);
                }
                pstmt.setInt(paramIndex++, size);
                pstmt.setInt(paramIndex, offset);

                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    Ingredients ingredient = new Ingredients();
                    ingredient.setId(rs.getInt("id"));
                    ingredient.setName(rs.getString("name"));
                    ingredient.setPrice(rs.getDouble("price"));
                    ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                    ingredients.add(ingredient);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des ingrédients: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return ingredients;
    }

    public List<Ingredients> findIngredientsByCriteria(String ingredientName) {
        return findIngredientsByCriteria(ingredientName, null, null, 0, 10);
    }
}