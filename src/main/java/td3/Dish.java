package td3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dish {

    private int id;
    private String name;
    private DishTypeEnum dishType;
    private List<Ingredients> ingredients;
    private Double price; // Prix de vente (peut être NULL)

    public Dish() {
        this.ingredients = new ArrayList<>();
    }

    public Dish(int id, String name, DishTypeEnum dishType) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.ingredients = new ArrayList<>();
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public List<Ingredients> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredients> ingredients) {
        this.ingredients = ingredients;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Ajoute un ingrédient au plat
     * @param ingredient L'ingrédient à ajouter
     */
    public void addIngredients(Ingredients ingredient) {
        if (this.ingredients == null) {
            this.ingredients = new ArrayList<>();
        }
        this.ingredients.add(ingredient);
    }

    /**
     * Calcule le coût total du plat basé sur les ingrédients et leurs quantités requises
     * @return Le coût total du plat
     * @throws IllegalArgumentException si une quantité requise est invalide
     */
    public double getDishCost() {
        if (this.ingredients == null || this.ingredients.isEmpty()) {
            return 0.0;
        }

        double totalCost = 0.0;
        for (Ingredients ingredient : this.ingredients) {
            if (ingredient.getRequired_quantity() <= 0) {
                throw new IllegalArgumentException(
                        "La quantité nécessaire pour l'ingrédient " + ingredient.getName() +
                                " est inconnue ou nulle."
                );
            }
            totalCost += ingredient.getPrice() * ingredient.getRequired_quantity();
        }
        return totalCost;
    }

    /**
     * Calcule la marge brute du plat (prix de vente - coût des ingrédients)
     * @return La marge brute
     * @throws IllegalStateException si le prix de vente est NULL
     */
    public double getGrossMargin() {
        // Vérifier si le prix de vente est défini
        if (this.price == null) {
            throw new IllegalStateException(
                    "Impossible de calculer la marge brute pour le plat '" + this.name +
                            "' : le prix de vente n'est pas défini (NULL)."
            );
        }

        double cost = getDishCost(); // Utilise la méthode existante qui peut lever une exception
        return this.price - cost;
    }

    /**
     * Calcule le prix total du plat (somme des prix des ingrédients sans quantité)
     * @deprecated Utilisez getDishCost() à la place
     * @return Le prix total
     */
    @Deprecated
    public double getDishPrice() {
        if (this.ingredients == null) {
            return 0.0;
        }
        double totalPrice = 0.0;
        for (Ingredients ingredient : this.ingredients) {
            if (ingredient != null) {
                totalPrice += ingredient.getPrice();
            }
        }
        return totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dish)) return false;
        Dish dish = (Dish) o;
        return id == dish.id &&
                Objects.equals(name, dish.name) &&
                dishType == dish.dishType &&
                Objects.equals(ingredients, dish.ingredients) &&
                Objects.equals(price, dish.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dishType, ingredients, price);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Dish{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", dishType=").append(dishType);
        sb.append(", price=").append(price != null ? price : "NULL");
        sb.append(", ingredients=");

        if (ingredients != null && !ingredients.isEmpty()) {
            sb.append("[");
            for (int i = 0; i < ingredients.size(); i++) {
                if (i > 0) sb.append(", ");
                Ingredients ing = ingredients.get(i);
                sb.append(ing.getName())
                        .append("(")
                        .append(ing.getRequired_quantity())
                        .append(")");
            }
            sb.append("]");
        } else {
            sb.append("[]");
        }

        sb.append('}');
        return sb.toString();
    }
}