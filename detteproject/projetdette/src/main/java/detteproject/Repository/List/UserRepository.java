package detteproject.Repository.List;

import java.util.ArrayList;
import java.util.List;

import detteproject.State.Role;
import detteproject.State.Etat;
import detteproject.core.RepositorieUser;
import detteproject.core.Config.Repositorie;
import detteproject.data.entities.User;

public class UserRepository implements RepositorieUser {

    private List<User> users = new ArrayList<>();
    private int lastId = 0;

    @Override
    public boolean insert(User objet) {
        if (objet != null) {
            objet.setId(++lastId);
            users.add(objet);
            System.out.println("Insertion reussie du user : " + objet);
            return true;
        } else {
            System.out.println("Insertion echouée");
            return false;
        }
    }

    @Override
    public List<User> selectAll() {
        return users;
    }

    @Override
    public void update(User objet) {
        if (objet != null) {
            System.out.println("Modification Reussie");
        } else {
            System.out.println("Modification echouée");
        }
    }

    @Override
    public User getById(int id) {
        return users.stream().filter(user -> user.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<User> getByState(Etat etat) {
        return users.stream().filter(user -> user.getEtat() == etat).toList();
    }

    @Override
    public List<User> getByRole(Role role) {
        return users.stream().filter(user -> user.getRole() == role).toList();
    }

    @Override
    public User getByLogin(String login, String password) {
        return users.stream().filter(user -> user.getLogin().equals(login) && user.getPassword().equals(password))
                .findFirst().orElse(null);
    }

}
