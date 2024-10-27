package detteproject.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Model {

    private static final String URL = "jdbc:mysql://localhost:3306/dette_project_licence3";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Méthode pour obtenir une connexion à la base de données
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // Charger le driver MySQL si nécessaire
        Class.forName("com.mysql.cj.jdbc.Driver");
        // Créer et retourner la connexion
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
