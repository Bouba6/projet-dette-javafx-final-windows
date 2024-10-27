package detteproject.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "detailDette")
public class DetailDette extends AbstractEntity {

    @Column(name = "qte")
    private double qte;

    @ManyToOne
    @JoinColumn(name = "detteId")
    private Dette dette;

    @ManyToOne
    @JoinColumn(name = "articleId")
    private Article article;

    @Override
    public String toString() {
        return "DetailDette [id=" + id + ", qte=" + qte + ", article=" + article.getLibelle() + "]";
    }

}
