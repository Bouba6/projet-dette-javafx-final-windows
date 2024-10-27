package detteproject.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import detteproject.App;
import detteproject.Repository.List.DetteRepository;
import detteproject.Repository.jpa.RepositorieJpaDette;
import detteproject.Repository.jpa.RepositoryJpaClient;
import detteproject.State.EtatDette;
import detteproject.core.UserConnected;
import detteproject.core.Factory.Impl.FactoryRepo;
import detteproject.core.Factory.Impl.FactoryService;
import detteproject.data.entities.Client;
import detteproject.data.entities.DetailDette;
import detteproject.data.entities.Dette;
import detteproject.data.entities.Paiement;
import detteproject.data.entities.User;
import detteproject.services.ClientService;
import detteproject.services.DetteService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;

public class DetteListClient extends ClientController {

    @FXML
    private TableView<Dette> detteTableView;

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
    private TableColumn<Dette, LocalDate> dueDateColumn;

    @FXML
    private TableColumn<Dette, LocalDateTime> createat;

    @FXML
    private TableColumn<DetailDette, Integer> id;

    @FXML
    private TableColumn<DetailDette, String> libelle;
    @FXML
    private TableView<DetailDette> ArticleTableView;

    @FXML
    private TableColumn<DetailDette, Double> prix;

    @FXML
    private TableColumn<DetailDette, Integer> quantite;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField idDette;

    @FXML
    private Label clientNameLabel;

    @FXML
    private TableColumn<Dette, String> etatColumn;

    @FXML
    private ComboBox<String> etatComboBox;

    @FXML
    private TableColumn<Dette, Void> actionColumn;

    @FXML
    private Label Labelnom;

    @FXML
    private Label Labelmail;

    @FXML
    private Button searchButton;

    private ObservableList<Dette> detteList;
    private DetteService detteService;
    private ClientService clientService;

    @FXML
    private void initialize() {

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

        etatComboBox.setItems(FXCollections.observableArrayList("All", "ENCOURS", "VALIDER", "ANNULER"));
        etatComboBox.setValue("All");
        etatComboBox.valueProperty().addListener((observable, oldValue, newValue) -> filterDette());

        amountColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));

        createat.setCellValueFactory(new PropertyValueFactory<>("createAt"));
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

        }); // Inherited from AbstractEntity

        actionColumn.setCellFactory(column -> {
            return new TableCell<Dette, Void>() {
                private final Button actionButton = new Button("Réactiver");

                {
                    actionButton.setStyle(
                            "-fx-background-color:  #1FA055; -fx-text-fill: #fff;-fx-border-radius: 5px;-fx-border-width: 1; -fx-padding: 7; -fx-font-size: 9; -fx-font-weight: bold;");
                    actionButton.setOnAction(event -> {
                        Dette dette = getTableView().getItems().get(getIndex());
                        changeEtatToEncours(dette);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setAlignment(Pos.CENTER);
                        setGraphic(null);

                    } else {
                        Dette dette = getTableView().getItems().get(getIndex());
                        if (dette.getEtat().ordinal() == 2) { // 2 corresponds to "ANNULER"
                            setGraphic(actionButton);
                            setAlignment(Pos.CENTER);
                        } else {
                            setAlignment(Pos.CENTER);
                            setGraphic(null);
                        }
                    }
                }
            };
        });

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        libelle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getArticle().getLibelle()));
        prix.setCellValueFactory(
                data -> new ReadOnlyObjectWrapper<Double>(data.getValue().getArticle().getPrix()));

        quantite.setCellValueFactory(new PropertyValueFactory<>("qte"));
        idPaiement.setCellValueFactory(new PropertyValueFactory<>("id"));
        montant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        datePaiement.setCellValueFactory(new PropertyValueFactory<>("date"));
        // Initialize dette service
        FactoryRepo<Dette> detteFactoryRepo = new FactoryRepo<>(Dette.class);
        RepositorieJpaDette detteRepository = (RepositorieJpaDette) detteFactoryRepo.createRepository();
        FactoryRepo<Client> clientFactoryRepo = new FactoryRepo<>(Client.class);
        RepositoryJpaClient clientRepository = (RepositoryJpaClient) clientFactoryRepo.createRepository();

        FactoryService<Dette> factory = new FactoryService(Dette.class, detteRepository);
        FactoryService<Client> factory1 = new FactoryService(Client.class, clientRepository);
        detteService = (DetteService) factory.createService();
        clientService = (ClientService) factory1.createService();

        loadDetteData();
    }

    UserConnected userConnected = new UserConnected();
    User user = UserConnected.getUserConnected();
    Client client = user.getClient();

    private void loadDetteData() {

        // Fetch the list of dettes and convert to ObservableList
        detteList = FXCollections.observableArrayList(detteService.ListDetEc(client));
        detteTableView.setItems(detteList);
    }

    @FXML
    private void showArticle() {
        ArticleTableView.getItems().clear();
        PaiementTableView.getItems().clear();
        String id = idDette.getText();
        if (client != null) {
            try {
                List<DetailDette> details = detteService
                        .findArtInDet(detteService.getById(Integer.parseInt(id), client));
                ObservableList<DetailDette> detailsList = FXCollections.observableArrayList(details);
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
    private void filterDette() {
        // Récupérer la valeur sélectionnée dans le ComboBox pour l'état
        String selectedEtat = etatComboBox.getValue();

        // Filtrer la liste des dettes en fonction de l'état sélectionné
        ObservableList<Dette> filteredDetteList;

        if (selectedEtat.equals("All")) {
            // Si "All" est sélectionné, montrer toutes les dettes
            filteredDetteList = FXCollections.observableArrayList(detteService.ListDetEc(client));
        } else {
            // Filtrer la liste des dettes par état
            filteredDetteList = FXCollections.observableArrayList(
                    detteList.filtered(dette -> {
                        // Comparer l'état de chaque dette avec la sélection
                        String detteEtat;
                        switch (dette.getEtat().ordinal()) {
                            case 0:
                                detteEtat = "ENCOURS";
                                break;
                            case 1:
                                detteEtat = "VALIDER";
                                break;
                            case 2:
                                detteEtat = "ANNULER";
                                break;
                            default:
                                detteEtat = "UNKNOWN";
                                break;
                        }
                        return detteEtat.equals(selectedEtat);
                    }));
        }

        // Mettre à jour la TableView avec la liste filtrée
        detteTableView.setItems(filteredDetteList);
    }

    @FXML
    public void handleListDebt() {
        System.out.println("Lister dette");

    }

    @FXML
    public void createDette() {
        try {
            App.setRoot("clientCreateDette");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeEtatToEncours(Dette dette) {
        // Mettre à jour l'état de la dette à "ENCOURS"
        dette.setEtat(EtatDette.ENCOURS); // Assuming Etat is an enum in your Dette class

        // Mettre à jour la dette dans la base de données ou via le service
        detteService.update(dette);

        // Recharger la liste des dettes
        loadDetteData();
    }
}
