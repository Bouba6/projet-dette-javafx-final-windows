package detteproject.Repository.jpa;

import detteproject.core.RepositoriePaiement;
import detteproject.core.RepositoryJpaImpl;
import detteproject.data.entities.Paiement;

public class RepositorieJpaPaiement extends RepositoryJpaImpl<Paiement> implements RepositoriePaiement {

    public RepositorieJpaPaiement() {
        super(Paiement.class);
        // TODO Auto-generated constructor stub
    }

}
