package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mercury.database.models.SystemSubFeatureToggle;

@Repository
public class SystemSubFeatureToggleDao {
					
    @Autowired 
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    
	@SuppressWarnings("unchecked")
	public List<SystemSubFeatureToggle> getAll() {
		return entityManager.createQuery("FROM mercury.database.models.SystemSubFeatureToggle").getResultList();
	}
	
	public SystemSubFeatureToggle get(Integer id) {
		return entityManager.find(SystemSubFeatureToggle.class, id);
	}
	
    @Transactional
    public void update(SystemSubFeatureToggle systemSubFeatureToggle) {
        entityManager.merge(systemSubFeatureToggle);
    }
	
	
}
