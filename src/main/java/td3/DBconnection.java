package td3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


    public class DBconnection {
        private static final String URL = "jdbc:postgresql://localhost:5432/dish_normalisation";
        private static final String USER = "dish_normalisation_manager";
        private static final String PASSWORD = "123456";

        public static Connection getDBConnection() {
            Connection connection = null;
            try {

                Class.forName("org.postgresql.Driver");

                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion réussie à PostgreSQL!");

            } catch (ClassNotFoundException e) {
                System.err.println("Driver PostgreSQL non trouvé");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("Erreur de connexion SQL");
                e.printStackTrace();
            }
            return connection;
        }
    }


