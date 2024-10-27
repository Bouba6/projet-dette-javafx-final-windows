package detteproject.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "article")
public class Article extends AbstractEntity {

    @Column(length = 50)
    private String Libelle;

    @Column
    private double Prix;

    @Column
    private double QteStock;

    public void setQteStock(double qteStock) {
        QteStock = qteStock;
    }

    @Override
    public String toString() {
        return "Article [id=" + id + ", Libelle=" + Libelle + ", Prix=" + Prix + ", QteStock=" + QteStock
                + ", userCreate=" + getUserCreate() + ", userUpdate=" + getUserUpdate() + "+,"
                + "createAt=" + getCreateAt() + ", updateAt=" + getUpdateAt() + "]";
    }
}
