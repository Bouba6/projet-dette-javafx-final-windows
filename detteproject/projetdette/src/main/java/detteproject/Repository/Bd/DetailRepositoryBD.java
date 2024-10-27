package detteproject.Repository.Bd;

import java.util.List;

import detteproject.core.RepositorieDetailDette;
import detteproject.core.RepositoryBdImpl;
import detteproject.data.entities.DetailDette;

public class DetailRepositoryBD extends RepositoryBdImpl<DetailDette> implements RepositorieDetailDette {

    public DetailRepositoryBD() {
        super(DetailDette.class);
    }

    @Override
    public List<DetailDette> selectAll() {
        try {
            return super.selectAll(null);
        } catch (InstantiationException e) {

            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected String getTableName() {
        return "detaildette";
    }

    @Override
    protected List<String> excludedFieldsInsert() {
        return List.of("id", "updateAt", "userUpdate");
    }

    @Override
    protected List<String> excludedFieldsUpdate() {
        return List.of("id", "createAt", "userCreate");
    }

    @Override
    protected List<String> getColumnNames() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getColumnNames'");
    }

    @Override
    protected String[] column() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'column'");
    }

    @Override
    protected List<String> excludedFieldsSelect() {
        return List.of();
    }
}
