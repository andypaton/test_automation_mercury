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

import mercury.database.models.PortalNotificationsQueue;


@Repository
public class PortalNotificationsQueueDao {

    private static final Logger logger = LogManager.getLogger();

    private static final String SELECT_EMAILS_SQL = "SELECT pnq.* FROM PortalNotificationsQueue pnq " 
            + "INNER JOIN NotificationMethodType nmt ON nmt.Id = pnq.NotificationMethodTypeId " 
            + "WHERE JobReference = %d " 
            + "AND nmt.Name = 'Email' " 
            + "AND pnq.TimelineTitle = '%s' " 
            + "ORDER BY CreatedOn DESC";


    @Autowired
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;
    

    @SuppressWarnings("unchecked")
    public List<PortalNotificationsQueue> getAll() {
        return entityManager.createQuery("from mercury.database.models.PortalNotificationsQueue").getResultList();
    }

    
    /**
     * Fetch email notifications for jobReference
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<PortalNotificationsQueue> getEmailsByJobReference(int jobReference, String timelineTitle) {
        String sql = String.format(SELECT_EMAILS_SQL , jobReference, timelineTitle);
        logger.debug("getByJobReference: " + sql);
        try {
            Query query =  entityManager.createNativeQuery(sql, PortalNotificationsQueue.class);
            return (List<PortalNotificationsQueue>) query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

}
