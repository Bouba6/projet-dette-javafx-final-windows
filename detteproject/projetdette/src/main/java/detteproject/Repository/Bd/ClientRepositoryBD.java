package detteproject.Repository.Bd;

import java.util.List;

import detteproject.core.DataSourceImpl;
import detteproject.core.Model;
import detteproject.core.RepositorieClient;
import detteproject.core.RepositoryBdImpl;
import detteproject.data.entities.Client;
import detteproject.data.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClientRepositoryBD extends RepositoryBdImpl<Client> implements RepositorieClient {
    private UserRepositoryBD userRepositoryBD;

    public ClientRepositoryBD(UserRepositoryBD userRepositoryBD) {
        super(Client.class);
        this.userRepositoryBD = userRepositoryBD;

    }

    protected List<String> getColumnNames() {
        return List.of("nom", "telephone", "\"userId\"", "adresse"); // Mettez à jour selon votre modèle
    }

    @Override
    public boolean insert(Client objet) {
        // Vérifiez d'abord si l'utilisateur existe
        if (objet.getUser() != null) {
            User user = objet.getUser();
            if (user.getId() <= 0) { // Si l'ID est invalide, insérez l'utilisateur
                user.setCreateAt(objet.getCreateAt());
                user.setUserCreate(objet.getUserCreate());
                userRepositoryBD.insert(user);
                objet.setUser(user); // Mettez à jour l'objet Client
            }
        }

        // Ensuite, insérez le client
        return super.insert(objet);
    }

    @Override
    protected String getTableName() {
        return "client";
    }

    public void update(Client objet) {
        System.err.println(objet);
        super.update(objet);
    }

    @Override
    public List<Client> selectAll() {
        try {
            return super.selectAll(null);
        } catch (InstantiationException e) {

            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Client getBytel(String tel) {
        String condition = "telephone = '" + tel + "'"; // Use "id = " + id
        List<Client> clients;
        try {
            clients = super.selectAll(condition);
            return clients.isEmpty() ? null : clients.get(0);
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    private Client creatObject(ResultSet resultSet) throws SQLException {
        Client client = new Client();
        client.setId(resultSet.getInt("id"));
        client.setAdresse(resultSet.getString("adresse"));
        client.setNom(resultSet.getString("nom"));
        client.setTelephone(resultSet.getString("telephone"));
        client.setSolde(resultSet.getDouble("solde"));
        User user = new User();
        user.setId(resultSet.getInt("userId"));
        client.setUser(user);

        return client;
    }

    @Override
    public Client getByid(int id) {
        String condition = "id = '" + id + "'"; // Use "id = " + id
        List<Client> clients;
        try {
            clients = super.selectAll(condition);
            return clients.isEmpty() ? null : clients.get(0);
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Client getClientByUser(User user) {
        String sql = "SELECT * FROM client WHERE \"userId\"=?";
        try {
            super.innit(sql);
            stm.setInt(1, user.getId());
            ResultSet resultSet = stm.executeQuery();
            if (resultSet.next()) {
                return creatObject(resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected List<String> excludedFieldsInsert() {
        return List.of("id", "dettes", "updateAt", "userUpdate");
    }

    @Override
    protected List<String> excludedFieldsUpdate() {
        return List.of("id", "createAt", "userCreate", "dettes");
    }

    @Override
    protected String[] column() {
        return new String[] { "id", "nom", "adresse", "telephone", "solde", "user" };
    }

    @Override
    protected List<String> excludedFieldsSelect() {
        return List.of("dettes");
    }

    // @Override
    // public boolean insert(Client objet) {
    // // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method 'insert'");
    // }

    // @Override
    // public List<Client> selectAll() {
    // // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method 'selectAll'");
    // }

}
