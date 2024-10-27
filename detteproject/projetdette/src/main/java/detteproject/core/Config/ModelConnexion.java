package detteproject.core.Config;

import java.util.List;

import detteproject.State.Role;
import detteproject.data.entities.User;
import detteproject.services.ClientService;
import detteproject.services.UserService;

public class ModelConnexion {
    private static UserService userService;;
    public static List<User> staticUsers = List.of(
            new User(Role.Admin, "admin123", "Admin"),
            new User(Role.Boutiquier, "boutiquier123", "Boutiquier"),
            new User(Role.Client, "client123", "Client"));

    public static User Connexion(String login, String password) {

        for (User user : userService.show()) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

}
