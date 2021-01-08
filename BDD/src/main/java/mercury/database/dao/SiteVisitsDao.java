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

import cucumber.api.PendingException;
import mercury.database.models.SiteVisits;
import mercury.databuilders.UpdateJob;
import mercury.helpers.DbUtilityHelper;

@Repository
public class SiteVisitsDao {

    @Autowired private DbUtilityHelper dbUtilityHelper;

    private static final Logger logger = LogManager.getLogger();

    private static final String SELECT_ALL_SQL = "SELECT ";
    private static final String GET_SITE_VISIT_SELECT = " sv.* FROM Job j LEFT JOIN ResourceAssignment ra ON j.Id = ra.JobId " +
            " INNER JOIN Site s ON s.Id = j.SiteId " +
            " LEFT JOIN SiteVisits sv ON ra.Id = sv.ResourceAssignmentId  ";



    private static final String SELECT_SITE_VISITS_UK = "SELECT TOP(1) sv.* "
            + "FROM SiteVisits sv "
            + "INNER JOIN SiteVisitsGasUsage svgu ON sv.Id = svgu.SiteVisitId "
            + "INNER JOIN ResourceAssignment ra ON ra.Id = sv.ResourceAssignmentId "
            + "INNER JOIN ResourceAssignmentEvent rae ON rae.ResourceAssignmentId = ra.Id "
            + "INNER JOIN ResourceAssignmentEventType raet ON raet.Id = rae.ResourceAssignmentEventTypeId AND Name IN ('Assignment Completed', 'Resource Returning', 'Refrigerant Gas Used') "
            + "INNER JOIN Job j ON j.Id = ra.JobId ";

    private static final String SELECT_SITE_VISITS_US = "SELECT TOP(1) sv.* "
            + "FROM SiteVisits sv "
            + "INNER JOIN SiteVisitGasDetails svgd ON sv.Id = svgd.SiteVisitId "
            + "LEFT JOIN SiteVisitCylinderDetails svcd ON svcd.SiteVisitGasDetailsId = svgd.SiteVisitId "
            + "LEFT JOIN SiteVisitGasLeakSiteCheck svglsc ON svglsc.SiteVisitId = sv.Id "
            + "INNER JOIN ResourceAssignment ra ON ra.Id = sv.ResourceAssignmentId "
            + "INNER JOIN ResourceAssignmentStatus ras ON ras.Id = ra.ResourceAssignmentStatusId "
            + "INNER JOIN ResourceAssignmentEvent rae ON rae.ResourceAssignmentId = ra.Id "
            + "INNER JOIN ResourceAssignmentEventType raet ON raet.Id = rae.ResourceAssignmentEventTypeId "
            + "INNER JOIN Job j ON j.Id = ra.JobId ";

    private static final String SELECT_SITE_VISITS_WITH_APPLIANCE_TYPE = "SELECT TOP(1) sv.* "
            + "FROM SiteVisits sv "
            + "INNER JOIN SiteVisitGasDetails svgd ON sv.Id = svgd.SiteVisitId "
            + "LEFT JOIN SiteVisitCylinderDetails svcd ON svcd.SiteVisitGasDetailsId = svgd.SiteVisitId "
            + "LEFT JOIN SiteVisitGasLeakSiteCheck svglsc ON svglsc.SiteVisitId = sv.Id "
            + "INNER JOIN ResourceAssignment ra ON ra.Id = sv.ResourceAssignmentId "
            + "INNER JOIN ResourceAssignmentStatus ras ON ras.Id = ra.ResourceAssignmentStatusId "
            + "INNER JOIN ResourceAssignmentEvent rae ON rae.ResourceAssignmentId = ra.Id "
            + "INNER JOIN ResourceAssignmentEventType raet ON raet.Id = rae.ResourceAssignmentEventTypeId "
            + "INNER JOIN Job j ON j.Id = ra.JobId "
            + "INNER JOIN GasApplianceType gat ON gat.Id = svgd.GasApplianceTypeId ";

