package detteproject.Auth;

import java.util.Scanner;

import detteproject.data.entities.User;
import detteproject.services.UserService;

public class Connexion {
    private final UserService userService;

    // Constructor to inject userService dependency
    public Connexion(UserService userService) {
        this.userService = userService;
    }

    public User connexion() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your login: ");
        String login = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        User user = userService.findByLogin(login, password);

        if (user == null) {
            System.out.println("Invalid login or password.");
            return null;
        }

        System.out.println("Welcome " + user.getLogin() + "!");
        return user;
    }
}
