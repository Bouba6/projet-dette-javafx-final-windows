package detteproject.controller;

import java.io.IOException;

import detteproject.App;
import javafx.fxml.FXML;

public class AdminController {
    @FXML
    protected void handleCreateClientAccount() {
        try {
            App.setRoot("clientSansCompte");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleCreateUserAccount() {
        // Logic to create a user account
    }

    @FXML
    protected void handleDeactivateUserAccount() {
        System.out.println("3---Désactiver un compte utilisateur");
        // Logic to deactivate a user account
    }

    @FXML
    protected void handleListUsers() {
        try {
            App.setRoot("userTable");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleShowAccountsByRole() {
        try {
            App.setRoot("userRole");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleShowActiveAccounts() {
        System.out.println("6---Afficher les comptes actifs");
        // Logic to show active accounts
    }

    @FXML
    protected void handleArticleInput() {
        try {
            App.setRoot("createArticle");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @FXML
    protected void Deconnexion() {
        try {
            App.setRoot("login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleShowArticles() {
        try {
            App.setRoot("articleList");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleFilterAvailableArticles() {
        System.out.println("9---Filtrer les articles par disponibilité");
        // Logic to filter articles by availability
    }

    @FXML
    protected void handleUpdateStock() {
        System.out.println("10---Mettre à jour la quantité en stock");
        // Logic to update stock quantity
    }
}
