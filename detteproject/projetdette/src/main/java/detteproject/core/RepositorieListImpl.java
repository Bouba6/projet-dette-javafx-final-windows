package detteproject.core;

import java.util.ArrayList;
import java.util.List;

import detteproject.core.Config.Repositorie;

public abstract class RepositorieListImpl<T> implements Repositorie<T> {

    protected List<T> list = new ArrayList<>();
    protected int lastId = 0;

    @Override
    public boolean insert(T objet) {
        if (objet != null) {
            list.add(objet);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void update(T objet) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public List<T> selectAll() {
        return list;
    }

}
