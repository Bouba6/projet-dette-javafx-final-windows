package detteproject.data.entities;

import java.time.LocalDate;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter

@Entity
public class Paiement extends AbstractEntity {

    @Column(name = "date")
    private LocalDate date;
    @Column
    private double montant;

    @JoinColumn(name = "detteId")
    @ManyToOne
    private Dette dette;

    @Override
    public String toString() {
        return "Paiement{" +
                "id=" + id +
                "date=" + date +
                ", montant=" + montant +
                '}';
    }
}