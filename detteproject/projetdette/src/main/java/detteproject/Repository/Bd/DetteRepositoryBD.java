package detteproject.Repository.Bd;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import detteproject.State.EtatDette;
import detteproject.State.StateDette;
import detteproject.core.RepositorieDette;
import detteproject.core.RepositoryBdImpl;
import detteproject.data.entities.Article;
import detteproject.data.entities.Client;
import detteproject.data.entities.DetailDette;
import detteproject.data.entities.Dette;
import detteproject.data.entities.Paiement;

public class DetteRepositoryBD extends RepositoryBdImpl<Dette> implements RepositorieDette {

    private ClientRepositoryBD clientRepositoryBD;
    private ArticleRepositoryBD articleRepositoryBD;
    private DetailRepositoryBD detailDetteRepositoryBD;

    public DetteRepositoryBD(ClientRepositoryBD clientRepositoryBD, ArticleRepositoryBD articleRepositoryBD,
            DetailRepositoryBD detailDetteRepositoryBD) {
        super(Dette.class);
        this.clientRepositoryBD = clientRepositoryBD;
        this.articleRepositoryBD = articleRepositoryBD;
        this.detailDetteRepositoryBD = detailDetteRepositoryBD;

    }

    public DetteRepositoryBD() {
        super(Dette.class);
    }

    @Override
    public void update(Dette objet) {
        System.out.println(objet);
        super.update(objet);
    }

    @Override
    public List<Dette> selectAll() {
        try {
            return super.selectAll(null);
        } catch (InstantiationException e) {

            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Dette getById(int id, Client client) {
        String sql = String.format("SELECT * FROM %s WHERE id = ? AND clientid = ?", getTableName());
        try {
            super.innit(sql);
            stm.setInt(1, id);
            stm.setInt(2, client.getId());
            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                return creatObject(resultSet);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Dette creatObject(ResultSet resultSet) throws SQLException {
        Dette dette = new Dette();
        dette.setId(resultSet.getInt("id"));

        dette.setMontant(resultSet.getDouble("montant"));
        dette.setMontantRestant(resultSet.getDouble("MontantRestant"));
        int etat = resultSet.getInt("etatid");
        switch (etat) {
            case 0:
                dette.setEtat(EtatDette.ENCOURS);
                break;

            case 1:
                dette.setEtat(EtatDette.VALIDER);
                break;

            case 2:
                dette.setEtat(EtatDette.ANNULER);
                break;
            default:
                break;
        }
        dette.setClient(clientRepositoryBD.getByid(resultSet.getInt("clientid")));
        int stateStr = resultSet.getInt("stateid");
        StateDette state = StateDette.values()[stateStr];
        dette.setState(state);

        return dette;
    }

    @Override
    public List<Dette> ListDetEc(Client client) {
        String condition = "clientid = '" + client.getId() + "'"; // Use "id = " + id
        List<Dette> clients;
        try {
            clients = super.selectAll(condition);
            // System.out.println(clients.get(0).getState());
            return clients;
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<DetailDette> ListDetArt(Dette dette) {
        String sql = "SELECT d.* FROM detailDette d WHERE d.detteid = ?";
        try {
            System.out.println(dette.getId());
            super.innit(sql); // Initialiser la requête
            stm.setInt(1, dette.getId()); // Utiliser l'ID de la dette
            ResultSet resultSet = stm.executeQuery(); // Exécuter la requête

            List<DetailDette> detailsDette = new ArrayList<>();
            while (resultSet.next()) {
                DetailDette detailDette = new DetailDette();
                detailDette.setId(resultSet.getInt("id"));
                Dette dette1 = new Dette();
                dette.setId(resultSet.getInt("detteid"));
                detailDette.setDette(dette1);
                Article article = new Article();
                article.setId(resultSet.getInt("articleid"));
                detailDette.setArticle(article);
                detailDette.setQte(resultSet.getInt("qte"));
                // Ajouter d'autres propriétés de DetailDette si nécessaire
                detailsDette.add(detailDette);
            }
            return detailsDette; // Retourner la liste des détails de la dette
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Paiement> ListDetPai(Dette dette) {
        String sql = "SELECT * FROM paiement WHERE detteid = ?";
        try {
            super.innit(sql); // Initialiser la requête
            stm.setInt(1, dette.getId()); // Utiliser l'ID de la dette
            ResultSet resultSet = stm.executeQuery(); // Exécuter la requête

            List<Paiement> paiements = new ArrayList<>();
            while (resultSet.next()) {
                Paiement paiement = creatObjectPaiement(resultSet);
                paiements.add(paiement);
            }
            return paiements; // Retourner la liste des paiements associés
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Dette> showByEtat(EtatDette etat) {
        String condition = "etatid = '" + etat.ordinal() + "'";
        try {
            return super.selectAll(condition);
        } catch (InstantiationException e) {

            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected String getTableName() {
        return "dette";
    }

    private Paiement creatObjectPaiement(ResultSet resultSet) throws SQLException {
        Paiement paiement = new Paiement();
        String dateString = resultSet.getString("date");
        LocalDate localDate = LocalDate.parse(dateString);
        paiement.setDate(localDate);
        paiement.setMontant(resultSet.getDouble("montant"));
        paiement.setId(resultSet.getInt("id"));
        return paiement;
    }

    @Override
    protected List<String> getColumnNames() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getColumnNames'");
    }

    @Override
    public boolean insert(Dette objet) {
        super.insert(objet);
        List<DetailDette> details = objet.getDetails();
        for (DetailDette detail : details) {
            detail.onPrePersist();
            detail.setUserCreate(objet.getUserCreate());
            detailDetteRepositoryBD.insert(detail);
            Article article = detail.getArticle();
            article.setUpdateAt(LocalDateTime.now());
            article.setUserUpdate(objet.getUserCreate());
            articleRepositoryBD.update(detail.getArticle());
        }
        Client client = objet.getClient();
        client.setUpdateAt(LocalDateTime.now());
        client.setUserUpdate(objet.getUserCreate());
        clientRepositoryBD.update(objet.getClient());

        return true;
    }

    @Override
    public Dette getById1(int id) {
        String condition = "id = '" + id + "'"; // Use "id = " + id
        List<Dette> dettes;
        try {
            dettes = super.selectAll(condition);

            return dettes.isEmpty() ? null : dettes.get(0);
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected List<String> excludedFieldsInsert() {
        return List.of("id", "updateAt", "userUpdate", "paiements", "details", "date");
    }

    @Override
    protected List<String> excludedFieldsUpdate() {
        return List.of("id", "createAt", "userCreate", "paiements", "details", "date");
    }

    @Override
    protected List<String> excludedFieldsSelect() {
        return List.of("date", "details", "paiements");
    }

    @Override
    protected String[] column() {
        return new String[] { "id", "montantRestant", "montant", "etat", "clientId", "userId" };
    }

    @Override
    public Boolean insert1(Dette objet) {
        super.insert(objet);
        List<DetailDette> details = objet.getDetails();
        for (DetailDette detail : details) {
            detail.onPrePersist();
            detail.setUserCreate(objet.getUserCreate());
            detailDetteRepositoryBD.insert(detail);
            Article article = detail.getArticle();
            article.setUpdateAt(LocalDateTime.now());
            article.setUserUpdate(objet.getUserCreate());
        }
        Client client = objet.getClient();
        client.setUpdateAt(LocalDateTime.now());
        client.setUserUpdate(objet.getUserCreate());
        clientRepositoryBD.update(objet.getClient());

        return true;
    }
}
