package td3;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.


import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== TEST DE CONNEXION DISH MANAGER ===");

        Connection conn = DBconnection.getDBConnection() ;

        if (conn != null) {
            try {

                System.out.println("\n--- Test 1: Requête de base ---");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT version(), current_user, current_database()");

                if (rs.next()) {
                    System.out.println("Version PostgreSQL : " + rs.getString(1));
                    System.out.println("Utilisateur : " + rs.getString(2));
                    System.out.println("Base de données : " + rs.getString(3));
                }


                System.out.println("\n--- Test 2: Tables disponibles ---");
                rs = stmt.executeQuery(
                        "SELECT table_name FROM information_schema.tables " +
                                "WHERE table_schema = 'public' ORDER BY table_name"
                );

                int tableCount = 0;
                while (rs.next()) {
                    System.out.println("• Table : " + rs.getString(1));
                    tableCount++;
                }
                System.out.println("Total tables : " + tableCount);


                conn.close();
                System.out.println("\n Connexion  avec succès au base de donnee!");

            } catch (Exception e) {
                System.err.println(" Erreur lors des tests : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("\n Impossible de continuer sans connexion à la base de données");
        }

    }

}