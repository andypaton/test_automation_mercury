package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import mercury.database.models.BrandWorkingHours;
import mercury.database.models.ResourceWorkingHours;

@Repository
public class ResourceWorkingHoursDao {

    private static final Logger logger = LogManager.getLogger();

    private static final String ALL_FIELDS = " Id, ResourceId, StartDayOfTheWeek, EndDayOfTheWeek, CAST(StartAt AS VARCHAR(15)) AS StartAt, CAST(EndAt AS VARCHAR(15))as EndAt ";
    private static final String ALL_FIELDS_NO_ID = " 0 AS Id, ResourceId, StartDayOfTheWeek, EndDayOfTheWeek, CAST(StartAt AS VARCHAR(15)) AS StartAt, CAST(EndAt AS VARCHAR(15))as EndAt ";

    private static final String BASE_SQL = "SELECT "+ ALL_FIELDS + " FROM ResourceWorkingHours ";

    private static final String FOR_RESOURCE_TODAY = " WHERE ResourceId=%d AND StartDayOfTheWeek = DATEPART(dw,GETDATE()) ";

    @Autowired
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<BrandWorkingHours> getAll() {
        return entityManager.createQuery(BASE_SQL).getResultList();
    }

    public ResourceWorkingHours get(Integer id) {
        return entityManager.find(ResourceWorkingHours.class, id);
    }

    /**
     * getResourceWorkingHoursForToday : get from dbo.ResourceWorkingHours
     * @param resourceId
     * @return
     */
    @Modifying(clearAutomatically = true)
    public ResourceWorkingHours getResourceWorkingHoursForToday(Integer resourceId) {
        String jpql = BASE_SQL + String.format(FOR_RESOURCE_TODAY, resourceId);

        String queryStatement = String.format(jpql, resourceId);
        logger.debug("getResourceWorkingHoursForToday: " + queryStatement);

        Query query =  entityManager.createNativeQuery(queryStatement, ResourceWorkingHours.class);
        try {
            return (ResourceWorkingHours) query.getResultList().get(0);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * getWorkingHoursForToday : get from dbo.uvw_ResourceWorkingHours which defaults to ResourceProfileWorkingHours if ResourceWorkingHours not set
     * @param resourceId
     * @return
     */
    @Modifying(clearAutomatically = true)
    public ResourceWorkingHours getWorkingHoursForToday(Integer resourceId) {
        String jpql = "SELECT "+ ALL_FIELDS_NO_ID + " FROM uvw_ResourceWorkingHours " + String.format(FOR_RESOURCE_TODAY, resourceId);

        String queryStatement = String.format(jpql, resourceId);
        logger.debug("getWorkingHoursForToday: " + queryStatement);

        Query query =  entityManager.createNativeQuery(queryStatement, ResourceWorkingHours.class);
        try {
            return (ResourceWorkingHours) query.getResultList().get(0);
        } catch (Exception e) {
            return null;
        }
    }

}
