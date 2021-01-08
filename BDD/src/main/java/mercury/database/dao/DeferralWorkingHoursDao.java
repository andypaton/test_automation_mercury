package mercury.database.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import mercury.database.models.DeferralWorkingHours;

@Repository
public class DeferralWorkingHoursDao {

    private static final Logger logger = LogManager.getLogger();

    private static final String GET_SITE_WORKING_HOURS = "SELECT TOP(1) dwh.id, DayOfTheWeek, CAST(dwh.StartAt AS VARCHAR(15)) AS StartAt, CAST(dwh.EndAt AS VARCHAR(15))as EndAt " +
            " FROM DeferralWorkingHours dwh" +
            " LEFT JOIN DeferralWorkingHoursGroup dwhg ON dwhg.DeferralWorkingHoursId = dwh.Id" +
            " LEFT JOIN Site s ON s.BrandId = dwhg.BrandId ";


    private static final String FOR_SITE_TODAY = " WHERE s.Id = %s and DayOfTheWeek = DATEPART(dw,GETDATE())";

    @Autowired
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;


    @Modifying(clearAutomatically = true)
    public DeferralWorkingHours getDeferralWorkingHoursForSiteId(Integer siteId) {
        String jpql = GET_SITE_WORKING_HOURS + String.format(FOR_SITE_TODAY, siteId);

        String queryStatement = String.format(jpql, siteId);
        logger.debug("getDeferralWorkingHoursForSiteId: " + queryStatement);

        try {
            Query query =  entityManager.createNativeQuery(queryStatement, DeferralWorkingHours.class);
            return (DeferralWorkingHours) query.getSingleResult();
        }
        catch (Exception e) {
            return null;
        }
    }

}
