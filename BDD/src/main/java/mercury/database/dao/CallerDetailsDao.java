package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.CallerDetails;
import mercury.helpers.DbUtilityHelper;

@Repository
public class CallerDetailsDao {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private EntityManager entityManager;
    @Autowired private DbUtilityHelper dbUtilityHelper;

    private static final String ALL_CALLERS_CTE = " WITH CC_CTE AS ("
            + " SELECT c.Id, cc.Name, cc.Department, cc.PhoneNumber, cc.Extension, cc.JobTitle AS JobRole, ct.Name AS CallerType, s.name AS SiteName, s.Id AS SiteId"
            + " FROM Caller c"
            + " JOIN CallerType ct ON c.CallerTypeId = ct.Id"
            + " JOIN ClientCaller cc ON c.Id = cc.Id"
            + " JOIN Site s ON cc.SiteId = s.Id"
            + " WHERE c.CallerTypeId = 1), "
            + " RC_CTE AS ("
            + " SELECT c.Id, r.Name, rp.name AS Department, rpn.PhoneNumber, null AS Extension, null AS JobRole, ct.Name AS CallerType, s.name AS SiteName, s.Id AS SiteId"
            + " FROM Caller c"
            + " JOIN CallerType ct ON c.CallerTypeId = ct.Id"
            + " JOIN ResourceCaller rc ON c.Id = rc.Id"
            + " JOIN Resource r ON rc.ResourceId = r.Id"
            + " JOIN ResourceProfile rp ON rp.Id = r.ResourceProfileId"
            + " JOIN ResourcePhoneNumber rpn ON rpn.ResourceId = r.Id"
            + " JOIN SiteResource sr ON sr.ResourceId = r.Id"
            + " JOIN Site s ON sr.SiteId = s.Id"
            + " WHERE c.CallerTypeId = 2 ),"
            + " UNION_CTE AS ( SELECT * FROM CC_CTE UNION SELECT * FROM RC_CTE)";

    private static final String FOR_JOB_REFERENCE = " JOIN JobContact JC ON UNION_CTE.id = jc.CallerId"
            + " INNER JOIN Job J ON j.id = jc.jobid"
            + " WHERE j.JobReference = :jobReference ";

    private static final String FOR_SITE_NAME = " WHERE SiteName = '%s' ";
    private static final String FOR_CALLER_NAME = " WHERE Name='%s' ";
    private static final String FOR_CALLER_NAME_NOT_LIKE = " AND Name NOT LIKE '%s' ";
    private static final String RESOURCE_CALLER_FOR_SITE_NAME = " WHERE CallerType='Resource' AND Department != '' AND SiteName = '%s' ";
    private static final String CLIENT_CALLER_FOR_SITE_STATE = " WHERE CallerType='Client' AND Department != '' AND SiteId IN (SELECT s.Id FROM Site s INNER JOIN SiteStatuses ss ON ss.Id = s.SiteStatusId WHERE ss.name = '%s') ";

    private static final String RANDOM_SQL = " SELECT TOP(1) * FROM UNION_CTE  %s  ORDER BY NEWID() ";
    private static final String RANDOM_SQL_NO_ORDER = " SELECT TOP(1) * FROM UNION_CTE  %s ";


