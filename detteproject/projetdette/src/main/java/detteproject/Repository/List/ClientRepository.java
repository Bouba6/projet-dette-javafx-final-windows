package detteproject.Repository.List;

import java.util.ArrayList;
import java.util.List;

import detteproject.core.RepositorieClient;
import detteproject.core.Config.Repositorie;
import detteproject.data.entities.Client;
import detteproject.data.entities.User;

public class ClientRepository implements RepositorieClient {
    private List<Client> clients = new ArrayList<>();
    private UserRepository userRepository = new UserRepository();
    private int lastId = 0;

    @Override
    public boolean insert(Client objet) {
        objet.setId(++lastId);
        if (objet.getUser() != null) {
            userRepository.insert(objet.getUser());
        }
        clients.add(objet);

        return true;
    }

    @Override
    public Client getBytel(String telephone) {
        return clients.stream().filter(client -> client.getTelephone().compareTo(telephone) == 0).findFirst()
                .orElse(null);
    }

    @Override
    public List<Client> selectAll() {
        return clients;
    }

    @Override
    public void update(Client objet) {
        if (objet == null) {
            System.out.println("Modification echouée");
        } else {
            System.out.println("Modification reussie");
        }
    }

    @Override
    public Client getByid(int id) {
        return clients.stream().filter(client -> client.getId() == id).findFirst().orElse(null);
    }

    @Override
    public Client getClientByUser(User user) {
        return clients.stream().filter(client -> client.getUser() == user).findFirst().orElse(null);
    }

}
