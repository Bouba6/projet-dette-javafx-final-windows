package detteproject.core;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import detteproject.core.Config.Repositorie;

public class RepositoryJpaImpl<T> implements Repositorie<T> {
    protected EntityManager em;
    protected EntityManagerFactory emf;
    private Class<T> entityClass;

    public RepositoryJpaImpl(Class<T> entityClass) {
        this.entityClass = entityClass;

        // Charger la configuration depuis config.yaml
        ServiceYml serviceYml = new ServiceYml();
        Map<String, Object> config = serviceYml.readYml("META-INF/config.yaml");
        try {
            // Créer l'EntityManager en fonction de l'unité de persistance
            String persistenceUnit = (String) ((Map<String, Object>) config.get("persistence"))
                    .get("unit");

            // Créer l'EntityManagerFactory en fonction de l'unité de persistance
            emf = Persistence.createEntityManagerFactory(persistenceUnit);

            if (em == null) {
                em = emf.createEntityManager();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean insert(T objet) {
        em.getTransaction().begin();
        try {
            em.persist(objet);
            em.getTransaction().commit();
            System.out.println("Enregistrement effectué");
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void update(T objet) {
        em.getTransaction().begin(); // Begin a transaction
        try {
            em.merge(objet); // Merge the updated entity with the existing one in the database
            em.getTransaction().commit(); // Commit the transaction if successful
        } catch (Exception e) {
            em.getTransaction().rollback(); // Rollback if any error occurs
            e.printStackTrace();
        }
    }

    @Override
    public List<T> selectAll() {
        TypedQuery<T> query = em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass);
        return query.getResultList();
    }

}
