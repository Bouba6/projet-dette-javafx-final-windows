package detteproject.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

import detteproject.Repository.List.DetteRepository;
import detteproject.Repository.jpa.RepositorieJpaArticle;
import detteproject.Repository.jpa.RepositorieJpaDette;
import detteproject.Repository.jpa.RepositoryJpaClient;
import detteproject.core.UserConnected;
import detteproject.core.Factory.Impl.FactoryRepo;
import detteproject.core.Factory.Impl.FactoryService;
import detteproject.core.Factory.Impl.FactoryView;
import detteproject.core.Factory.Repo.FactoryUser;
import detteproject.data.entities.Article;
import detteproject.data.entities.Client;
import detteproject.data.entities.Dette;
import detteproject.data.entities.User;
import detteproject.services.ArticleService;
import detteproject.services.ClientService;
import detteproject.services.DetteService;
import detteproject.services.UserService;
import detteproject.views.DetteView;
import detteproject.views.UserView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;

public class ClientValideDetteController {

    @FXML
    private TableView<Dette> detteTableView;

    @FXML
    private TableColumn<Dette, String> debtorNameColumn;

    @FXML
    private TableColumn<Dette, Double> amountColumn;

    @FXML
    private TableColumn<Dette, LocalDate> dueDateColumn;

    @FXML
    private TableColumn<Dette, LocalDateTime> createat;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private Label clientNameLabel;
    @FXML
    private Button searchButton;

    @FXML
    private Label Labelnom;

    @FXML
    private Label Labelmail;

    private DetteView detteView;

    private ObservableList<Dette> detteList;
    private DetteService detteService;
    private ClientService clientService;
    private ArticleService articleService;

    private UserService userService;

    @FXML
    private void initialize() {
        Scanner scanner = new Scanner(System.in);

        // Initialize columns
        debtorNameColumn.setCellValueFactory(cellData -> {
            Client client = cellData.getValue().getClient();
            return new SimpleStringProperty(client != null ? client.getNom() : "No Client");
        });
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("montant"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        createat.setCellValueFactory(new PropertyValueFactory<>("createAt")); // Inherited from AbstractEntity

        // Initialize dette service
        FactoryRepo<Dette> detteFactoryRepo = new FactoryRepo<>(Dette.class);
        RepositorieJpaDette detteRepository = (RepositorieJpaDette) detteFactoryRepo.createRepository();
        FactoryRepo<Client> clientFactoryRepo = new FactoryRepo<>(Client.class);
        RepositoryJpaClient clientRepository = (RepositoryJpaClient) clientFactoryRepo.createRepository();

        FactoryService<Dette> factory = new FactoryService(Dette.class, detteRepository);
        FactoryService<Client> factory1 = new FactoryService(Client.class, clientRepository);

        FactoryRepo<Article> articleFactory = new FactoryRepo<>(Article.class);
        RepositorieJpaArticle articleRepository = (RepositorieJpaArticle) articleFactory.createRepository();
        FactoryService<Article> factoryService = new FactoryService<>(Article.class, articleRepository);
        articleService = (ArticleService) factoryService.createService();

        FactoryView<User> userViewFactory = new FactoryView<>(User.class, clientService, articleService, userService,
                null, detteService);
        UserView userView = (UserView) userViewFactory.createView();

        FactoryUser factoryUser = new FactoryUser();
        userService = factoryUser.getUserService();
        detteService = (DetteService) factory.createService();
        clientService = (ClientService) factory1.createService();

        FactoryView<Dette> detteViewFactory = new FactoryView<>(Dette.class, clientService, articleService,
                userService, userView, detteService);

        DetteView detteView = (DetteView) detteViewFactory.createView();

        loadDetteData(detteView);
    }

    UserConnected userConnected = new UserConnected();
    User user = UserConnected.getUserConnected();
    Client client = user.getClient();

    private void loadDetteData(DetteView detteView) {

        // Fetch the list of dettes and convert to ObservableList
        detteList = FXCollections.observableArrayList(detteView.afficherDettesValides(client));
        detteTableView.setItems(detteList);
    }

    @FXML
    private void handleListDebt() {
        System.out.println("Lister dette");

    }
}
