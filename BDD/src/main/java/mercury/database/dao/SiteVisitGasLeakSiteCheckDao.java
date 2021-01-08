package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.SiteVisitGasLeakSiteCheck;
import mercury.databuilders.UpdateJob;

import static mercury.helpers.StringHelper.quote;

@Repository
public class SiteVisitGasLeakSiteCheckDao {

    private static final Logger logger = LogManager.getLogger();

    private static final String FIELD_LIST = " svglsc.*  ";

    private static final String SELECT_TOP_SQL = "SELECT TOP(1) ";
    private static final String SELECT_ALL_SQL = "SELECT ";
    private static final String GET_SITE_VISIT_GAS_LEAK_SITE_DETAILS_SELECT = FIELD_LIST + " FROM Job j LEFT JOIN ResourceAssignment ra ON j.Id = ra.JobId "
            + " LEFT JOIN SiteVisits sv ON ra.Id = sv.ResourceAssignmentId  "
            + " LEFT JOIN SiteVisitGasLeakSiteCheck svglsc ON svglsc.SiteVisitId = sv.Id  "
            + " LEFT JOIN GasLeakLocation gll ON svglsc.GasLeakLocationId = gll.Id "
            + " LEFT JOIN GasLeakSubLocation glsl ON svglsc.GasLeakSubLocationId = glsl.Id "
            + " LEFT JOIN GasLeakSiteStatus glss ON svglsc.GasLeakSiteStatusId = glss.Id "
            + " LEFT JOIN GasLeakInitialTest glit ON svglsc.GasLeakInitialTestId = glit.Id "
            + " LEFT JOIN GasLeakFollowUpTest glfut ON svglsc.GasLeakFollowUpTestId = glfut.Id ";

    private static final String GET_SITE_VISIT_GAS_LEAK_SITE_DETAILS_WHERE = " WHERE j.JobReference = :jobReference " + " AND gll.Name = :gasLeakLocationName "
            + " AND glsl.Name = :gasLeakSubLocationName "
            + " AND glss.Name = :gasLeakSiteStatusName "
            + " AND glit.Name = :gasLeakInitialTestName "
            + " AND glfut.Name = :gasLeakFollowUpTestName "
            + " AND svglsc.PrimaryComponentInformation = :primaryComponentInformation"
            + " AND svglsc.id IS NOT NULL";


    private static final String GET_SITE_VISIT_GAS_LEAK_SITE_DETAILS_BY_JOB_REFERENCE_WHERE = " WHERE j.JobReference = :jobReference ";
    private static final String UPDATED_ON_ORDER_BY = " ORDER BY svglsc.UpdatedOn DESC";

    @Autowired
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<SiteVisitGasLeakSiteCheck> getAll() {
        return entityManager.createQuery("from mercury.database.models.SiteVisitGasLeakSiteDetails").getResultList();
    }