    private static final String SELECT_SITE_VISITS_FOR_ASSIGNMENT_TYPE = "SELECT TOP(1) * FROM ( SELECT sv.* "
            + "FROM SiteVisits sv "
            + "INNER JOIN SiteVisitsGasUsage svgu ON sv.Id = svgu.SiteVisitId "
            + "INNER JOIN ResourceAssignment ra ON ra.Id = sv.ResourceAssignmentId "
            + "INNER JOIN ResourceAssignmentEvent rae ON rae.ResourceAssignmentId = ra.Id "
            + "INNER JOIN ResourceAssignmentEventType raet ON raet.Id = rae.ResourceAssignmentEventTypeId AND Name IN ('%s')"
            + "INNER JOIN Job j ON j.Id = ra.JobId "
            + "UNION "
            + "SELECT sv.* "
            + "FROM SiteVisits sv "
            + "INNER JOIN SiteVisitGasDetails svgd ON sv.Id = svgd.SiteVisitId "
            + "INNER JOIN ResourceAssignment ra ON ra.Id = sv.ResourceAssignmentId "
            + "INNER JOIN ResourceAssignmentEvent rae ON rae.ResourceAssignmentId = ra.Id "
            + "INNER JOIN ResourceAssignmentEventType raet ON raet.Id = rae.ResourceAssignmentEventTypeId AND Name IN ('%s') "
            + "INNER JOIN Job j ON j.Id = ra.JobId ) UsAndUK";

    private static final String GET_SITE_VISIT_WHERE = " WHERE j.JobReference = '%s' " +
            "  AND CAST(sv.WorkStartTime AS datetime2(7)) = CAST('%s' AS datetime2(7)) " +
            "  --AND CAST(sv.WorkEndTime AS datetime2(7)) = CAST('%s' AS datetime2(7)) " +
            "  AND sv.RemoteFix =  CAST ( '%s' AS bit)  " +
            "  AND sv.GasUsage =  CAST ( '%s' AS bit)  " +
            "  AND sv.Notes = '%s' " +
            "  AND sv.AdditionalResourceRequired = CAST( '%s' AS bit) " +
            "  AND sv.QuoteRequired = CAST( '%s' AS bit)   ";

    private static final String SELECT_SITE_VISITS_WITH_LEAK_SITE_CHECK = "SELECT TOP(1) sv.* "
            + "FROM SiteVisits sv "
            + "INNER JOIN SiteVisitGasDetails svgd ON sv.Id = svgd.SiteVisitId "
            + "LEFT JOIN SiteVisitCylinderDetails svcd ON svcd.SiteVisitGasDetailsId = svgd.SiteVisitId "
            + "LEFT JOIN SiteVisitGasLeakSiteCheck svglsc ON svglsc.SiteVisitId = sv.Id "
            + "INNER JOIN ResourceAssignment ra ON ra.Id = sv.ResourceAssignmentId "
            + "INNER JOIN ResourceAssignmentStatus ras ON ras.Id = ra.ResourceAssignmentStatusId "
            + "INNER JOIN ResourceAssignmentEvent rae ON rae.ResourceAssignmentId = ra.Id "
            + "INNER JOIN ResourceAssignmentEventType raet ON raet.Id = rae.ResourceAssignmentEventTypeId "
            + "INNER JOIN Job j ON j.Id = ra.JobId "
            + "LEFT JOIN GasLeakCheckStatus glcs ON glcs.Id = svgd.GasLeakCheckStatusId "
            + "LEFT JOIN GasLeakSiteStatus glss ON glss.Id = svglsc.GasLeakSiteStatusId "
            + "LEFT JOIN GasLeakCheckResultType glcrt ON glcrt.Id = svgd.GasLeakCheckResultTypeId "
            + "LEFT JOIN GasLeakFollowUpTest glfut ON glfut.Id = svglsc.GasLeakFollowUpTestId "
            + "WHERE svglsc.Id IS NOT NULL AND svcd.Id IS NOT NULL ";

    private static final String SELECT_SITE_VISITS_WITHOUT_LEAK_CHECK = "SELECT TOP(1) sv.* "
            + "FROM SiteVisits sv "
            + "INNER JOIN SiteVisitGasDetails svgd ON sv.Id = svgd.SiteVisitId "
            + "INNER JOIN ResourceAssignment ra ON ra.Id = sv.ResourceAssignmentId "
            + "INNER JOIN ResourceAssignmentStatus ras ON ras.Id = ra.ResourceAssignmentStatusId "
            + "INNER JOIN ResourceAssignmentEvent rae ON rae.ResourceAssignmentId = ra.Id "
            + "INNER JOIN ResourceAssignmentEventType raet ON raet.Id = rae.ResourceAssignmentEventTypeId "
            + "INNER JOIN Job j ON j.Id = ra.JobId "
            + "LEFT JOIN GasLeakCheckStatus glcs ON glcs.Id = svgd.GasLeakCheckStatusId "
            + "WHERE glcs.Name = 'Leak check not performed' ";

