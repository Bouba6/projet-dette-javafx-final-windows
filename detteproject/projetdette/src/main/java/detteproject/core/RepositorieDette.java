package detteproject.core;

import java.util.List;

import detteproject.State.EtatDette;
import detteproject.core.Config.Repositorie;
import detteproject.data.entities.Article;
import detteproject.data.entities.Client;
import detteproject.data.entities.DetailDette;
import detteproject.data.entities.Dette;
import detteproject.data.entities.Paiement;

public interface RepositorieDette extends Repositorie<Dette> {
    Dette getById(int id, Client client);

    Dette getById1(int id);

    List<Dette> ListDetEc(Client client);

    List<DetailDette> ListDetArt(Dette dette);

    List<Paiement> ListDetPai(Dette dette);

    List<Dette> showByEtat(EtatDette etat);

    Boolean insert1(Dette objet);

}
