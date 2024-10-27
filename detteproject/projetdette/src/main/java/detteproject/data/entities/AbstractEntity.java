package detteproject.data.entities;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@MappedSuperclass
public class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;

    @Column(name = "createAt")
    private LocalDateTime createAt;

    @Column(name = "updateAt")
    private LocalDateTime updateAt;

    // Change userCreate to a ManyToOne relationship
    @ManyToOne
    @JoinColumn(name = "userCreate", referencedColumnName = "id") // assuming "id" is the primary key of User
    public User userCreate;

    // Change userUpdate to a ManyToOne relationship
    @ManyToOne
    @JoinColumn(name = "userUpdate", referencedColumnName = "id") // assuming "id" is the primary key of User
    private User userUpdate;

    @PrePersist
    public void onPrePersist() {
        this.setCreateAt(LocalDateTime.now());
        this.setUpdateAt(LocalDateTime.now());
        System.out.println("CreateAt: " + this.createAt + ", UpdateAt: " + this.updateAt); // Ajoutez ce log pour
                                                                                           // déboguer

    }

    @PreUpdate
    public void onPreUpdate() {
        this.setUpdateAt(LocalDateTime.now());
    }
}
