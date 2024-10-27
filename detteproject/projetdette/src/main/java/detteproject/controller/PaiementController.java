package detteproject.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import java.util.List;

import detteproject.Repository.jpa.RepositorieJpaDette;
import detteproject.Repository.jpa.RepositorieJpaPaiement;
import detteproject.Repository.jpa.RepositoryJpaClient;
import detteproject.State.EtatDette;
import detteproject.State.StateDette;
import detteproject.core.UserConnected;
import detteproject.core.Factory.Impl.FactoryRepo;
import detteproject.core.Factory.Impl.FactoryService;
import detteproject.data.entities.Article;
import detteproject.data.entities.Client;
import detteproject.data.entities.Dette;
import detteproject.data.entities.Paiement;
import detteproject.data.entities.User;
import detteproject.services.ClientService;
import detteproject.services.DetteService;
import detteproject.services.PaiementService;

public class PaiementController extends BoutiquierController {

    @FXML
    private TextField detteIdTextField;

    @FXML
    private TableView<Dette> detteTableView;

    @FXML
    private TableColumn<Dette, Integer> idColumn;

    @FXML
    private TableColumn<Dette, String> debtorNameColumn;

    @FXML
    private TableColumn<Dette, Double> amountColumn;

    @FXML
    private TableColumn<Dette, String> etatColumn;

    @FXML
    private Label Labelnom;

    @FXML
    private Label Labelmail;

    @FXML
    private TextField telephoneField;

    @FXML
    private TextField montantTextField;
    private DetteService detteService;
    private ClientService clientService;
    private PaiementService paiementService;

    @FXML
    private Button validerPaiementButton;

    @FXML
    public void initialize() {

        User user = UserConnected.getUserConnected();
        String nom = user.getLogin();
        String mail = user.getEmail();
        Labelnom.setText(nom);
        Labelmail.setText(mail);

        FactoryRepo<Dette> detteFactoryRepo = new FactoryRepo<>(Dette.class);
        RepositorieJpaDette detteRepository = (RepositorieJpaDette) detteFactoryRepo.createRepository();

        FactoryRepo<Client> clientFactoryRepo = new FactoryRepo<>(Client.class);
        RepositoryJpaClient clientRepository = (RepositoryJpaClient) clientFactoryRepo.createRepository();

        FactoryRepo<Paiement> paiementFactoryRepo = new FactoryRepo<>(Paiement.class);
        RepositorieJpaPaiement paiementRepository = (RepositorieJpaPaiement) paiementFactoryRepo.createRepository();

        FactoryService<Dette> factory = new FactoryService(Dette.class, detteRepository);
        FactoryService<Client> factory1 = new FactoryService(Client.class, clientRepository);
        FactoryService<Paiement> factory2 = new FactoryService(Paiement.class, paiementRepository);

        detteService = (DetteService) factory.createService();
        clientService = (ClientService) factory1.createService();
        paiementService = (PaiementService) factory2.createService();

        // Add listener to comboBox for client selection

        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        debtorNameColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClient().getNom()));
        amountColumn
                .setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getMontant()).asObject());
        etatColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEtat().toString()));

    }

    Client client;

    public void search() {
        String tel = telephoneField.getText();
        client = clientService.find(tel);
        if (client == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Client non trouvé");
            alert.setContentText("Le client n'a pas été dans la base de données");
            alert.showAndWait();
        } else {
            populateDetteTable(client);
        }

    }

    private void populateDetteTable(Client client) {
        List<Dette> dettes = detteService.ListDetEc(client); // Implement this method in your service
        detteIdTextField.setDisable(false);
        montantTextField.setDisable(false);
        validerPaiementButton.setDisable(false);
        ObservableList<Dette> detteList = FXCollections.observableArrayList(dettes);
        System.out.println("Number of dettes: " + detteList.size());
        detteTableView.setItems(detteList);

    }

    Dette dette;

    // Méthode pour gérer l'événement de validation du paiement
    @FXML
    private void handleValiderPaiement(ActionEvent event) {

        int detteId = detteIdTextField.getText().isEmpty() ? 0 : Integer.parseInt(detteIdTextField.getText());
        String montant = montantTextField.getText();

        dette = detteService.getById1(detteId);
        Client clie = clientService.find(client.getTelephone());

        if (client == null || detteId == 0 || montant.isEmpty()) {

            showAlert("Erreur", "Veuillez remplir tous les champs !");
            return;
        } else if (dette == null || clie == null) {
            showAlert("Erreur", "Dette non existante !");
            return;
        } else if (dette.getEtat().equals(EtatDette.ENCOURS) || dette.getEtat().equals(EtatDette.ANNULER)) {
            showAlert("Erreur", "Dette en cours de paiement !");
            return;
        } else if (detteService.getById(detteId, client) == null) {
            showAlert("Erreur", "Dette non existante !");
            return;
        }

        // Convertir le montant en nombre pour effectuer des validations supplémentaires
        try {
            Paiement paiement = new Paiement();
            double montantDouble = Double.parseDouble(montant);

            if (dette.getMontantRestant() < montantDouble) {
                throw new NumberFormatException();
            } else if (dette.getMontantRestant() == montantDouble) {
                dette.setState(StateDette.ARCHIVER);

            }
            paiement.setMontant(montantDouble);
            dette.setPaiements(paiement);
            paiementService.save(paiement);
            detteService.update(dette);
            // Ajouter la logique pour enregistrer le paiement de la dette

            showAlert("Succès", "Paiement enregistré avec succès !");
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le montant doit être un nombre valide !");
        }
    }

    // Exemple de méthode pour obtenir la liste des clients (à remplacer par la
    // logique de base de données)

    private Double prompt(Double montant) {
        do {
            showAlert("Erreur", "Le montant n'est pas acceptable !");
        } while (montant < 0 || montant > dette.getMontantRestant());
        return montant;
    }

    // Exemple de méthode pour enregistrer le paiement (à adapter selon votre
    // logique métier)

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