    public CallerDetails getRandomClientCallerForSite(String siteName) {
        String sql = " SELECT TOP(1) c.Id, cc.Name, cc.Department, cc.PhoneNumber, cc.Extension,"
                + " cc.JobTitle AS JobRole, ct.Name AS CallerType, s.name AS SiteName, s.Id AS SiteId"
                + " FROM Caller c JOIN CallerType ct ON c.CallerTypeId = ct.Id"
                + " JOIN ClientCaller cc ON c.Id = cc.Id JOIN Site s ON cc.SiteId = s.Id"
                + " WHERE c.CallerTypeId = 1 AND s.Name = '%s'"
                + " AND cc.Department != '' "
                + " AND cc.Name IN (SELECT Name FROM ClientCaller GROUP BY Name HAVING COUNT(Name) = 1)"; 

        sql = String.format(sql, siteName);
        
        sql = sql + " AND cc.Name NOT LIKE '%(%' ORDER BY NEWID()";
        logger.debug("getRandomClientCallerForSite: " + sql);
        Query query = entityManager.createNativeQuery(sql, CallerDetails.class);
        try {
            return (CallerDetails) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public CallerDetails getNewClientCallerForSite(String siteName, String caller) {
        String where  =  String.format(FOR_SITE_NAME, siteName)
                + String.format(FOR_CALLER_NAME_NOT_LIKE, caller);

        String sql = ALL_CALLERS_CTE+ RANDOM_SQL;
        sql = String.format(sql, where);

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getNewClientCallerForSite: " + sql);
        Query query = entityManager.createNativeQuery(sql, CallerDetails.class);
        try {
            return (CallerDetails) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public CallerDetails getClientCallerByName(String name) {
        String where  =  String.format(FOR_CALLER_NAME, name);

        String sql = ALL_CALLERS_CTE+ RANDOM_SQL;
        sql = String.format(sql, where);

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        logger.debug("getClientCallerByName: " + sql);
        Query query = entityManager.createNativeQuery(sql, CallerDetails.class);


        List<CallerDetails> results = query.getResultList();

        return results.size() > 0 ? results.get(0) : null;
    }

    public CallerDetails getRandomClientCallerWithSite(String siteState) {
        String where  =  String.format(dbUtilityHelper.dbNameSchemaReplacement(CLIENT_CALLER_FOR_SITE_STATE), siteState)
                + " AND Name LIKE '% %' " // Added And condition to get a caller whose name has atleast 2 words to reduce search failures
                + " AND Name IN (SELECT Name FROM UNION_CTE GROUP BY Name HAVING COUNT(Name) = 1) "; //Avoiding duplicate names to reduce failures

        String sql = ALL_CALLERS_CTE + RANDOM_SQL_NO_ORDER ; // Removed 'Order By' statement as it was causing query timeout
        sql = String.format(sql, where);
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getRandomClientCallerWithSite: " + sql);
        Query query = entityManager.createNativeQuery(sql, CallerDetails.class);
        return (CallerDetails) query.getResultList().get(0);
    }

    public CallerDetails getRandomCallerWithPhoneNumber(String type) {
        String where =  String.format(" WHERE CallerType='" + type + "' and PhoneNumber != '' ");

        String sql = ALL_CALLERS_CTE+ RANDOM_SQL;
        sql = String.format(sql, where);
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getRandomCallerWithPhoneNumber: " + sql);
        Query query = entityManager.createNativeQuery(sql, CallerDetails.class);
        try {
            return (CallerDetails) query.getResultList().get(0);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<CallerDetails> getCallerNamesForJob(String jobReference) {
        String sql = ALL_CALLERS_CTE + " SELECT DISTINCT Name, UNION_CTE.* FROM UNION_CTE" + FOR_JOB_REFERENCE;

        sql = sql.replaceAll(":jobReference", jobReference);
        logger.debug("getCallerNamesForJob: " + sql);
        Query query = entityManager.createNativeQuery(sql, CallerDetails.class);
        try {
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public CallerDetails getRandomCallerForSite(String siteName) {
        String where = String.format(FOR_SITE_NAME, siteName);

        String sql = ALL_CALLERS_CTE + RANDOM_SQL;
        sql = String.format(sql, where);
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getRandomClientCallerForSite: " + sql);
        Query query = entityManager.createNativeQuery(sql, CallerDetails.class);
        try {
            return (CallerDetails) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public CallerDetails getRandomResourceCallerForSite(String siteName) {
        String where  =  String.format(dbUtilityHelper.dbNameSchemaReplacement(RESOURCE_CALLER_FOR_SITE_NAME), siteName)
                + " AND Name LIKE '% %' "; // Added And condition to get a caller whose name has atleast 2 words to reduce search failures

        String sql = ALL_CALLERS_CTE + RANDOM_SQL_NO_ORDER;  // removed order statement due to query taking too long
        sql = String.format(sql, where);
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getRandomResourceCallerForSite: " + sql);
        Query query = entityManager.createNativeQuery(sql, CallerDetails.class);
        return (CallerDetails) query.getResultList().get(0);
    }

}
