package detteproject.core.Factory.Repo;

import detteproject.Repository.Bd.UserRepositoryBD;
import detteproject.Repository.List.UserRepository;
import detteproject.Repository.jpa.RepositoryJpaUser;
import detteproject.core.RepositorieUser;
import detteproject.data.entities.Client;
import detteproject.data.entities.User;
import detteproject.services.UserService;

public class FactoryUser {

    private RepositorieUser repositorieUser;

    private UserService userService;

    private UserRepository userRepository;

    private UserRepositoryBD userRepositoryBD;

    private RepositoryJpaUser repositoryjPAuser;

    public FactoryUser() {
        UserRepository userRepository = new UserRepository();
        UserRepositoryBD userRepositoryBD = new UserRepositoryBD();
        this.repositorieUser = new RepositoryJpaUser();
        this.repositorieUser = repositorieUser;
        userService = new UserService(repositorieUser);

    }

    public RepositorieUser getRepositorieUser() {
        return repositorieUser;
    }

    public UserService getUserService() {
        return userService;
    }

    public UserRepositoryBD getUserRepositoryBD() {
        return userRepositoryBD;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

}
