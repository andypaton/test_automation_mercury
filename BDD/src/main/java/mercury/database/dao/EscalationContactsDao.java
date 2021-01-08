package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.EscalationContacts;

@Repository	
public class EscalationContactsDao {

	private static final Logger logger = LogManager.getLogger();

	private static final String GET_ESCALATION_CONTACT_BASE_SQL = " SELECT TOP(1) eer.*,ehc.Name " + 
			"FROM EscalationEmailRecipientLink eer INNER JOIN " + 
			"EscalationEmailHistory eeh ON eer.EscalationEmailHistoryId = eeh.Id INNER JOIN " + 
			"EscalationHistoryContact ehc ON ehc.EscalationHistoryId = eeh.EscalationHistoryId INNER JOIN " + 
			"Incident i ON i.Id = eeh.IncidentId " + 
			"WHERE i.IncidentReference = :incidentReference " + 
			"ORDER BY eeh.Id DESC ";

	@Autowired
	@PersistenceContext(type = PersistenceContextType.TRANSACTION)
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public List<EscalationContacts> getAll() {
		return entityManager.createQuery("from mercury.database.models.EscalationContacts").getResultList();
	}

	public EscalationContacts getByIncidentReferenceNumber(Integer incidentReference) {
		String jpql =   GET_ESCALATION_CONTACT_BASE_SQL ;
		String queryStatement = jpql;
		logger.debug(queryStatement);
		Query query =  entityManager.createNativeQuery(queryStatement, EscalationContacts.class);
		query.setParameter("incidentReference", incidentReference);
		return (EscalationContacts) query.getSingleResult();     
	}

}
