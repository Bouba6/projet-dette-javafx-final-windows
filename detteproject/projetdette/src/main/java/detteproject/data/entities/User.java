package detteproject.data.entities;

import javax.persistence.*;

import detteproject.State.Etat;
import detteproject.State.Role;
import lombok.Data;

@Data
@Entity // Ajoutez cette annotation pour que Hibernate reconnaisse la classe comme une
        // entité
@Table(name = "users") // Nommez la table si nécessaire
public class User extends AbstractEntity {
    public User() {
    }

    private String email;

    @Column(name = "\"roleId\"")
    @Enumerated(EnumType.ORDINAL)
    private Role role;

    @Column(name = "etat")
    @Enumerated(EnumType.ORDINAL)
    private Etat etat;

    private String login;

    private String password;

    @OneToOne(mappedBy = "user")

    @JoinColumn(name = "\"clientId\"")
    private Client client;

    public User(Role role, String login, String password) {
        this.role = role;
        this.login = login;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + login + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
