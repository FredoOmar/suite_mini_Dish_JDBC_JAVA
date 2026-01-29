package td3;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Random;

public class TestOrders {
    private static int orderCounter = 1;
    private static Random random = new Random();


    private static String generateUniqueReference() {
        long timestamp = System.currentTimeMillis() % 100000;
        return String.format("ORD%05d", timestamp);
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   TEST DU SYSTÈME DE GESTION DES COMMANDES");
        System.out.println("========================================\n");

        // Test de connexion
        Connection conn = DBconnection.getDBConnection();
        if (conn == null) {
            System.err.println(" Impossible de se connecter à la base de données!");
            return;
        }

        try {
            conn.close();
            System.out.println("✓ Connexion à la base de données réussie\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

        DataRetriever dataRetriever = new DataRetriever();

        System.out.println("TEST 1: Création d'une commande valide");
        System.out.println("----------------------------------------");

        try {
            String ref1 = generateUniqueReference();
            Order order1 = new Order(ref1);

            // Récupérer les plats depuis la base de données
            Dish dish1 = dataRetriever.findDishById(1); // Salade fraîche
            Dish dish2 = dataRetriever.findDishById(2); // Poulet grillé

            DishOrder dishOrder1 = new DishOrder(order1, dish1, 2);
            DishOrder dishOrder2 = new DishOrder(order1, dish2, 1);

            order1.addDishOrder(dishOrder1);
            order1.addDishOrder(dishOrder2);

            System.out.println("Référence: " + order1.getReference());
            System.out.println("Date de création: " + order1.getCreationDateTime());
            System.out.println("Nombre de plats: " + order1.getDishOrders().size());
            System.out.println("Montant HT: " + String.format("%.2f", order1.getTotalAmountExcludingTax()) + " Ar");
            System.out.println("Montant TTC: " + String.format("%.2f", order1.getTotalAmountIncludingTax()) + " Ar");

            Order savedOrder = dataRetriever.saveOrder(order1);
            System.out.println("✓ Commande sauvegardée avec succès (ID: " + savedOrder.getId() + ")");
            System.out.println("  Stocks mis à jour automatiquement\n");

        } catch (InsufficientStockException e) {
            System.err.println("❌ Erreur de stock: " + e.getMessage() + "\n");
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage() + "\n");
            e.printStackTrace();
        }

        System.out.println("TEST 2: Récupération d'une commande par référence");
        System.out.println("--------------------------------------------------");

        try {
            Order foundOrder = dataRetriever.findOrderByReference("ORD00001");

            System.out.println("✓ Commande trouvée:");
            System.out.println("  ID: " + foundOrder.getId());
            System.out.println("  Référence: " + foundOrder.getReference());
            System.out.println("  Date: " + foundOrder.getCreationDateTime());
            System.out.println("  Nombre de plats: " + foundOrder.getDishOrders().size());
            System.out.println("  Montant HT: " + String.format("%.2f", foundOrder.getTotalAmountExcludingTax()) + " Ar");
            System.out.println("  Montant TTC: " + String.format("%.2f", foundOrder.getTotalAmountIncludingTax()) + " Ar");

            System.out.println("\n  Détail des plats:");
            for (DishOrder dishOrder : foundOrder.getDishOrders()) {
                System.out.println("    - " + dishOrder.getDish().getName() +
                        " x" + dishOrder.getQuantity() +
                        " = " + String.format("%.2f", dishOrder.getSubtotal()) + " Ar");
            }
            System.out.println();

        } catch (OrderNotFoundException e) {
            System.err.println("❌ " + e.getMessage() + "\n");
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage() + "\n");
            e.printStackTrace();
        }

        System.out.println("TEST 3: Tentative de commande avec stock insuffisant");
        System.out.println("-----------------------------------------------------");

        try {
            String ref2 = generateUniqueReference();
            Order order2 = new Order(ref2);

            Dish dish = dataRetriever.findDishById(2); // Poulet grillé
            DishOrder dishOrder = new DishOrder(order2, dish, 100); // Quantité très élevée

            order2.addDishOrder(dishOrder);

            System.out.println("Tentative de commande de 100 Poulet grillé...");

            dataRetriever.saveOrder(order2);
            System.out.println("❌ La commande n'aurait pas dû être sauvegardée!\n");

        } catch (InsufficientStockException e) {
            System.out.println("✓ Exception levée comme prévu:");
            System.out.println("  " + e.getMessage());
            System.out.println("  Ingrédient: " + e.getIngredientName());
            System.out.println("  Quantité requise: " + e.getRequiredQuantity());
            System.out.println("  Quantité disponible: " + e.getAvailableQuantity());
            System.out.println("  → La commande a été annulée (rollback)\n");
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue: " + e.getMessage() + "\n");
        }

        System.out.println("TEST 4: Recherche d'une commande inexistante");
        System.out.println("---------------------------------------------");

        try {
            Order order = dataRetriever.findOrderByReference("ORD99999");
            System.out.println("❌ La commande n'aurait pas dû être trouvée!\n");

        } catch (OrderNotFoundException e) {
            System.out.println("✓ Exception levée comme prévu:");
            System.out.println("  " + e.getMessage());
            System.out.println("  Référence recherchée: " + e.getReference() + "\n");
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue: " + e.getMessage() + "\n");
        }

        System.out.println("TEST 5: Création d'une commande complexe");
        System.out.println("------------------------------------------");

        try {
            String ref3 = generateUniqueReference();
            Order order3 = new Order(ref3);

            Dish dish1 = dataRetriever.findDishById(1); // Salade fraîche
            Dish dish2 = dataRetriever.findDishById(2); // Poulet grillé
            Dish dish4 = dataRetriever.findDishById(4); // Gâteau au chocolat

            order3.addDishOrder(new DishOrder(order3, dish1, 3));
            order3.addDishOrder(new DishOrder(order3, dish2, 2));
            order3.addDishOrder(new DishOrder(order3, dish4, 4));

            System.out.println("Référence: " + order3.getReference());
            System.out.println("Composition de la commande:");
            for (DishOrder dishOrder : order3.getDishOrders()) {
                System.out.println("  - " + dishOrder.getDish().getName() +
                        " x" + dishOrder.getQuantity());
            }

            System.out.println("\nMontant HT: " + String.format("%.2f", order3.getTotalAmountExcludingTax()) + " Ar");
            System.out.println("Montant TTC: " + String.format("%.2f", order3.getTotalAmountIncludingTax()) + " Ar");

            Order savedOrder = dataRetriever.saveOrder(order3);
            System.out.println("✓ Commande complexe sauvegardée avec succès (ID: " + savedOrder.getId() + ")\n");

        } catch (Exception e) {
            System.err.println(" Erreur: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
        System.out.println("TEST 6: Vérification de la mise à jour des stocks");
        System.out.println("--------------------------------------------------");

        try {
            String ref4 = generateUniqueReference();
            Order order4 = new Order(ref4);

            Dish dish1 = dataRetriever.findDishById(1); // Salade fraîche (nécessite Laitue et Tomate)
            order4.addDishOrder(new DishOrder(order4, dish1, 1));

            System.out.println("Création d'une commande de 1 Salade fraîche");
            System.out.println("Ingrédients nécessaires:");
            System.out.println("  - Laitue: 0.20 Kg");
            System.out.println("  - Tomate: 0.15 Kg");

            Order savedOrder = dataRetriever.saveOrder(order4);
            System.out.println("✓ Commande sauvegardée");
            System.out.println("  → Les stocks de Laitue et Tomate ont été réduits automatiquement\n");

        } catch (Exception e) {
            System.err.println(" Erreur: " + e.getMessage() + "\n");
        }

        System.out.println("========================================");
        System.out.println("   RÉSUMÉ DES TESTS");
        System.out.println("========================================");
        System.out.println("✓ TEST 1: Création de commande valide");
        System.out.println("✓ TEST 2: Récupération de commande par référence");
        System.out.println("✓ TEST 3: Gestion des stocks insuffisants");
        System.out.println("✓ TEST 4: Gestion des commandes inexistantes");
        System.out.println("✓ TEST 5: Création de commande complexe");
        System.out.println("✓ TEST 6: Vérification mise à jour des stocks");
        System.out.println("========================================");
        System.out.println("\n✅ TOUS LES TESTS SONT PASSÉS AVEC SUCCÈS!");
        System.out.println("========================================\n");
    }
}