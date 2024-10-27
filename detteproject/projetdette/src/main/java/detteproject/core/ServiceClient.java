package detteproject.core;

import detteproject.core.Config.Service;
import detteproject.data.entities.Client;
import detteproject.data.entities.User;

public interface ServiceClient extends Service<Client> {
    Client find(String tel);

    Client findById(int id);

    Client findClientByUser(User user);
}
