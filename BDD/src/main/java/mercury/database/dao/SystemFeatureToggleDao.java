package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mercury.database.models.SystemFeatureToggle;

@Repository
public class SystemFeatureToggleDao {
					
    @Autowired 
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    
	@SuppressWarnings("unchecked")
	public List<SystemFeatureToggle> getAll() {
		return entityManager.createQuery("FROM mercury.database.models.SystemFeatureToggle").getResultList();
	}
	
	public SystemFeatureToggle get(Integer id) {
		return entityManager.find(SystemFeatureToggle.class, id);
	}
	
    @Transactional
    public void update(SystemFeatureToggle systemFeatureToggle) {
        entityManager.merge(systemFeatureToggle);
    }
	
	
}
