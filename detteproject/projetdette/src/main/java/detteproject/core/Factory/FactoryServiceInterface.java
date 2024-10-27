package detteproject.core.Factory;

import detteproject.core.Config.Service;

public interface FactoryServiceInterface<T> {
    Service<T> createService();
}
