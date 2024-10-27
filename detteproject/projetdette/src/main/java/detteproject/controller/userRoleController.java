package detteproject.controller;

import detteproject.State.Etat;
import detteproject.State.Role;
import detteproject.core.Factory.Repo.FactoryUser;
import detteproject.data.entities.User;
import detteproject.services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class userRoleController {

    @FXML
    private TextField roleTextField;
    @FXML
    private TableView<User> accountTableView;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private ChoiceBox<String> statusChoiceBox;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    @FXML
    private TableColumn<User, String> statusColumn;

    private ObservableList<User> accountList;
    private UserService userService;

    @FXML
    private void initialize() {
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Initialize user service
        FactoryUser factoryUser = new FactoryUser();
        userService = factoryUser.getUserService();
        // Ensure UserService is implemented to fetch users by role

        // Initial load with empty data
        accountList = FXCollections.observableArrayList();
        accountTableView.setItems(accountList);
    }

    @FXML
    private void handleSearchByRole() {
        String role = roleTextField.getText().trim();
        if (!role.isEmpty()) {
            // Convert the String role to a Role enum value
            Role roleEnum = Role.valueOf(role);
            accountList = FXCollections.observableArrayList(userService.findByRole(roleEnum));
            accountTableView.setItems(accountList);
        } else {
            System.out.println("Please enter a valid role.");
        }
    }

}
