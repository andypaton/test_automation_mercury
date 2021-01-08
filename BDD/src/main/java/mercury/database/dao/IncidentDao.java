package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.Incident;
import mercury.helpers.DbUtilityHelper;

@Repository
public class IncidentDao {

    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private EntityManager entityManager;
    @Autowired private DbUtilityHelper dbUtilityHelper;

    @SuppressWarnings("unchecked")
    public List<Incident> getAll() {
        return entityManager.createQuery("from mercury.database.models.Incident").getResultList();
    }

    public Incident get(Integer id) {
        return entityManager.find(Incident.class, id);
    }


    /**
     * Fetch List<Incident>
     * @param id
     * @return count of New Logged Incidents of type in (Incident Followup, Incident Telephone Escalation Callback, Immediate Escalation)
     */
    @SuppressWarnings("unchecked")
    public List<Incident> getLoggedIncidents(Integer siteId) {
        String jpql = "SELECT DISTINCT i.* FROM Incident i "
                + "INNER JOIN Site s ON i.SiteId = s.Id "
                + "INNER JOIN ToDo td ON i.id = td.IncidentId AND td.ToDoStatusId = 0 "
                + "WHERE s.Id = %s "
                + "AND i.IncidentStatusId = 1 "
                + "AND td.TypeId IN (1, 3, 8) "
                + "AND td.ToDoStatusId = 0";

        String queryStatement = String.format(jpql, siteId);
        logger.debug("getLoggedIncidents: " + queryStatement);

        Query query =  entityManager.createNativeQuery(queryStatement, Incident.class);
        return query.getResultList();
    }


}
