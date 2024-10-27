package detteproject.Repository.jpa;

import detteproject.core.RepositorieDetailDette;
import detteproject.core.RepositoryJpaImpl;
import detteproject.data.entities.DetailDette;

public class RepositorieJpaDetail extends RepositoryJpaImpl<DetailDette> implements RepositorieDetailDette {

    public RepositorieJpaDetail() {
        super(DetailDette.class);
        // TODO Auto-generated constructor stub
    }

}
