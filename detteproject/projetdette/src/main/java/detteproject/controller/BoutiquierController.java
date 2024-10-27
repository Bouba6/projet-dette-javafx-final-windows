package detteproject.controller;

import java.io.IOException;

import detteproject.App;
import detteproject.data.entities.Client;
import detteproject.data.entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class BoutiquierController {

    @FXML
    protected void handleCreateClient() {
        try {
            App.setRoot("createClient");
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @FXML
    protected void handleListClients() {
        try {
            App.setRoot("clientTable");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // System.out.println("1---Créer un client");
    // System.out.println("2---Lister Clients");
    // System.out.println("3--Lister les informations d'un client par son numéro");
    // System.out.println("4--Saisie dette");
    // System.out.println("5--Lister dette");
    // System.out.println("6--Faire le paiement d'une dette");
    // System.out.println("7--Lister dette non payée");
    // System.out.println("8--Lister les demandes de dettes en cours");
    // System.out.println("--QUITTER");

    @FXML
    protected void handleClientInfo() {
        try {
            App.setRoot("clientinfoview");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleDebtInput() {
        // Logic for entering debt
        System.out.println("Saisie dette");
    }

    @FXML
    protected void handleListDebt() {
        try {
            App.setRoot("DetteList");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleDebtPayment() {
        try {
            App.setRoot("paiement");
        } catch (Exception e) {
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
    protected void handleUnpaidDebtList() {
        // Logic to list unpaid debts
        System.out.println("Lister dette non payée");
    }

    @FXML
    protected void handleDebtRequests() {
        try {
            App.setRoot("detteEc");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleExit() {
        // Logic to exit the application or go back
        System.out.println("Quitter");
        // System.exit(0); // Uncomment to exit the application
    }
}
