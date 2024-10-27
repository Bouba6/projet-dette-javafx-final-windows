package detteproject.core;

import java.util.List;

import detteproject.State.Role;
import detteproject.core.Config.Repositorie;
import detteproject.data.entities.User;
import detteproject.State.Etat;

public interface RepositorieUser extends Repositorie<User> {
    User getById(int id);

    List<User> getByRole(Role role);

    List<User> getByState(Etat etat);

    User getByLogin(String login, String password);
}
