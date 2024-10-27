package detteproject.services;

import java.time.LocalDateTime;
import java.util.List;

import detteproject.State.Role;
import detteproject.State.Etat;
import detteproject.core.RepositorieUser;
import detteproject.core.ServiceUser;
import detteproject.core.UserConnected;
import detteproject.core.Config.Repositorie;
import detteproject.core.Config.Service;
import detteproject.data.entities.User;

public class UserService implements ServiceUser {
    RepositorieUser userRepository;

    public UserService(RepositorieUser userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean save(User objet) {
        objet.setCreateAt(LocalDateTime.now());
        User user = UserConnected.getUserConnected();
        objet.setUserCreate(user);
        userRepository.insert(objet);
        return true;
    }

    @Override
    public List<User> show() {
        return userRepository.selectAll();
    }

    @Override
    public void update(User objet) {
        objet.setUpdateAt(LocalDateTime.now());
        User user = UserConnected.getUserConnected();
        objet.setUserUpdate(user);
        userRepository.update(objet);
    }

    @Override
    public List<User> findByRole(Role role) {
        List<User> list = userRepository.getByRole(role);
        if (list == null) {
            return null;
        }
        return userRepository.getByRole(role);
    }

    @Override
    public List<User> findByState(Etat etat) {
        List<User> list = userRepository.getByState(etat);
        if (list == null) {
            return null;
        }
        return userRepository.getByState(etat);
    }

    @Override
    public User findById(int id) {
        return userRepository.getById(id);
    }

    @Override
    public User findByLogin(String login, String password) {
        return userRepository.getByLogin(login, password);
    }

}
