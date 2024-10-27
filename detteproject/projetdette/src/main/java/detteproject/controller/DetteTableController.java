package detteproject.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import detteproject.App;
import detteproject.Repository.List.DetteRepository;
import detteproject.Repository.jpa.RepositorieJpaDette;
import detteproject.Repository.jpa.RepositoryJpaClient;
import detteproject.State.EtatDette;
import detteproject.State.Role;
import detteproject.core.UserConnected;
import detteproject.core.Factory.Impl.FactoryRepo;
import detteproject.core.Factory.Impl.FactoryService;
import detteproject.data.entities.Client;
import detteproject.data.entities.Dette;
import detteproject.data.entities.User;
import detteproject.services.ClientService;
import detteproject.services.DetteService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class DetteTableController extends BoutiquierController {

    @FXML
    private TableView<Dette> detteTableView;

    @FXML
    private Label Labelnom;

    @FXML
    private Label Labelmail;

    @FXML
    private TableColumn<Dette, String> debtorNameColumn;

    @FXML
    private TableColumn<Dette, Double> amountColumn;

    @FXML
    private TableColumn<Dette, LocalDate> dueDateColumn;

    @FXML
    private TableColumn<Dette, LocalDateTime> createat;

    @FXML
    private TableColumn<Dette, String> etatColumn;

    @FXML
    private ChoiceBox<String> statusChoiceBox;

    @FXML
    private TextField statusFilterTextField;

    @FXML
    private Button searchButton;

    @FXML
    private ChoiceBox<String> roleChoiceBox;

    private ObservableList<Dette> detteListEtat;
    private ObservableList<Dette> detteList;
    private DetteService detteService;

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

        roleChoiceBox.setItems(FXCollections.observableArrayList("ENCOURS", "VALIDER", "ANNULER"));

        // Initialize dette service
        FactoryRepo<Dette> detteFactoryRepo = new FactoryRepo<>(Dette.class);
        RepositorieJpaDette detteRepository = (RepositorieJpaDette) detteFactoryRepo.createRepository();

        FactoryService<Dette> factory = new FactoryService(Dette.class, detteRepository);
        detteService = (DetteService) factory.createService();

        // Fetch and set dette data
        loadDetteData();
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
                                .filter(dette -> dette.getEtat().equals(roleEnum))
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

    private void loadDetteData() {
        // Fetch the list of dettes and convert to ObservableList
        System.out.println(detteService.show());
        detteList = FXCollections.observableArrayList(detteService.show());
        detteTableView.setItems(detteList);
    }

    @FXML
    public void handleListDebt() {
        System.out.println("Lister dette");
        // Additional logic for listing debts if necessary
    }

    public void createDette() {
        try {
            App.setRoot("createDette");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
