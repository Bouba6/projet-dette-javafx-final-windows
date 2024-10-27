package detteproject.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.deser.impl.PropertyValue;

import detteproject.Repository.List.DetteRepository;
import detteproject.Repository.jpa.RepositorieJpaArticle;
import detteproject.Repository.jpa.RepositorieJpaDette;
import detteproject.Repository.jpa.RepositoryJpaClient;
import detteproject.State.EtatDette;
import detteproject.core.UserConnected;
import detteproject.core.Factory.Impl.FactoryRepo;
import detteproject.core.Factory.Impl.FactoryService;
import detteproject.data.entities.Article;
import detteproject.data.entities.Client;
import detteproject.data.entities.DetailDette;
import detteproject.data.entities.Dette;
import detteproject.data.entities.Paiement;
import detteproject.data.entities.User;
import detteproject.services.ArticleService;
import detteproject.services.ClientService;
import detteproject.services.DetteService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;

public class DetteEcController extends BoutiquierController {

    @FXML
    private TableView<Dette> detteTableView;

    @FXML
    private TableView<DetailDette> ArticleTableView;

    @FXML
    private TableView<Paiement> PaiementTableView;

    @FXML
    private TableColumn<Paiement, Integer> idPaiement;

    @FXML
    private TableColumn<Paiement, Double> montant;

    @FXML
    private TableColumn<Paiement, LocalDateTime> datePaiement;

    @FXML
    private TableColumn<Dette, String> debtorNameColumn;

    @FXML
    private TableColumn<Dette, Double> amountColumn;

    @FXML
    private TableColumn<Dette, LocalDateTime> createat;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private Label clientNameLabel;

    @FXML
    private TextField idDette;

    @FXML
    private Button searchButton;

    @FXML
    private TableColumn<DetailDette, Integer> id;

    @FXML
    private TableColumn<DetailDette, String> libelle;

    @FXML
    private TableColumn<DetailDette, Double> prix;

    @FXML
    private TableColumn<DetailDette, Integer> quantite;

    @FXML
    private TableColumn<Dette, String> etatColumn;

    @FXML
    private Button searchButton1;

    @FXML
    private ChoiceBox<String> roleChoiceBox;

    private ObservableList<Dette> detteListEtat;

    private ArticleService articleService;

    private ObservableList<Dette> detteList;
    private ObservableList<DetailDette> detailsList;
    private DetteService detteService;
    private ClientService clientService;

    @FXML
    private Label Labelnom;

    @FXML
    private Label Labelmail;

    @FXML
    private TableColumn<Dette, Void> actionsColumn;

