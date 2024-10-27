package detteproject.core.Config;

import java.util.List;

public interface Service<T> {
    boolean save(T objet);

    void update(T objet);

    List<T> show();

}
