package detteproject.Repository.jpa;

import java.util.List;

import detteproject.State.Etat;
import detteproject.State.Role;
import detteproject.core.RepositorieUser;
import detteproject.core.RepositoryJpaImpl;
import detteproject.data.entities.User;

public class RepositoryJpaUser extends RepositoryJpaImpl<User> implements RepositorieUser {

    public RepositoryJpaUser() {
        super(User.class);

    }

    @Override
    public User getById(int id) {
        return em.find(User.class, id);
    }

    @Override
    public List<User> getByRole(Role role) {
        List<User> result = em.createQuery("SELECT u FROM User u WHERE u.role = :role", User.class)
                .setParameter("role", role).getResultList();
        if (result.isEmpty()) {
            System.out.println("User non existant");
            return null;
        } else {
            return result;
        }
    }

    @Override
    public List<User> getByState(Etat etat) {
        List<User> result = em.createQuery("SELECT u FROM User u WHERE u.etat = :etat", User.class)
                .setParameter("etat", etat).getResultList();
        if (result.isEmpty()) {
            System.out.println("User non existant");
            return null;
        } else {
            return result;
        }
    }

    @Override
    public User getByLogin(String login, String password) {
        User result = em
                .createQuery("SELECT u FROM User u WHERE u.login = :login AND u.password = :password", User.class)
                .setParameter("login", login).setParameter("password", password).getSingleResult();
        if (result == null) {
            System.out.println("User non existant");
            return null;
        } else {
            return result;
        }
    }

}
