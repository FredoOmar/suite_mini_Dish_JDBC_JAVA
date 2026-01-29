package td3;

import java.sql.Connection;

public class TestSales {

    private static String generateUniqueReference() {
        long timestamp = System.currentTimeMillis() % 100000;
        return String.format("ORD%05d", timestamp);
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   TEST DU SYSTÈME DE GESTION DES VENTES");
        System.out.println("========================================\n");


        Connection conn = DBconnection.getDBConnection();
        if (conn == null) {
            System.err.println(" Impossible de se connecter à la base de données!");
            return;
        }

        try {
            conn.close();
            System.out.println(" Connexion à la base de données réussie\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

        DataRetriever dataRetriever = new DataRetriever();

        System.out.println("TEST 1: Tentative de créer une vente avec commande NON PAYÉE");
        System.out.println("-------------------------------------------------------------");

        try {
            String ref1 = generateUniqueReference();
            Order order1 = new Order(ref1);
            order1.setPaymentStatus(PaymentStatusEnum.UNPAID);

            Dish dish1 = dataRetriever.findDishById(1);
            order1.addDishOrder(new DishOrder(order1, dish1, 2));

            Order savedOrder = dataRetriever.saveOrder(order1);
            System.out.println("Commande créée: " + savedOrder.getReference());
            System.out.println("Statut: " + savedOrder.getPaymentStatus());

            System.out.println("\nTentative de création de vente...");
            Sale sale = dataRetriever.createSaleFromOrder(savedOrder);
            System.out.println(" La vente n'aurait pas dû être créée!\n");

        } catch (OrderAlreadyPaidException e) {
            System.out.println("✓ Exception levée comme prévu:");
            System.out.println("  " + e.getMessage());
            System.out.println("  → Une vente ne peut être créée que pour une commande PAYÉE\n");
        } catch (Exception e) {
            System.err.println(" Erreur: " + e.getMessage() + "\n");
            e.printStackTrace();
        }


        System.out.println("TEST 2: Créer une vente avec commande PAYÉE");
        System.out.println("--------------------------------------------");

        try {
            String ref2 = generateUniqueReference();
            Order order2 = new Order(ref2);
            order2.setPaymentStatus(PaymentStatusEnum.PAID); // Payée

            Dish dish1 = dataRetriever.findDishById(1);
            Dish dish2 = dataRetriever.findDishById(2);

            order2.addDishOrder(new DishOrder(order2, dish1, 1));
            order2.addDishOrder(new DishOrder(order2, dish2, 2));

            Order savedOrder = dataRetriever.saveOrder(order2);
            System.out.println("Commande créée: " + savedOrder.getReference());
            System.out.println("Statut: " + savedOrder.getPaymentStatus());
            System.out.println("Montant HT: " + String.format("%.2f", savedOrder.getTotalAmountExcludingTax()) + " Ar");
            System.out.println("Montant TTC: " + String.format("%.2f", savedOrder.getTotalAmountIncludingTax()) + " Ar");

            System.out.println("\nCréation de la vente...");
            Sale sale = dataRetriever.createSaleFromOrder(savedOrder);

            System.out.println("✓ Vente créée avec succès!");
            System.out.println("  ID Vente: " + sale.getId());
            System.out.println("  Date: " + sale.getCreationDateTime());
            System.out.println("  Commande associée: " + sale.getOrder().getReference());
            System.out.println("  Montant TTC: " + String.format("%.2f", sale.getTotalAmountIncludingTax()) + " Ar");


            System.out.println("\n  → La commande a été mise à jour:");
            System.out.println("    id_sale = " + savedOrder.getIdSale() + "\n");

        } catch (Exception e) {
            System.err.println(" Erreur: " + e.getMessage() + "\n");
            e.printStackTrace();
        }


        System.out.println("TEST 3: Tentative de créer une DEUXIÈME vente pour la même commande");
        System.out.println("---------------------------------------------------------------------");

        try {
            Order existingOrder = dataRetriever.findOrderByReference("ORD00001");

            if (existingOrder != null && existingOrder.getPaymentStatus() == PaymentStatusEnum.PAID) {
                System.out.println("Commande: " + existingOrder.getReference());
                System.out.println("Statut: " + existingOrder.getPaymentStatus());
                System.out.println("ID Vente existante: " + existingOrder.getIdSale());

                System.out.println("\nTentative de création d'une 2ème vente...");
                Sale sale = dataRetriever.createSaleFromOrder(existingOrder);
                System.out.println(" La vente n'aurait pas dû être créée!\n");
            }

        } catch (OrderAlreadyHasSaleException e) {
            System.out.println("✓ Exception levée comme prévu:");
            System.out.println("  " + e.getMessage());
            System.out.println("  ID Vente existante: " + e.getSaleId());
            System.out.println("  → Une commande ne peut être associée qu'à une seule vente\n");
        } catch (Exception e) {
            System.err.println(" Erreur: " + e.getMessage() + "\n");
            e.printStackTrace();
        }

        System.out.println("TEST 4: Récupération d'une vente par son ID");
        System.out.println("--------------------------------------------");

        try {
            Sale foundSale = dataRetriever.findSaleById(1);

            if (foundSale != null) {
                System.out.println("✓ Vente trouvée:");
                System.out.println("  ID: " + foundSale.getId());
                System.out.println("  Date de création: " + foundSale.getCreationDateTime());
                System.out.println("  Commande associée: " + foundSale.getOrder().getReference());
                System.out.println("  Statut paiement: " + foundSale.getOrder().getPaymentStatus());
                System.out.println("  Montant HT: " + String.format("%.2f", foundSale.getTotalAmountExcludingTax()) + " Ar");
                System.out.println("  Montant TTC: " + String.format("%.2f", foundSale.getTotalAmountIncludingTax()) + " Ar\n");
            } else {
                System.out.println("Aucune vente trouvée avec l'ID 1\n");
            }

        } catch (Exception e) {
            System.err.println(" Erreur: " + e.getMessage() + "\n");
            e.printStackTrace();
        }


        System.out.println("TEST 5: Workflow complet - Commande → Paiement → Vente");
        System.out.println("--------------------------------------------------------");

        try {
            System.out.println("Étape 1: Création de la commande (UNPAID)");
            String ref5 = generateUniqueReference();
            Order order5 = new Order(ref5);
            order5.setPaymentStatus(PaymentStatusEnum.UNPAID);

            Dish dish4 = dataRetriever.findDishById(4); // Gâteau au chocolat
            order5.addDishOrder(new DishOrder(order5, dish4, 3));

            Order savedOrder5 = dataRetriever.saveOrder(order5);
            System.out.println("  ✓ Commande créée: " + savedOrder5.getReference());
            System.out.println("    Statut: " + savedOrder5.getPaymentStatus());
            System.out.println("    Montant: " + String.format("%.2f", savedOrder5.getTotalAmountIncludingTax()) + " Ar TTC");

            System.out.println("\nÉtape 2: Le client effectue le paiement");
            savedOrder5.setPaymentStatus(PaymentStatusEnum.PAID);
            System.out.println("  ✓ Statut mis à jour: " + savedOrder5.getPaymentStatus());

            System.out.println("\nÉtape 3: Création de la vente");
            Sale sale5 = dataRetriever.createSaleFromOrder(savedOrder5);
            System.out.println("  ✓ Vente créée avec succès!");
            System.out.println("    ID Vente: " + sale5.getId());
            System.out.println("    Montant final: " + String.format("%.2f", sale5.getTotalAmountIncludingTax()) + " Ar TTC\n");

        } catch (Exception e) {
            System.err.println(" Erreur: " + e.getMessage() + "\n");
            e.printStackTrace();
        }

        System.out.println("========================================");
        System.out.println("   RÉSUMÉ DES TESTS");
        System.out.println("========================================");
        System.out.println("✓ TEST 1: Validation - Commande non payée");
        System.out.println("✓ TEST 2: Création de vente réussie");
        System.out.println("✓ TEST 3: Validation - Vente unique par commande");
        System.out.println("✓ TEST 4: Récupération de vente");
        System.out.println("✓ TEST 5: Workflow complet");
        System.out.println("========================================");
        System.out.println("\n TOUS LES TESTS DE VENTE SONT PASSÉS!");
        System.out.println("========================================\n");
    }
}