package detteproject.views;

import java.util.ArrayList;
import java.util.List;

import detteproject.Repository.List.ClientRepository;
import detteproject.core.RepositorieClient;
import detteproject.core.RepositorieUser;
import detteproject.core.ViewImpl;
import detteproject.data.entities.Client;
import detteproject.data.entities.User;
import detteproject.services.ClientService;
import detteproject.services.UserService;

public class ClientView extends ViewImpl<Client> {
    UserView userView;
    ClientService clientService;
    UserService userService;

    public ClientView(UserView userView, ClientService clientService, UserService userService) {
        this.userView = userView;
        this.clientService = clientService;
        this.userService = userService;
    }

    @Override
    public Client saisie() {
        scanner.nextLine();
        Client client = new Client();
        client.setSolde(0.0);
        System.out.println("Saisir le nom du client");
        client.setNom(scanner.nextLine());
        System.out.println("Saisir le telephone du client");
        client.setTelephone(scanner.nextLine());
        System.out.println("Saisir l'adresse du client");
        client.setAdresse(scanner.nextLine());
        User user = ask();
        if (user != null) {
            client.setUser(user);
            user.setClient(client);
            // changes ca quand tu bascules en liste
            // userService.save(user);
            // retiens bien ca petit fou
        }

        return client;

    }

    private User ask() {
        User user = new User();
        System.out.println("Voulez vous creer un compte a ce client 1-Oui 2-Non");
        int resp = scanner.nextInt();
        if (resp == 1) {
            user = userView.saisieclient();
            // userService.save(user);
        } else {
            user = null;
        }
        return user;
    }

    public void finding() {
        scanner.nextLine();
        System.out.println("Entrer le numero de telephone du client");
        String tel = scanner.nextLine();
        Client client = clientService.find(tel);
        if (client == null) {
            System.out.println("Client non existant");
        } else {
            System.out.println(client);
        }

    }

    public Client updateClient() {
        scanner.nextLine();
        System.out.println("Entrer le numero de telephone du client");
        String tel = scanner.nextLine();
        Client client = clientService.find(tel);
        if (client == null) {
            System.out.println("Client non existant");
            return null;
        } else if (client.getUser() != null) {
            System.out.println("Le client possede un compte");
            return null;
        }
        User user = userView.saisieclient();
        client.setUser(user);
        user.setClient(client);
        userService.save(user);
        clientService.update(client);
        return client;
    }

    public List<Client> show() {
        List<Client> list = new ArrayList<>();
        List<Client> clients = clientService.show();
        for (Client client : clients) {
            if (client.getUser() != null) {
                list.add(client);
            }
        }
        return list;
    }

}
