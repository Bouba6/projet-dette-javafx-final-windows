package detteproject.core;

import java.util.List;

public interface View<T> {
    T saisie();

    void afficher(List<T> list);
}
