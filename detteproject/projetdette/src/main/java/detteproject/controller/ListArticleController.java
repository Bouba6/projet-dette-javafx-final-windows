package detteproject.controller;

import detteproject.App;
import detteproject.Repository.jpa.RepositorieJpaArticle;
import detteproject.core.UserConnected;
import detteproject.core.Factory.Impl.FactoryRepo;
import detteproject.core.Factory.Impl.FactoryService;
import detteproject.data.entities.Article;
import detteproject.data.entities.User;
import detteproject.services.ArticleService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ListArticleController extends AdminController {

    @FXML
    private TableView<Article> articleTableView;

    @FXML
    private Label Labelnom;

    @FXML
    private Label Labelmail;

    @FXML
    private TableColumn<Article, String> libelleColumn;

    @FXML
    private TableColumn<Article, Double> prixColumn;

    @FXML
    private TableColumn<Article, Double> qteStockColumn;

    @FXML
    private TableColumn<Article, Void> actionsColumn; // Nouvelle colonne pour les boutons "Modifier"

    @FXML
    private ComboBox<String> filterComboBox; // ComboBox for filtering options

    private ArticleService articleService;
    private ObservableList<Article> allArticles; // List of all articles

    @FXML
    public void initialize() {
        User user = UserConnected.getUserConnected();
        String nom = user.getLogin();
        String mail = user.getEmail();
        Labelnom.setText(nom);
        Labelmail.setText(mail);

        // Configure the table columns
        libelleColumn.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        qteStockColumn.setCellValueFactory(new PropertyValueFactory<>("qteStock"));

        FactoryRepo<Article> articleFactory = new FactoryRepo<>(Article.class);
        RepositorieJpaArticle articleRepository = (RepositorieJpaArticle) articleFactory.createRepository();
        FactoryService<Article> factoryService = new FactoryService<>(Article.class, articleRepository);
        articleService = (ArticleService) factoryService.createService();

        // Load articles into the table
        loadArticles();

        // Set up ComboBox with filter options
        filterComboBox.getItems().addAll("All", "Available");
        filterComboBox.getSelectionModel().select("All"); // Default selection
        filterComboBox.setOnAction(event -> handleFilterSelection()); // Add action listener

        // Ajouter les boutons "Modifier" à chaque ligne
        addModifyButtonToTable();
    }

    private void loadArticles() {
        List<Article> articles = articleService.show();
        allArticles = FXCollections.observableArrayList(articles);
        articleTableView.setItems(allArticles); // Show all articles by default
    }

    private void addModifyButtonToTable() {
        // Crée une cellule avec un bouton "Modifier" pour chaque ligne
        Callback<TableColumn<Article, Void>, TableCell<Article, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Article, Void> call(final TableColumn<Article, Void> param) {
                final TableCell<Article, Void> cell = new TableCell<>() {

                    private final Button modifyButton = new Button("Modifier");

                    {
                        modifyButton.setStyle(
                                "-fx-background-color:  #111827; -fx-text-fill: #fff;-fx-border-radius: 5px; -fx-border-color: #000000; -fx-border-width: 1; -fx-padding: 10; -fx-font-size: 14; -fx-font-weight: bold;");
                        modifyButton.setOnAction(event -> {
                            Article article = getTableView().getItems().get(getIndex());
                            openModifyQuantityDialog(article);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(modifyButton);
                            setAlignment(Pos.CENTER);
                        }
                    }
                };
                return cell;
            }
        };

        actionsColumn.setCellFactory(cellFactory);
    }

    private void openModifyQuantityDialog(Article article) {
        // Crée une boîte de dialogue pour saisir la nouvelle quantité
        TextInputDialog dialog = new TextInputDialog(String.valueOf(article.getQteStock()));
        dialog.setTitle("Modifier la Quantité");
        dialog.setHeaderText("Modifier la Quantité en Stock pour : " + article.getLibelle());
        dialog.setContentText("Veuillez entrer la nouvelle quantité :");

        // Affiche la boîte de dialogue et attend la saisie de l'utilisateur
        Optional<String> result = dialog.showAndWait();

        // Si l'utilisateur a saisi une nouvelle quantité, on la traite
        result.ifPresent(quantityString -> {
            try {
                double newQuantity = Double.parseDouble(quantityString);

                // Vérifier que la quantité est positive
                if (newQuantity >= 0) {
                    // Met à jour la quantité dans l'article
                    article.setQteStock(newQuantity);

                    // Met à jour l'article dans la base de données
                    articleService.update(article);

                    // Rafraîchir la table pour refléter les modifications
                    articleTableView.refresh();

                    // Affiche un message de succès
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Succès");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("La quantité a été mise à jour avec succès !");
                    successAlert.showAndWait();
                } else {
                    // Affiche un message d'erreur si la quantité est invalide
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erreur");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Veuillez entrer une quantité valide (supérieure ou égale à 0).");
                    errorAlert.showAndWait();
                }
            } catch (NumberFormatException e) {
                // Affiche un message d'erreur si l'entrée n'est pas un nombre valide
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erreur");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Veuillez entrer une valeur numérique valide.");
                errorAlert.showAndWait();
            }
        });
    }

    @FXML
    private void handleFilterSelection() {
        String selectedFilter = filterComboBox.getSelectionModel().getSelectedItem();
        ObservableList<Article> filteredArticles = FXCollections.observableArrayList();

        // Apply filter based on the selection
        if ("Available".equals(selectedFilter)) {
            // Keep only the available articles
            for (Article article : allArticles) {
                if (article.getQteStock() > 0) {
                    filteredArticles.add(article);
                }
            }
            articleTableView.setItems(filteredArticles); // Update the TableView
        } else {
            articleTableView.setItems(allArticles); // Show all articles
        }
    }

    @FXML
    private void handleBack() {
        try {
            App.setRoot("admin");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreateArticle(ActionEvent event) {
        try {
            App.setRoot("createArticle");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
