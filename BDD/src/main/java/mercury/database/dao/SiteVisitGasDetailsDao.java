package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.SiteVisitGasDetails;
import mercury.databuilders.UpdateJob;

@Repository
public class SiteVisitGasDetailsDao {

    private static final Logger logger = LogManager.getLogger();

    private static final String FIELD_LIST = " svgd.*  ";


    private static final String SELECT_TOP_SQL = "SELECT TOP(1) ";
    private static final String SELECT_ALL_SQL = "SELECT ";
    private static final String GET_SITE_VISIT_GAS_DETAILS_SELECT = FIELD_LIST +
            " FROM Job j LEFT JOIN ResourceAssignment ra ON j.Id = ra.JobId " +
            " LEFT JOIN SiteVisits sv ON ra.Id = sv.ResourceAssignmentId  " +
            " INNER JOIN SiteVisitGasDetails svgd ON svgd.SiteVisitId = sv.Id ";


    private static final String GET_SITE_VISIT_GAS_DETAILS_WHERE = " WHERE j.JobReference = '%s' " +
            "  AND CAST(sv.WorkStartTime AS datetime2(7)) = CAST('%s' AS datetime2(7)) " +
            "  --AND CAST(sv.WorkEndTime AS datetime2(7)) = CAST('%s' AS datetime2(7)) " +
            "  AND sv.RemoteFix =  CAST ( '%s' AS bit)  " +
            "  AND sv.GasUsage =  CAST ( '%s' AS bit)  " +
            "  AND sv.Notes = '%s' " +
            "  AND sv.AdditionalResourceRequired = CAST( '%s' AS bit) " +
            "  AND sv.QuoteRequired = CAST( '%s' AS bit)   ";


    private static final String GET_SITE_VISIT_GAS_DETAILS_BY_JOB_REFERENCE_WHERE =  " WHERE j.JobReference = :jobReference AND svgd.CreatedOn >= CAST(:createdOn AS datetime2(7)) ";;
    private static final String UPDATED_ON_ORDER_BY = " ORDER BY svgd.UpdatedOn ASC";

    @Autowired
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<SiteVisitGasDetails> getAll() {
        return entityManager.createQuery("from mercury.database.models.SiteVisitGasDetails").getResultList();
    }

    /**
     * Get Site Visit Gas Details
     * @param id - the SiteVisitId
     * @return
     */
    public SiteVisitGasDetails get(Integer id) {
        return entityManager.find(SiteVisitGasDetails.class, id);
    }


    /**
     * Fetch entityManager entity for job
     * @param siteName
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SiteVisitGasDetails> getJobSiteVisits(UpdateJob  updateJob) {
        String jpql = SELECT_ALL_SQL + GET_SITE_VISIT_GAS_DETAILS_SELECT + GET_SITE_VISIT_GAS_DETAILS_WHERE;

        String queryStatement = String.format(jpql, updateJob.getJobReference(),
                updateJob.getWorkStart() ,
                updateJob.getWorkEnd(),
                updateJob.getRemoteJob().toString().toUpperCase(),
                updateJob.getRefrigerantGasUsed().toString().toUpperCase(),
                updateJob.getNotes(),
                updateJob.getAdditionalResourceRequired().toString().toUpperCase(),
                updateJob.getRequestQuote().toString().toUpperCase());

        try {
            Query query =  entityManager.createNativeQuery(queryStatement, SiteVisitGasDetails.class);
            List<SiteVisitGasDetails> queryResult = query.getResultList();
            return queryResult;
        } catch (Exception e) {
            return null;
        }
    }

    public SiteVisitGasDetails getSiteVisitGasDetailsForJobReference(Integer jobReference, String createdOn) {
        String jpql = SELECT_TOP_SQL + GET_SITE_VISIT_GAS_DETAILS_SELECT  + GET_SITE_VISIT_GAS_DETAILS_BY_JOB_REFERENCE_WHERE + UPDATED_ON_ORDER_BY;
        logger.debug("getSiteVisitGasDetailsForJobReference :".concat(jpql));
        try {
            Query query =  entityManager.createNativeQuery(jpql, SiteVisitGasDetails.class);

            query.setParameter("jobReference", jobReference);
            query.setParameter("createdOn", createdOn);
            SiteVisitGasDetails queryResult = (SiteVisitGasDetails) query.getSingleResult();
            return queryResult;
        } catch (Exception e) {
            return null;
        }
    }

}
