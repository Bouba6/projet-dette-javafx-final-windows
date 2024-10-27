package detteproject.core;

import java.util.List;

import detteproject.core.Config.Repositorie;
import detteproject.data.entities.Client;
import detteproject.data.entities.Dette;
import detteproject.data.entities.User;

public interface RepositorieClient extends Repositorie<Client> {
    Client getBytel(String tel);

    Client getByid(int id);

    Client getClientByUser(User user);

}
