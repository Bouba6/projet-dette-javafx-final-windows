package detteproject.core;

import java.util.List;

import detteproject.State.Role;
import detteproject.core.Config.Service;
import detteproject.data.entities.User;
import detteproject.State.Etat;

public interface ServiceUser extends Service<User> {
    List<User> findByRole(Role role);

    User findById(int id);

    List<User> findByState(Etat etat);

    User findByLogin(String login, String password);

}
