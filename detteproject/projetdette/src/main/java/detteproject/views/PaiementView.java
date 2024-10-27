package detteproject.views;

import java.lang.Thread.State;
import java.time.LocalDate;
import java.util.List;

import detteproject.State.EtatDette;
import detteproject.State.StateDette;
import detteproject.core.ViewImpl;
import detteproject.data.entities.Client;
import detteproject.data.entities.Dette;
import detteproject.data.entities.Paiement;
import detteproject.services.ClientService;
import detteproject.services.DetteService;
import detteproject.services.PaiementService;

public class PaiementView extends ViewImpl {
    private ClientService clientService;
    private DetteService detteService;

    public PaiementView(ClientService clientService, DetteService detteService) {
        this.clientService = clientService;
        this.detteService = detteService;

    }

    @Override
    public Paiement saisie() {
        do {
            scanner.nextLine();
            Paiement paiement = new Paiement();
            Client client = findClientByPhone();
            if (client == null) {
                System.out.println("Client non existant");
                return null;
            }

            List<Dette> dettes = detteService.ListDetEc(client);
            System.out.println("------------------------------------");
            System.out.println(client);
            System.out.println("------------------------------------");
            for (Dette dette : dettes) {
                System.out.println(dette);
            }
            System.out.println("------------------------------------");
            if (dettes.size() == 0) {
                System.out.println("Aucune dette");
                return null;
            }

            Dette dette = findDetteById(client);
            if (dette == null || dette.getMontant() == 0) {

                return null;
            }
            if (dette.getMontantRestant() == 0 || dette.getState() == StateDette.ARCHIVER) {
                System.out.println("La dette est totalement payée");
                // dette.setState(StateDette.ARCHIVER);
                // detteService.update(dette);
                return null;
            }

            if (dette.getEtat() != EtatDette.VALIDER) {
                System.out.println("Cette dette n'est pas encore validee");
                return null;
            }

            double montant = promptMontant(dette);
            if (montant == dette.getMontantRestant()) {
                dette.setState(StateDette.ARCHIVER);
                detteService.update(dette);
            }
            paiement.setMontant(montant);
            paiement.setDette(dette);
            paiement.setDate(LocalDate.now());
            dette.setPaiements(paiement);
            detteService.update(dette);
            return paiement;
        } while (askContinue() == 1);
    }

    // 1. Trouver le client par téléphone
    private Client findClientByPhone() {
        System.out.println("Entrer le numero de telephone du client");
        String tel = scanner.nextLine();
        return clientService.find(tel);
    }

    // 2. Afficher les dettes d'un client

    // 3. Trouver la dette par ID
    // 3. Trouver la dette par ID en évitant une boucle infinie
    private Dette findDetteById(Client client) {
        Dette dette = null;
        int id;
        int attempts = 0;
        final int maxAttempts = 3;
        do {
            System.out.println("Entrer l'id de la dette (ou tapez -1 pour quitter)");
            id = scanner.nextInt();

            if (id == -1) {
                System.out.println("Opération annulée par l'utilisateur.");
                return null;
            }
            dette = detteService.getById(id, client);

            if (dette == null) {
                System.out.println("Dette non existante.");
            } else if (dette.getMontantRestant() == 0) {
                System.out.println("La dette est déjà payée.");
                return null;
            }

            attempts++;

        } while (dette == null && attempts < maxAttempts);

        if (attempts >= maxAttempts) {
            System.out.println("Trop d'essais. Opération annulée.");
            return null;
        }

        return dette;
    }

    // 4. Saisir le montant à payer
    private double promptMontant(Dette dette) {
        double montant;
        do {
            System.out.println("La dette est de : " + dette.getMontantRestant());
            System.out.println("Entrer le montant a payer");
            montant = scanner.nextDouble();

        } while (montant > dette.getMontantRestant());
        return montant;
    }

    // 5. Demander si on continue ou non
    private int askContinue() {
        int rep;
        do {
            System.out.println("Voulez-vous continuer ? 1-oui 2-non");
            rep = scanner.nextInt();
        } while (rep != 1 && rep != 2);
        return rep;
    }
}
