package detteproject.core.Factory;

import detteproject.core.View;

public interface FactoryViewInterface<T> {
    View<T> createView();
}
