package detteproject.controller;

import java.io.IOException;
import java.util.List;

import detteproject.App;
import detteproject.State.Etat;
import detteproject.State.Role;
import detteproject.core.UserConnected;
import detteproject.core.Factory.Repo.FactoryUser;
import detteproject.data.entities.Dette;
import detteproject.data.entities.User;
import detteproject.services.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class UserListController extends AdminController {

    @FXML
    private ComboBox<String> roleComboBox; // Updated from ChoiceBox to ComboBox
    @FXML
    private ComboBox<String> statusComboBox; // Updated from ChoiceBox to ComboBox
    @FXML
    private TableView<User> userTableView;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> passwordColumn;
    @FXML
    private TableColumn<User, String> statusColumn;
    @FXML
    private TableColumn<User, Void> actionColumn;

    @FXML
    private Label Labelnom;

    @FXML
    private Label Labelmail;

    private ObservableList<User> userList;
    private UserService userService;

    @FXML
    private void initialize() {

        User user = UserConnected.getUserConnected();
        String nom = user.getLogin();
        String mail = user.getEmail();
        Labelnom.setText(nom);
        Labelmail.setText(mail);
        // Initialize the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        statusColumn.setCellValueFactory(cellData -> {
            User user1 = cellData.getValue();
            String status;
            switch (user1.getEtat().ordinal()) { // Assuming getEtat() returns the ordinal value
                case 0:
                    status = "Active";
                    break;
                case 1:
                    status = "Desactivater";
                    break;
                default:
                    status = "UNKNOWN";
                    break;
            }
            return new SimpleStringProperty(status);
        });

        FactoryUser factoryUser = new FactoryUser();
        userService = factoryUser.getUserService();

        // Set up ComboBox items and default values
        statusComboBox.setItems(FXCollections.observableArrayList("All", "Active", "Desactivater"));
        statusComboBox.setValue("All");

        roleComboBox.setItems(FXCollections.observableArrayList("All", "Admin", "Boutiquier", "Client"));
        roleComboBox.setValue("All");

        createButtonColumn();
        loadUserData();

        // Add listeners to ComboBox to trigger filtering when selection changes
        roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> filterUsers());
        statusComboBox.valueProperty().addListener((observable, oldValue, newValue) -> filterUsers());
    }

    private void filterUsers() {
        String selectedRole = roleComboBox.getValue();
        String selectedStatus = statusComboBox.getValue();

        // Ensure userList is not null before proceeding
        if (userList == null) {
            userList = FXCollections.observableArrayList(); // Initialize as an empty list if null
        }

        ObservableList<User> filteredUsers = FXCollections.observableArrayList(userList);

        // Filter by role if a specific role is selected
        if (selectedRole != null && !selectedRole.equals("All")) {
            Role roleEnum = Role.valueOf(selectedRole);
            filteredUsers = FXCollections.observableArrayList(userService.findByRole(roleEnum));
        }

        // Further filter by status if a specific status is selected
        if (selectedStatus != null && !selectedStatus.equals("All")) {
            Etat etat = selectedStatus.equals("Active") ? Etat.Activer : Etat.Desactiver;
            List<User> usersByState = userService.findByState(etat);
            if (usersByState == null) {
                usersByState = FXCollections.observableArrayList();
            }

            if (filteredUsers != null) {
                filteredUsers.retainAll(usersByState); // Retain only those users that match the state
            } else {
                filteredUsers = FXCollections.observableArrayList(usersByState);
            }
        }

        // Set the filtered list to the TableView
        userTableView.setItems(filteredUsers);
    }

    private void loadUserData() {
        // Fetch the list of users and convert it to an ObservableList
        userList = FXCollections.observableArrayList(userService.show());
        userTableView.setItems(userList);
    }

    @FXML
    private void handleChangeState(User user) {
        if (user != null) {
            // Changer l'état de l'utilisateur
            if (user.getEtat() == Etat.Activer) {
                user.setEtat(Etat.Desactiver);
            } else {
                user.setEtat(Etat.Activer);
            }

            userTableView.refresh();
            userService.update(user);
            System.out.println("Changed state for user: " + user.getEmail() + " to " + user.getEtat());
        }
    }

    public void handleCreateUser() {

        try {
            App.setRoot("CreateUser");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createButtonColumn() {
        actionColumn.setCellFactory(new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
            @Override
            public TableCell<User, Void> call(TableColumn<User, Void> param) {
                return new TableCell<User, Void>() {
                    private final Button changeStateButton = new Button("Change State");

                    {
                        changeStateButton.setStyle(
                                "-fx-background-color:  #111827; -fx-text-fill: #fff;-fx-border-radius: 5px;  -fx-border-width: 1; -fx-padding: 8; -fx-font-size: 10; -fx-font-weight: bold;");
                        changeStateButton.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleChangeState(user); // Call the method to change the state of the user
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setAlignment(Pos.CENTER);
                        } else {
                            setAlignment(Pos.CENTER);
                            setGraphic(changeStateButton);
                        }
                    }
                };
            }
        });
    }
}
