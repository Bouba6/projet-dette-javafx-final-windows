package detteproject.data.entities;

import java.lang.Thread.State;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import detteproject.State.Etat;
import detteproject.State.EtatDette;
import detteproject.State.StateDette;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dette")
public class Dette extends AbstractEntity {

    @Column(name = "montant")
    private double montant;

    @JoinColumn(name = "clientid")
    @ManyToOne()
    private Client client;

    @OneToMany(mappedBy = "dette")
    List<DetailDette> details = new ArrayList<>();

    @Column(name = "montantrestant") // attention si ca ne marche pas tu enleves
    private double MontantRestant;

    @Column(name = "montantverser")
    private double montantVerser;

    public void setDetails(DetailDette detail) {
        montant += detail.getQte() * detail.getArticle().getPrix();
        details.add(detail);
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "stateid")
    private StateDette state;

    @OneToMany(mappedBy = "dette")
    List<Paiement> paiements = new ArrayList<>();

    public void setPaiements(Paiement paiement) {
        this.montantVerser += paiement.getMontant();
        this.MontantRestant -= paiement.getMontant();
        paiements.add(paiement);
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "etatid")
    private EtatDette etat;

    @Override
    public String toString() {
        return "Dette{" +
                "id=" + id +
                ", montant=" + montant +
                ", MontantRestant=" + MontantRestant +
                ", etat=" + etat +

                '}';
    }

}
