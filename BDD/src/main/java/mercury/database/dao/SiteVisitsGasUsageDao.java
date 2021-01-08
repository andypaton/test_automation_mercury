package mercury.database.dao;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.SiteVisitsGasUsage;

@Repository
public class SiteVisitsGasUsageDao {

    private static final Logger logger = LogManager.getLogger();

    private static final String GET_SITE_VISIT_GAS_USAGE_FOR_JOB = "SELECT svgu.Id, SiteVisitId, BottleNumber, BottleQuantity " +
            "FROM SiteVisitsGasUsage svgu " +
            "INNER JOIN SiteVisits sv ON sv.Id = svgu.SiteVisitId " +
            "INNER JOIN ResourceAssignment ra ON ra.Id = sv.ResourceAssignmentId " +
            "INNER JOIN Job j ON j.Id = ra.JobId " +
            "WHERE j.jobReference = %d";

    private static final String GET_SITE_VISIT_GAS_USAGE_FOR_SITE_VISIT = "SELECT svgu.Id, SiteVisitId, BottleNumber, BottleQuantity " +
            "FROM SiteVisitsGasUsage svgu " +
            "WHERE svgu.SiteVisitId = %d";

    @Autowired
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<SiteVisitsGasUsage> getAll() {
        return entityManager.createQuery("from mercury.database.models.SiteVisitsGasUsage").getResultList();
    }


    /**
     * Fetch SiteVisitsGasUsage entity for job
     * @param jobReference
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SiteVisitsGasUsage> getForJobReference(Integer jobReference) {
        String sql = String.format(GET_SITE_VISIT_GAS_USAGE_FOR_JOB, jobReference);
        logger.debug("getForJobReference: " + sql);

        try {
            Query query =  entityManager.createNativeQuery(sql, SiteVisitsGasUsage.class);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetch SiteVisitsGasUsage entity for job
     * @param jobReference
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SiteVisitsGasUsage> getForSiteVisit(Integer siteVisitId) {
        String sql = String.format(GET_SITE_VISIT_GAS_USAGE_FOR_SITE_VISIT, siteVisitId);
        logger.debug("getForSiteVisit: " + sql);

        try {
            Query query =  entityManager.createNativeQuery(sql, SiteVisitsGasUsage.class);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }
}
