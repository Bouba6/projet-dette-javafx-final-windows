package detteproject.Repository.Bd;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import detteproject.core.RepositoriePaiement;
import detteproject.core.RepositoryBdImpl;
import detteproject.data.entities.Dette;
import detteproject.data.entities.Paiement;

public class PaiementRepositoryBD extends RepositoryBdImpl<Paiement> implements RepositoriePaiement {
    DetteRepositoryBD detteRepositoryBD;

    public PaiementRepositoryBD(DetteRepositoryBD detteRepositoryBD) {
        super(Paiement.class);
        this.detteRepositoryBD = detteRepositoryBD;
    }

    @Override
    public void update(Paiement objet) {
        String sql = String.format("UPDATE %s SET montant = ?, date = ?,updateAt = ?,userupdate = ? WHERE id = ?",
                getTableName());
        try {
            super.innit(sql);
            stm.setDouble(1, objet.getMontant());
            stm.setDate(2, java.sql.Date.valueOf(objet.getDate()));
            Timestamp updateTimestamp = Timestamp.valueOf(objet.getUpdateAt());
            stm.setTimestamp(3, updateTimestamp);
            stm.setInt(4, objet.getUserUpdate().getId());
            stm.setInt(5, objet.getId());
            stm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Paiement> selectAll() {
        String sql = String.format("SELECT * FROM %s", getTableName());
        try {
            super.innit(sql);
            stm.executeQuery();
            ResultSet resultSet = stm.getResultSet();
            List<Paiement> list = new ArrayList<>();
            while (resultSet.next()) {
                Paiement paiement = creatObjectPaiement(resultSet);
                list.add(paiement);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    private Paiement creatObjectPaiement(ResultSet resultSet) throws SQLException {
        Paiement paiement = new Paiement();
        Date date = resultSet.getDate("date");
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        paiement.setDate(localDate);
        paiement.setMontant(resultSet.getDouble("montant"));
        paiement.setId(resultSet.getInt("id"));
        return paiement;
    }

    @Override
    protected String getTableName() {
        return "paiement";
    }

    @Override
    protected List<String> getColumnNames() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getColumnNames'");
    }

    @Override
    public boolean insert(Paiement objet) {
        return super.insert(objet);
    }

    @Override
    protected List<String> excludedFieldsInsert() {
        return List.of("id", "updateAt", "userUpdate");
    }

    @Override
    protected List<String> excludedFieldsUpdate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'excludedFieldsUpdate'");
    }

    @Override
    protected String[] column() {
        return new String[] { "id", "montant", "date", "updateAt", "userupdate" };
    }

    @Override
    protected List<String> excludedFieldsSelect() {
        return List.of();
    }

}
