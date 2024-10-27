package detteproject.core;

import java.util.List;

import detteproject.State.EtatDette;
import detteproject.core.Config.Service;
import detteproject.data.entities.Article;
import detteproject.data.entities.Client;
import detteproject.data.entities.DetailDette;
import detteproject.data.entities.Dette;
import detteproject.data.entities.Paiement;

public interface ServiceDette extends Service<Dette> {
    public Dette getById(int id, Client client);

    Dette getById1(int id);

    public List<Dette> ListDetEc(Client client);

    public List<DetailDette> findArtInDet(Dette dette);

    public List<Paiement> ListDetPai(Dette dette);

    public List<Dette> ListDetByEtat(EtatDette etat);

    public boolean save1(Dette objet);

}
