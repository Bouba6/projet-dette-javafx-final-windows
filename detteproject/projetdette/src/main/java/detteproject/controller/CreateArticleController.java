package detteproject.controller;

import detteproject.Repository.jpa.RepositorieJpaArticle;
import detteproject.Repository.jpa.RepositoryJpaClient;
import detteproject.core.RepositorieArticle;
import detteproject.core.UserConnected;
import detteproject.core.Factory.Impl.FactoryRepo;
import detteproject.core.Factory.Impl.FactoryService;
import detteproject.data.entities.Article;
import detteproject.data.entities.Client;
import detteproject.data.entities.User;
import detteproject.services.ArticleService;
import detteproject.services.ClientService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;

public class CreateArticleController extends AdminController {

    @FXML
    private TextField libelleTextField;

    @FXML
    private TextField prixTextField;

    @FXML
    private TextField qteStockTextField;

    @FXML
    private ArticleService articleService;

    @FXML
    private Label Labelnom;

    @FXML
    private Label Labelmail;

    public void initialize() {
        User user = UserConnected.getUserConnected();
        String nom = user.getLogin();
        String mail = user.getEmail();
        Labelnom.setText(nom);
        Labelmail.setText(mail);
    }

    @FXML
    private void handleAddArticle() {

        String libelle = libelleTextField.getText();
        String prixText = prixTextField.getText();
        String qteStockText = qteStockTextField.getText();

        if (libelle.isEmpty() || prixText.isEmpty() || qteStockText.isEmpty()) {
            showAlert("Tous les champs doivent être remplis !");
            return;
        }

        double prix;
        double qteStock;

        try {
            prix = Double.parseDouble(prixText);
            qteStock = Double.parseDouble(qteStockText);
        } catch (NumberFormatException e) {
            showAlert("Veuillez entrer des valeurs numériques valides pour le prix et la quantité !");
            return;
        }

        // Créer un nouvel article
        Article article = new Article();
        article.setLibelle(libelle);
        article.setPrix(prix);
        article.setQteStock(qteStock);

        FactoryRepo<Article> articleFactory = new FactoryRepo<>(Article.class);
        RepositorieJpaArticle articleRepository = (RepositorieJpaArticle) articleFactory.createRepository();

        // Logique pour sauvegarder l'article dans la base de données (ajouter votre
        // logique ici)
        FactoryService<Article> factoryService = new FactoryService<>(Article.class, articleRepository);
        articleService = (ArticleService) factoryService.createService();

        articleService.save(article);
        // Réinitialiser les champs après l'ajout
        resetFields();

        // Afficher une notification de succès
        showAlert("Article ajouté avec succès !");
    }

    private void resetFields() {
        libelleTextField.clear();
        prixTextField.clear();
        qteStockTextField.clear();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}