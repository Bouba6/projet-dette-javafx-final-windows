package detteproject.services;

import java.time.LocalDateTime;
import java.util.List;

import detteproject.Repository.List.PaiementRepository;
import detteproject.core.RepositoriePaiement;
import detteproject.core.ServicePaiement;
import detteproject.core.UserConnected;
import detteproject.data.entities.Paiement;
import detteproject.data.entities.User;

public class PaiementService implements ServicePaiement {
    RepositoriePaiement paiementRepository;

    public PaiementService(RepositoriePaiement paiementRepository) {
        this.paiementRepository = paiementRepository;
    }

    @Override
    public boolean save(Paiement objet) {
        if (objet != null) {
            objet.setCreateAt(LocalDateTime.now());
            User user = UserConnected.getUserConnected();
            objet.setUserCreate(user);
            return paiementRepository.insert(objet);
        }
        return false;

    }

    @Override
    public void update(Paiement objet) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public List<Paiement> show() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'show'");
    }

}