    /**
     * Fetch entityManager entity for job
     *
     * @param siteName
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SiteVisitGasLeakSiteCheck> getJobSiteVisitGasLeakSiteDetails(UpdateJob updateJob) {
        String jpql = SELECT_ALL_SQL + GET_SITE_VISIT_GAS_LEAK_SITE_DETAILS_SELECT
                + GET_SITE_VISIT_GAS_LEAK_SITE_DETAILS_WHERE;

        String queryStatement = String.format(
                jpql,
                updateJob.getJobReference(),
                updateJob.getWorkStart(),
                updateJob.getWorkEnd(),
                updateJob.getRemoteJob().toString().toUpperCase(),
                updateJob.getRefrigerantGasUsed().toString().toUpperCase(),
                updateJob.getNotes(),
                updateJob.getAdditionalResourceRequired().toString().toUpperCase(),
                updateJob.getRequestQuote().toString().toUpperCase());

        try {
            Query query = entityManager.createNativeQuery(queryStatement, SiteVisitGasLeakSiteCheck.class);
            List<SiteVisitGasLeakSiteCheck> queryResult = query.getResultList();
            return queryResult;
        } catch (Exception e) {
            return null;
        }
    }

    public SiteVisitGasLeakSiteCheck getSiteVisitGasLeakSiteDetailsForJobReference(Integer jobReference, String gasLeakLocationName, String gasLeakSubLocationName, String gasLeakSiteStatusName,
            String gasLeakInitialTestName, String gasLeakFollowUpTestName, String primaryComponentInformation) {
        String jpql = SELECT_TOP_SQL + GET_SITE_VISIT_GAS_LEAK_SITE_DETAILS_SELECT
                + GET_SITE_VISIT_GAS_LEAK_SITE_DETAILS_BY_JOB_REFERENCE_WHERE
                + UPDATED_ON_ORDER_BY;
        logger.debug(jpql);
        try {
            Query query = entityManager.createNativeQuery(jpql, SiteVisitGasLeakSiteCheck.class);
            query.setParameter("jobReference", jobReference);

            query.setParameter("gasLeakLocationName", gasLeakLocationName);
            query.setParameter("gasLeakSubLocationName", gasLeakSubLocationName);
            query.setParameter("gasLeakSiteStatusName", gasLeakSiteStatusName);
            query.setParameter("gasLeakInitialTestName", gasLeakInitialTestName);
            query.setParameter("gasLeakFollowUpTestName", gasLeakFollowUpTestName);
            query.setParameter("primaryComponentInformation", primaryComponentInformation);

            SiteVisitGasLeakSiteCheck queryResult = (SiteVisitGasLeakSiteCheck) query.getSingleResult();
            return queryResult;
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getRecordCount(Integer jobReference, String gasLeakLocationName, String gasLeakSubLocationName, String gasLeakSiteStatusName, String gasLeakInitialTestName,
            String gasLeakFollowUpTestName, String primaryComponentInformation) {
        String jpql = SELECT_TOP_SQL + GET_SITE_VISIT_GAS_LEAK_SITE_DETAILS_SELECT
                + GET_SITE_VISIT_GAS_LEAK_SITE_DETAILS_WHERE
                + UPDATED_ON_ORDER_BY;

        String debugStr = jpql.replace(":jobReference", jobReference.toString())
                .replace(":gasLeakLocationName", quote(gasLeakLocationName))
                .replace(":gasLeakSubLocationName", quote(gasLeakSubLocationName))
                .replace(":gasLeakSiteStatusName", quote(gasLeakSiteStatusName))
                .replace(":gasLeakInitialTestName", quote(gasLeakInitialTestName))
                .replace(":gasLeakFollowUpTestName", quote(gasLeakFollowUpTestName))
                .replace(":primaryComponentInformation", quote(primaryComponentInformation));

        logger.debug(debugStr);

        try {
            Query query = entityManager.createNativeQuery(jpql, SiteVisitGasLeakSiteCheck.class);
            query.setParameter("jobReference", jobReference);

            query.setParameter("gasLeakLocationName", gasLeakLocationName);
            query.setParameter("gasLeakSubLocationName", gasLeakSubLocationName);
            query.setParameter("gasLeakSiteStatusName", gasLeakSiteStatusName);
            query.setParameter("gasLeakInitialTestName", gasLeakInitialTestName);
            query.setParameter("gasLeakFollowUpTestName", gasLeakFollowUpTestName);
            query.setParameter("primaryComponentInformation", primaryComponentInformation);

            return query.getResultList().size();
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<SiteVisitGasLeakSiteCheck> getForSiteVisitId(Integer siteVisitId, Boolean active) {
        String sql = String.format("SELECT * FROM SiteVisitGasLeakSiteCheck WHERE SiteVisitId = %d ", siteVisitId);
        sql = active ? sql + "AND Active = 1 " : sql;
        sql = sql + "ORDER BY Id";
        logger.debug("getForSiteVisitId: " + sql);
        Query query =  entityManager.createNativeQuery(sql, SiteVisitGasLeakSiteCheck.class);
        return query.getResultList();
    }

    public List<SiteVisitGasLeakSiteCheck> getForSiteVisitId(Integer siteVisitId) {
        return getForSiteVisitId(siteVisitId, true);
    }

    public List<SiteVisitGasLeakSiteCheck> getAllForSiteVisitId(Integer siteVisitId) {
        return getForSiteVisitId(siteVisitId, false);
    }

}
