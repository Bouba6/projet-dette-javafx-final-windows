package detteproject.views;

import java.util.List;

import detteproject.State.Etat;
import detteproject.State.Role;
import detteproject.core.RepositorieUser;
import detteproject.core.UserConnected;
import detteproject.core.ViewImpl;
import detteproject.core.Config.Repositorie;
import detteproject.data.entities.User;
import detteproject.services.UserService;

public class UserView extends ViewImpl<User> {

    UserService userService;

    public UserView(UserService userService) {
        this.userService = userService;
    }

    @Override
    public User saisie() {
        scanner.nextLine();
        User user = new User();

        System.out.println("Saisir l'email ");
        user.setEmail(scanner.nextLine());
        System.out.println("Saisir le login ");
        user.setLogin(scanner.nextLine());
        while (userService.findByLogin(user.getLogin(), user.getPassword()) != null) {
            System.out.println("login existant");
            System.out.println("Saisir le login ");
            user.setLogin(scanner.nextLine());
        }
        System.out.println("Saisir le mot de passe ");
        user.setPassword(scanner.nextLine());
        user.setEtat(Etat.Activer);
        user.setRole(askRole());
        user.onPrePersist();
        user.setUserCreate(UserConnected.getUserConnected());
        return user;

    }

    public User saisieclient() {
        User user = new User();
        scanner.nextLine();
        System.out.println("Saisir l'email ");
        user.setEmail(scanner.nextLine());
        System.out.println("Saisir le login ");
        user.setLogin(scanner.nextLine());
        System.out.println("Saisir le mot de passe ");
        user.setPassword(scanner.nextLine());
        user.setEtat(Etat.Activer);
        user.setRole(Role.Client);

        return user;
    }

    public List<User> findByRole() {
        Role role = askRole();
        return userService.findByRole(role);
    }

    public List<User> findByEtat() {
        Etat etat = askEtat();
        return userService.findByState(etat);
    }

    private Role askRole() {
        System.out.println("Entrer le role que vous recherchere 1-Admin 2-boutiquier  3-client");
        int choix = scanner.nextInt();
        do {
            if (choix == 1) {
                return Role.Admin;
            } else if (choix == 2) {
                return Role.Boutiquier;
            } else {
                return Role.Client;
            }
        } while (choix != 1 || choix != 2 || choix != 3);
    }

    private Etat askEtat() {
        System.out.println("Entrer l'etat que vous recherchere 1-Activer 2-Desactiver");
        int choix = scanner.nextInt();
        do {
            if (choix == 1) {
                return Etat.Activer;
            } else {
                return Etat.Desactiver;
            }
        } while (choix != 1 || choix != 2);
    }

    private Role ask() {
        Role role = null;
        System.out.println("Quelle est le role de ce user 1-Admin 2-boutiquier");
        int resp = scanner.nextInt();
        if (resp == 1) {
            role = Role.Admin;
        } else {
            role = Role.Boutiquier;
        }
        return role;
    }

    private int resp() {
        System.out.println("S'agit t-il pour la creation d'un compte pour le client ? 1-Oui 2-Non");
        int resp = scanner.nextInt();
        if (resp == 1) {
            return 1;
        } else {
            return 2;
        }
    }

    public User ChangeState() {
        System.out.println("entrer l'id du user");
        int id = scanner.nextInt();
        User user = userService.findById(id);
        if (user == null) {
            System.out.println("user non existant");
            return null;
        }

        ask(user);
        return user;
    }

    public void ask(User user) {
        System.out.println(" vous venez de changer l'etat de ce user qui etait de " + user.getEtat());

        user.setEtat(user.getEtat() == Etat.Desactiver ? Etat.Activer : Etat.Desactiver);

    }

}