    @FXML
    public void initialize() {
        User user = UserConnected.getUserConnected();
        String nom = user.getLogin();
        String mail = user.getEmail();
        Labelnom.setText(nom);
        Labelmail.setText(mail);

        // Initialize columns
        debtorNameColumn.setCellValueFactory(cellData -> {
            Client client = cellData.getValue().getClient();
            return new SimpleStringProperty(client != null ? client.getNom() : "No Client");
        });
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        libelle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getArticle().getLibelle()));
        prix.setCellValueFactory(
                data -> new ReadOnlyObjectWrapper<Double>(data.getValue().getArticle().getPrix()));
        quantite.setCellValueFactory(new PropertyValueFactory<>("qte"));

        createat.setCellValueFactory(new PropertyValueFactory<>("createAt")); // Inherited from AbstractEntity
        etatColumn.setCellValueFactory(cellData -> {
            Dette dette = cellData.getValue();
            String status;
            switch (dette.getEtat().ordinal()) { // Assuming getEtat() returns the ordinal value
                case 0:
                    status = "ENCOURS";
                    break;
                case 1:
                    status = "VALIDER";
                    break;
                case 2:
                    status = "ANNULER";
                    break;
                default:
                    status = "UNKNOWN";
                    break;
            }
            return new SimpleStringProperty(status);
        });

        idPaiement.setCellValueFactory(new PropertyValueFactory<>("id"));
        montant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        datePaiement.setCellValueFactory(new PropertyValueFactory<>("date"));
        // Initialize dette service
        FactoryRepo<Dette> detteFactoryRepo = new FactoryRepo<>(Dette.class);
        RepositorieJpaDette detteRepository = (RepositorieJpaDette) detteFactoryRepo.createRepository();
        FactoryRepo<Client> clientFactoryRepo = new FactoryRepo<>(Client.class);
        RepositoryJpaClient clientRepository = (RepositoryJpaClient) clientFactoryRepo.createRepository();

        FactoryRepo<Article> articleFactory = new FactoryRepo<>(Article.class);
        RepositorieJpaArticle articleRepository = (RepositorieJpaArticle) articleFactory.createRepository();
        FactoryService<Article> factoryService = new FactoryService<>(Article.class, articleRepository);
        articleService = (ArticleService) factoryService.createService();

        FactoryService<Dette> factory = new FactoryService(Dette.class, detteRepository);
        FactoryService<Client> factory1 = new FactoryService(Client.class, clientRepository);
        detteService = (DetteService) factory.createService();
        clientService = (ClientService) factory1.createService();

        addActionButtonsToTable();

        // Fetch and set dette data
        clientNameLabel.setVisible(false);
    }

    Client client;

    @FXML
    private void handleSearchClient() {
        String phoneNumber = phoneNumberField.getText();
        client = clientService.find(phoneNumber);

        if (client != null) {
            clientNameLabel.setText("Client: " + client.getNom());
            clientNameLabel.setVisible(true);
            loadDetteData(client);
        } else {
            clientNameLabel.setText("Client not found");
            clientNameLabel.setVisible(true);
            detteTableView.getItems().clear(); // Clear the table if the client is not found
        }
    }

    @FXML
    private void showArticle() {
        String id = idDette.getText();
        if (client != null) {
            try {
                List<DetailDette> details = detteService
                        .findArtInDet(detteService.getById(Integer.parseInt(id), client));
                detailsList = FXCollections.observableArrayList(details);
                ArticleTableView.setItems(detailsList);
                List<Paiement> paiements = detteService.ListDetPai(detteService.getById(Integer.parseInt(id), client));
                ObservableList<Paiement> paiementList = FXCollections.observableArrayList(paiements);
                PaiementTableView.setItems(paiementList);
            } catch (Exception e) {
                System.out.println("Article not found");
            }
        }
    }

    @FXML
    private void handleSearchByEtat() {
        String etat = roleChoiceBox.getValue(); // Get the selected value from the ChoiceBox
        if (etat != null) {
            try {
                EtatDette roleEnum = EtatDette.valueOf(etat);
                // Filter the existing detteList based on the selected etat
                ObservableList<Dette> filteredList = FXCollections.observableArrayList(
                        detteList.stream()
                                .filter(dette -> dette.getEtat() == roleEnum) // Filter by etat
                                .toList());
                detteTableView.setItems(filteredList); // Update the table with filtered data
                System.out.println(filteredList.size() + " dettes found with etat: " + etat);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid etat selected: " + etat);
            }
        } else {
            System.out.println("Please select a valid etat.");
        }
    }

    private void addActionButtonsToTable() {
        actionsColumn.setCellFactory(column -> new TableCell<Dette, Void>() {
            private final Button validerButton = new Button("Valider");
            private final Button annulerButton = new Button("Annuler");

            {
                validerButton.setStyle(
                        "-fx-background-color:  #1FA055; -fx-text-fill: #fff;-fx-border-radius: 5px;-fx-padding: 8; -fx-font-size: 10; -fx-font-weight: bold;");
                validerButton.setOnAction(event -> {
                    Dette dette = getTableView().getItems().get(getIndex());
                    handleValiderAction(dette);
                });

                annulerButton.setStyle(
                        "-fx-background-color:  #FF0000; -fx-text-fill: #fff;-fx-border-radius: 5px;-fx-padding: 8; -fx-font-size: 10; -fx-font-weight: bold;");
                annulerButton.setOnAction(event -> {
                    Dette dette = getTableView().getItems().get(getIndex());
                    handleAnnulerAction(dette);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Dette dette = getTableView().getItems().get(getIndex());
                    if (dette.getEtat() == EtatDette.ENCOURS) {
                        setGraphic(new HBox(5, validerButton, annulerButton)); // Display both buttons if status is
                        setAlignment(Pos.CENTER); // "ENCOURS"
                    } else {
                        setGraphic(null); // Hide buttons if status is not "ENCOURS"
                        setAlignment(Pos.CENTER);
                    }
                }
            }
        });
    }

    private void handleValiderAction(Dette dette) {
        // Update the status of the dette to "VALIDER"
        dette.setEtat(EtatDette.VALIDER);
        List<DetailDette> details = dette.getDetails();

        for (DetailDette detail : details) {
            detail.getArticle().setQteStock(detail.getArticle().getQteStock() - detail.getQte());
            articleService.update(detail.getArticle());
        }
        detteService.update(dette); // Persist the change to the database or repository
        detteTableView.refresh(); // Refresh the TableView to reflect the changes
    }

    private void handleAnnulerAction(Dette dette) {
        // Update the status of the dette to "ANNULER"
        dette.setEtat(EtatDette.ANNULER);
        detteService.update(dette); // Persist the change to the database or repository
        detteTableView.refresh(); // Refresh the TableView to reflect the changes
    }

    private void loadDetteData(Client client) {
        // Fetch the list of dettes and convert to ObservableList
        detteList = FXCollections.observableArrayList(detteService.ListDetEc(client));
        detteTableView.setItems(detteList);
    }

    @FXML
    public void handleListDebt() {
        System.out.println("Lister dette");
        // Additional logic for listing debts if necessary
    }
}