    private static final String SELECT_SITE_VISITS_WITH_NO_LEAK_FOUND = "SELECT TOP(1) sv.* "
            + "FROM SiteVisits sv "
            + "INNER JOIN SiteVisitGasDetails svgd ON sv.Id = svgd.SiteVisitId "
            + "LEFT JOIN SiteVisitCylinderDetails svcd ON svcd.SiteVisitGasDetailsId = svgd.SiteVisitId "
            + "LEFT JOIN SiteVisitGasLeakSiteCheck svglsc ON svglsc.SiteVisitId = sv.Id "
            + "INNER JOIN ResourceAssignment ra ON ra.Id = sv.ResourceAssignmentId "
            + "INNER JOIN ResourceAssignmentStatus ras ON ras.Id = ra.ResourceAssignmentStatusId "
            + "INNER JOIN ResourceAssignmentEvent rae ON rae.ResourceAssignmentId = ra.Id "
            + "INNER JOIN ResourceAssignmentEventType raet ON raet.Id = rae.ResourceAssignmentEventTypeId "
            + "INNER JOIN Job j ON j.Id = ra.JobId "
            + "WHERE svglsc.Id IS NULL "
            + "AND svgd.GasLeakCheckResultTypeId = 4 " // no leak found
            + "AND j.Id NOT IN (SELECT JobId FROM JobLock) ";

    private static final String NOT_RETURNING = "AND raet.Name = 'Assignment Completed' "
            + "AND glcs.IsResourceRequiredToReturn = 0 "
            + "AND glss.IsResourceRequiredToReturn = 0 "
            + "AND glcrt.IsResourceRequiredToReturn = 0 "
            + "AND glfut.IsResourceRequiredToReturn = 0";

    private static final String FAILED_LEAK_SITE_CHECKS = " (SELECT SiteVisitId FROM SiteVisitGasLeakSiteCheck WHERE IsRequiredToReturn = 1 AND (PrecedingGasLeakSiteCheckUuid IS NOT NULL OR SubsequentGasLeakSiteCheckUuid IS NOT NULL)) ";
    private static final String WITH_FAILED_LEAK_SITE_CHECKS = " AND sv.Id IN " + FAILED_LEAK_SITE_CHECKS;
    private static final String WITHOUT_FAILED_LEAK_SITE_CHECKS = " AND sv.Id NOT IN " + FAILED_LEAK_SITE_CHECKS;



    @Autowired
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<SiteVisits> getAll() {
        return entityManager.createQuery("from mercury.database.models.SiteVisits").getResultList();
    }


