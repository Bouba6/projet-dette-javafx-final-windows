package detteproject.core.Factory;

import detteproject.core.Config.Repositorie;

public interface FactoryRepositoryInterface<T> {
    Repositorie<T> createRepository();
}
