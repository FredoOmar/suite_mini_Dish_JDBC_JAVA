package test.test;

import td3.*;

import java.util.List;

public class TestDishCalcul {

        public static void main(String[] args) {
            System.out.println("=== TEST DES NOUVELLES FONCTIONNALITÉS ===\n");

            DataRetriever retriever = new DataRetriever();


            System.out.println("--- Test 1: Calcul du coût des plats (getDishCost) ---");
            testGetDishCost(retriever);

            System.out.println("\n" + "=".repeat(60) + "\n");


            System.out.println("--- Test 2: Calcul de la marge brute (getGrossMargin) ---");
            testGetGrossMargin(retriever);

            System.out.println("\n=== TESTS TERMINÉS ===");
        }


        private static void testGetDishCost(DataRetriever retriever) {
            System.out.println("Plat                    | Coût attendu");
            System.out.println("-".repeat(50));

            // Test pour chaque plat
            int[] dishIds = {1, 2, 3, 4, 5};
            double[] expectedCosts = {250.00, 4500.00, 0.00, 1400.00, 0.00};

            for (int i = 0; i < dishIds.length; i++) {
                int dishId = dishIds[i];
                double expectedCost = expectedCosts[i];

                try {
                    Dish dish = retriever.findDishById(dishId);

                    if (dish != null) {
                        double actualCost = dish.getDishCost();
                        String status = Math.abs(actualCost - expectedCost) < 0.01 ? "✓" : "✗";

                        System.out.printf("%-23s | %.2f %s (attendu: %.2f)%n",
                                dish.getName(),
                                actualCost,
                                status,
                                expectedCost
                        );

                        if (dish.getIngredients() != null && !dish.getIngredients().isEmpty()) {
                            for (Ingredients ing : dish.getIngredients()) {
                                System.out.printf("  - %s: %.2f x %.2f = %.2f%n",
                                        ing.getName(),
                                        ing.getPrice(),
                                        ing.getRequired_quantity(),
                                        ing.getPrice() * ing.getRequired_quantity()
                                );
                            }
                        } else {
                            System.out.println("  (aucun ingrédient)");
                        }
                    } else {
                        System.out.printf("Plat ID %d non trouvé%n", dishId);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.printf("Plat ID %d: Exception - %s%n", dishId, e.getMessage());
                } catch (Exception e) {
                    System.err.printf("Erreur pour le plat ID %d: %s%n", dishId, e.getMessage());
                }
                System.out.println();
            }
        }


        private static void testGetGrossMargin(DataRetriever retriever) {
            System.out.println("Plat                    | Marge attendue");
            System.out.println("-".repeat(60));

            // Test pour chaque plat
            int[] dishIds = {1, 2, 3, 4, 5};
            Object[] expectedResults = {3250.00, 7500.00, "Exception (prix NULL)", 6600.00, "Exception (prix NULL)"};

            for (int i = 0; i < dishIds.length; i++) {
                int dishId = dishIds[i];
                Object expectedResult = expectedResults[i];

                try {
                    Dish dish = retriever.findDishById(dishId);

                    if (dish != null) {
                        try {
                            double margin = dish.getGrossMargin();

                            if (expectedResult instanceof Double) {
                                double expectedMargin = (Double) expectedResult;
                                String status = Math.abs(margin - expectedMargin) < 0.01 ? "✓" : "✗";

                                System.out.printf("%-23s | %.2f %s (attendu: %.2f)%n",
                                        dish.getName(),
                                        margin,
                                        status,
                                        expectedMargin
                                );
                                System.out.printf("  Prix de vente: %.2f | Coût: %.2f%n",
                                        dish.getPrice(),
                                        dish.getDishCost()
                                );
                            } else {
                                System.out.printf("%-23s | %.2f ✗ (attendu: %s)%n",
                                        dish.getName(),
                                        margin,
                                        expectedResult
                                );
                            }
                        } catch (IllegalStateException e) {
                            String status = expectedResult instanceof String &&
                                    ((String)expectedResult).contains("Exception") ? "✓" : "✗";
                            System.out.printf("%-23s | %s Exception (prix NULL) %s%n",
                                    dish.getName(),
                                    "✗",
                                    status
                            );
                            System.out.printf("  Message: %s%n", e.getMessage());
                        }
                    } else {
                        System.out.printf("Plat ID %d non trouvé%n", dishId);
                    }
                } catch (Exception e) {
                    System.err.printf("Erreur pour le plat ID %d: %s%n", dishId, e.getMessage());
                    e.printStackTrace();
                }
                System.out.println();
            }
        }


        public static void testCreateAndSaveDish() {
            System.out.println("\n--- Test 3: Création et sauvegarde d'un plat ---");

            DataRetriever retriever = new DataRetriever();


            Dish newDish = new Dish();
            newDish.setName("Test Plat");
            newDish.setDishType(DishTypeEnum.MAIN);
            newDish.setPrice(5000.0);

            List<Ingredients> ingredients = retriever.findIngredients(0, 2);

            if (!ingredients.isEmpty()) {
                for (Ingredients ing : ingredients) {
                    ing.setRequired_quantity(0.5);
                    newDish.addIngredients(ing);
                }


                Dish savedDish = retriever.saveDish(newDish);
                System.out.printf("Plat sauvegardé: ID=%d, Nom=%s%n", savedDish.getId(), savedDish.getName());

                // Recalculer et afficher
                System.out.printf("Coût: %.2f%n", savedDish.getDishCost());
                System.out.printf("Marge: %.2f%n", savedDish.getGrossMargin());
            }
        }
    }

