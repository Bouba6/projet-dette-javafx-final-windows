package detteproject.core.Config;

import java.util.List;

public interface Repositorie<T> {

    boolean insert(T objet);

    void update(T objet);

    List<T> selectAll();

}