    /**
     * Fetch SiteVisits entity for job
     * @param siteName
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<SiteVisits> getJobSiteVisits(UpdateJob updateJob) {
        String jpql = SELECT_ALL_SQL + GET_SITE_VISIT_SELECT + GET_SITE_VISIT_WHERE;
        jpql = dbUtilityHelper.dbNameSchemaReplacement(jpql);

        String queryStatement = String.format(jpql, updateJob.getJobReference(),
                updateJob.getWorkStart() ,
                updateJob.getWorkEnd(),
                updateJob.getRemoteJob().toString().toUpperCase(),
                updateJob.getRefrigerantGasUsed().toString().toUpperCase(),
                updateJob.getNotes(),
                updateJob.getAdditionalResourceRequired().toString().toUpperCase(),
                updateJob.getRequestQuote().toString().toUpperCase());

        logger.debug("getJobSiteVisits " + queryStatement);

        try {
            Query query =  entityManager.createNativeQuery(queryStatement, SiteVisits.class);
            List<SiteVisits> queryResult = query.getResultList();
            return queryResult;
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<SiteVisits> getSiteVisitsForJobReference(Integer jobReference) {
        String jpql = "SELECT sv.* FROM Job j INNER JOIN Site s ON s.id = j.SiteId LEFT JOIN ResourceAssignment ra ON j.Id = ra.JobId "
                + "LEFT JOIN SiteVisits sv ON ra.Id = sv.ResourceAssignmentId WHERE j.JobReference = :jobReference ORDER BY sv.Id";

        try {
            Query query =  entityManager.createNativeQuery(jpql, SiteVisits.class);

            query.setParameter("jobReference", jobReference);
            logger.debug("getSiteVisitsForJobReference: " + jpql.replaceAll(":jobReference", jobReference.toString()) );
            List<SiteVisits> queryResult = query.getResultList();
            return queryResult;
        } catch (Exception e) {
            return null;
        }
    }

    public SiteVisits getLatestSiteVisitForJobReference(Integer jobReference) {
        try {
            List<SiteVisits> siteVisits = getSiteVisitsForJobReference(jobReference);
            return siteVisits.get(siteVisits.size() - 1);
        } catch (Exception e) {
            return null;
        }
    }

    public SiteVisits getFirstSiteVisitForJobReference(Integer jobReference) {
        try {
            List<SiteVisits> siteVisits = getSiteVisitsForJobReference(jobReference);
            return siteVisits.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get a random SiteVisit for UK Regulations
     */
    public SiteVisits getSiteVisitForPre2019Regulations(String resourceAssignmentEventType) {
        String sql = SELECT_SITE_VISITS_UK;
        sql = sql + " WHERE sv.CreatedOn > '2018-10-22' "               // hack to avoid bad data in DB
                + " AND j.Id NOT IN (SELECT JobId FROM JobLock) ";      // job not locked

        sql = resourceAssignmentEventType == null ? sql : sql + String.format(" AND raet.Name = '%s' ", resourceAssignmentEventType);

        logger.debug("getSiteVisitForUKRegulations: " + sql);

        try {
            Query query = entityManager.createNativeQuery(sql, SiteVisits.class);
            return (SiteVisits) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public SiteVisits getSiteVisitForPre2019Regulations() {
        return getSiteVisitForPre2019Regulations(null);
    }

    /**
     * Get a random SiteVisit for US Regulations
     */
    public SiteVisits getSiteVisitForUSRegulations() {
        String sql = SELECT_SITE_VISITS_US;

        logger.debug("getSiteVisitForUSRegulations: " + sql);

        try {
            Query query = entityManager.createNativeQuery(sql, SiteVisits.class);
            return (SiteVisits) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get a random SiteVisit for US Regulations
     */
    public SiteVisits getSiteVisitFor2019Regulations(String resourceAssignmentEventType, String resourceAssignmentStatus) {
        String sql = SELECT_SITE_VISITS_US
                + " WHERE sv.Id NOT IN (SELECT id FROM SiteVisits WHERE WorkEndTime IN (SELECT WorkEndTime FROM SiteVisits GROUP BY WorkEndTime, ResourceAssignmentId HAVING COUNT(WorkEndTime) > 1)) " // no multiple site visits with same WorkEndTime
                + " AND sv.CreatedOn > '2018-10-22' "                       // hack to avoid bad data in DB
                + " AND j.Id NOT IN (SELECT JobId FROM JobLock)";

        sql = resourceAssignmentEventType == null ? sql : sql + String.format(" AND raet.Name = '%s' ", resourceAssignmentEventType);
        if(resourceAssignmentStatus == "Complete / Work Transferred") {
            sql = sql + "AND ras.Name IN ('Complete', 'Work Transferred') ";
        } else {
            sql = resourceAssignmentStatus == null ? sql : sql + String.format(" AND ras.Name LIKE '%s' ", resourceAssignmentStatus);
        }

        logger.debug("getSiteVisitForUSRegulations: " + sql);

        try {
            Query query = entityManager.createNativeQuery(sql, SiteVisits.class);
            return (SiteVisits) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public SiteVisits getSiteVisitForUSRegulationsWithRefrigerantDetails(String resourceAssignmentEventType, String resourceAssignmentStatus) {
        String sql = SELECT_SITE_VISITS_US
                + "WHERE svcd.Id IS NOT NULL AND svglsc.Id IS NOT NULL "   // with cylinder and gas leak check details
                + "AND sv.Id NOT IN (SELECT id FROM SiteVisits WHERE WorkEndTime IN (SELECT WorkEndTime FROM SiteVisits GROUP BY WorkEndTime, ResourceAssignmentId HAVING COUNT(WorkEndTime) > 1)) " // no multiple site visits with same WorkEndTime
                + "AND j.Id NOT IN (SELECT JobId FROM JobLock) "           // job not locked
                + "AND ISNULL(LevelIndicator,0) != 0 "                     // avoid bad data input from mobile devices
                + "AND svgd.GasTypeId != 33 "                              // not Gas Type = Other
                + "AND sv.Id != 731102 "                                  // excluding this id to avoid bad data
                + "ORDER BY NEWID() ";

        sql = resourceAssignmentEventType == null ? sql : sql + String.format(" AND raet.Name = '%s' ", resourceAssignmentEventType);
        sql = resourceAssignmentStatus == null ? sql : sql + String.format(" AND ras.Name = '%s' ", resourceAssignmentStatus);

        logger.debug("getSiteVisitForUSRegulationsWithRefrigerantDetails: " + sql);

        try {
            Query query = entityManager.createNativeQuery(sql, SiteVisits.class).setHint("org.hibernate.timeout", 120);
            return (SiteVisits) query.getSingleResult();
        } catch (Exception e) {
            throw new PendingException("No site visit found: " + sql);
        }
    }

    public SiteVisits getSiteVisitFor2019RegulationsWithCylinderQty(boolean fullyUsed) {
        String sql = SELECT_SITE_VISITS_US
                + "WHERE svcd.Id IS NOT NULL AND svglsc.Id IS NOT NULL "   // with cylinder and gas leak check details
                + "AND sv.Id NOT IN (SELECT Id FROM SiteVisits WHERE WorkEndTime IN (SELECT WorkEndTime FROM SiteVisits GROUP BY WorkEndTime, ResourceAssignmentId HAVING COUNT(WorkEndTime) > 1)) " // no multiple site visits with same WorkEndTime
                + "AND j.Id NOT IN (SELECT JobId FROM JobLock) "           // job not locked
                + "AND svcd.IsPartialCylinder = 1 "                         // Partial
                + "AND BottleQuantity > 2 "
                + "AND svcd.Active = 1 ";

        sql = fullyUsed ? sql + " AND InitialQuantity = BottleQuantity " : sql + " AND InitialQuantity != BottleQuantity AND InitialQuantity > 2 ";

        logger.debug("getSiteVisitForUSRegulationsWithCylinderQty: " + sql);

        try {
            Query query = entityManager.createNativeQuery(sql, SiteVisits.class);
            return (SiteVisits) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public SiteVisits getSiteVisitFor2019RegulationsWithRemoteSystemApplianceType() {
        String sql = SELECT_SITE_VISITS_WITH_APPLIANCE_TYPE
                + " WHERE svcd.Id IS NOT NULL AND svglsc.Id IS NOT NULL "   // with cylinder and gas leak check details
                + " AND svgd.AssetId IS NOT NULL"
                + " AND j.Id NOT IN (SELECT JobId FROM JobLock) "           // job not locked
                + " AND gat.IsRemoteSystem = 1 "
                + " ORDER BY sv.CreatedOn DESC";                            // get latest matching row

        logger.debug("getSiteVisitForUSRegulationsWithRemoteSystemApplianceType: " + sql);

        try {
            Query query = entityManager.createNativeQuery(sql, SiteVisits.class);
            return (SiteVisits) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public SiteVisits getSiteVisitWithAsset() {
        String sql = SELECT_SITE_VISITS_US
                + " WHERE svcd.Id IS NOT NULL AND svglsc.Id IS NOT NULL " // with cylinder and gas leak check details
                + " AND j.Id NOT IN (SELECT JobId FROM JobLock) "           // job not locked
                + " AND svgd.AssetId IS NOT NULL";

        logger.debug("getSiteVisitWithAsset: " + sql);

        try {
            Query query = entityManager.createNativeQuery(sql, SiteVisits.class);
            return (SiteVisits) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public SiteVisits getSiteVisitFor2019RegulationsWithRefrigerantDetails() {
        return getSiteVisitForUSRegulationsWithRefrigerantDetails(null, null);
    }

    public SiteVisits getSiteVisitWithLeakSiteCheck() {
        return getSiteVisitWithLeakSiteCheck(false);
    }

    public SiteVisits getSiteVisitWithLeakSiteCheck(boolean completed) {
        String sql = SELECT_SITE_VISITS_WITH_LEAK_SITE_CHECK; // with gas leak check details
        sql = completed ? sql + NOT_RETURNING : sql;

        logger.debug("getSiteVisitWithLeakSiteCheck: " + sql);

        try {
            Query query = entityManager.createNativeQuery(sql, SiteVisits.class);
            return (SiteVisits) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public SiteVisits getSiteVisitWithLeakCheck(boolean completed) {
        String sql = SELECT_SITE_VISITS_WITH_LEAK_SITE_CHECK; // with gas leak check details
        sql = completed ? sql + NOT_RETURNING : sql;

        logger.debug("getSiteVisitWithLeakSiteCheck: " + sql);

        try {
            Query query = entityManager.createNativeQuery(sql, SiteVisits.class);
            return (SiteVisits) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get site visit with multiple leak site checks - but NOT with multiple site visits with leak site check failures
     * @return
     */
    public SiteVisits getSiteVisitWithLeakSiteChecks() {
        String sql = "SELECT TOP(1) sv.* "
                + "FROM SiteVisits sv "
                + "INNER JOIN SiteVisitGasDetails svgd ON sv.Id = svgd.SiteVisitId "
                + "INNER JOIN SiteVisitCylinderDetails svcd ON svcd.SiteVisitGasDetailsId = svgd.SiteVisitId "
                + "INNER JOIN SiteVisitGasLeakSiteCheck svglsc ON svglsc.SiteVisitId = sv.Id "
                + "INNER JOIN ResourceAssignment ra ON ra.Id = sv.ResourceAssignmentId "
                + "INNER JOIN ResourceAssignmentStatus ras ON ras.Id = ra.ResourceAssignmentStatusId "
                + "INNER JOIN ResourceAssignmentEvent rae ON rae.ResourceAssignmentId = ra.Id "
                + "INNER JOIN ResourceAssignmentEventType raet ON raet.Id = rae.ResourceAssignmentEventTypeId "
                + "INNER JOIN Job j ON j.Id = ra.JobId "
                + "WHERE j.Id NOT IN (SELECT JobId FROM JobLock) "      // job not locked
                + "AND sv.Id IN ( SELECT SiteVisitId FROM SiteVisitGasLeakSiteCheck WHERE Active = 1 AND (PrecedingGasLeakSiteCheckUuid IS NULL AND SubsequentGasLeakSiteCheckUuid IS NULL) GROUP BY SiteVisitId HAVING COUNT(SiteVisitId) > 1 )";

        logger.debug("getSiteVisitWithLeakSiteChecks: " + sql);

        try {
            Query query = entityManager.createNativeQuery(sql, SiteVisits.class);
            return (SiteVisits) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public SiteVisits getSiteVisitWithLeakSiteCheckFailure(boolean withFailure) {
        String sql = SELECT_SITE_VISITS_US
                + " WHERE svglsc.Id IS NOT NULL "                       // with gas leak check details
                + " AND j.Id NOT IN (SELECT JobId FROM JobLock) " ;     // job not locked

        sql = withFailure ? sql + WITH_FAILED_LEAK_SITE_CHECKS : sql + WITHOUT_FAILED_LEAK_SITE_CHECKS;

        logger.debug("getSiteVisitWithLeakSiteCheckFailure: " + sql);

        try {
            Query query = entityManager.createNativeQuery(sql, SiteVisits.class);
            return (SiteVisits) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public SiteVisits getSiteVisitWithoutLeakCheck() {
        String sql = SELECT_SITE_VISITS_WITHOUT_LEAK_CHECK;
        logger.debug("getSiteVisitWithoutLeakCheck: " + sql);

        try {
            Query query = entityManager.createNativeQuery(sql, SiteVisits.class);
            return (SiteVisits) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get a random SiteVisit for Regulations type
     * @param: type - UK
     */
    public SiteVisits getSiteVisitForAssignmentType(String type) {
        String sql = String.format(SELECT_SITE_VISITS_FOR_ASSIGNMENT_TYPE, type, type);

        logger.debug("getSiteVisitForAssignmentType: " + sql);

        try {
            Query query = entityManager.createNativeQuery(sql, SiteVisits.class);
            return (SiteVisits) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public SiteVisits getSiteVisitWithNoLeakFound() {
        String sql = SELECT_SITE_VISITS_WITH_NO_LEAK_FOUND;
        logger.debug("getSiteVisitWithNoLeakFound: " + sql);

        try {
            Query query = entityManager.createNativeQuery(sql, SiteVisits.class);
            return (SiteVisits) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
