package detteproject.Repository.List;

import java.util.ArrayList;
import java.util.List;

import detteproject.core.RepositorieListImpl;
import detteproject.core.RepositoriePaiement;
import detteproject.data.entities.Paiement;

public class PaiementRepository extends RepositorieListImpl<Paiement> implements RepositoriePaiement {
    List<Paiement> paiements = new ArrayList<>();
    private int lastId = 0;

    @Override
    public void update(Paiement objet) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

}
