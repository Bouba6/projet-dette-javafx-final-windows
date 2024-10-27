package detteproject.Repository.List;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import detteproject.State.EtatDette;
import detteproject.core.RepositorieDette;
import detteproject.core.Config.Repositorie;
import detteproject.data.entities.Article;
import detteproject.data.entities.Client;
import detteproject.data.entities.DetailDette;
import detteproject.data.entities.Dette;
import detteproject.data.entities.Paiement;
import detteproject.views.DetteView;

public class DetteRepository implements RepositorieDette {
    private List<Dette> dettes = new ArrayList<>();
    private int lastId = 0;
    private DetteView detteView;

    @Override
    public boolean insert(Dette objet) {
        if (objet != null) {
            objet.setId(++lastId);
            System.out.println("Insertion reussie");
            objet.setCreateAt(LocalDateTime.now());
            dettes.add(objet);
            return true;
        } else {
            System.out.println("Insertion echouée");
            return false;
        }

    }

    @Override
    public void update(Dette objet) {
        if (objet != null) {
            for (int i = 0; i < dettes.size(); i++) {
                if (objet.getId() == dettes.get(i).getId()) {
                    dettes.set(i, objet);
                    return;
                }
            }
        }

    }

    @Override
    public List<Dette> selectAll() {
        return dettes;
    }

    @Override
    public Dette getById(int id, Client client) {
        for (Dette det : dettes) {
            if (det.getId() == id && det.getClient().getId() == client.getId()) {
                return det;
            }
        }
        return null;
    }

    @Override
    public List<DetailDette> ListDetArt(Dette dette) {
        List<DetailDette> articles = new ArrayList<>();
        for (Dette det : dettes) {
            for (DetailDette art : det.getDetails()) {
                articles.add(art);
            }
        }
        return articles;
    }

    @Override
    public List<Paiement> ListDetPai(Dette dette) {
        List<Paiement> paiements = new ArrayList<>();
        for (Dette det : dettes) {
            for (Paiement pai : det.getPaiements()) {
                paiements.add(pai);
            }
        }
        return paiements;
    }

    @Override
    public List<Dette> ListDetEc(Client client) {
        List<Dette> dets = new ArrayList<>();
        for (Dette det : dettes) {

            if (det.getClient().getId() == client.getId()) {
                dets.add(det);
            }
        }
        return dets;
    }

    @Override
    public List<Dette> showByEtat(EtatDette etat) {
        List<Dette> dets = new ArrayList<>();
        for (Dette det : dettes) {
            if (det.getEtat() == etat) {
                dets.add(det);
            }
        }
        return dets;
    }

    @Override
    public Dette getById1(int id) {
        return dettes.stream().filter(dette -> dette.getId() == id).findFirst().orElse(null);
    }

    @Override
    public Boolean insert1(Dette objet) {
        if (objet != null) {
            objet.setId(++lastId);
            System.out.println("Insertion reussie");
            objet.setCreateAt(LocalDateTime.now());
            dettes.add(objet);
            return true;
        } else {
            System.out.println("Insertion echouée");
            return false;
        }
    }

}
