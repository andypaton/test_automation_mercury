package mercury.database.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;

import mercury.database.models.ProjectQuoteSummary;
import mercury.helpers.DbUtilityHelper;

public class ProjectQuoteSummaryDao {

    @Autowired private DbUtilityHelper dbUtilityHelper;


    private static final String GET_PROJECT_QUOTE_BASE_SQL = "SELECT pq.fld_int_Id ProjectQuoteId, ph.fld_int_ID ProjectHeaderId, qh.fld_int_ProjectQuoteId QuoteHeaderId  " +
            "FROM %portaldb.tblProjectQuote pq " +
            "INNER JOIN %portaldb.tblProjectHeader ph ON ph.fld_int_id = pq.fld_int_ProjectHeaderID " +
            "INNER JOIN %portaldb.tblQuoteHeader qh ON ph.fld_int_HelpDeskFaultId = qh.fld_int_HelpDeskFaultId " +
            "INNER JOIN %helpdeskdb.Resource r ON pq.fld_int_SubmittedByResourceId = r.EpochId ";

    private static final String GET_PROJECT_QUOTE_BY_JOB_REFERENCE_WHERE_PARAMETER_SQL = "WHERE qh.fld_int_HelpDeskFaultId = :jobReference ";

    private static final String GET_PROJECT_QUOTE_HEADERS_SQL = "SELECT pq.fld_int_Id ProjectQuoteId, ph.fld_int_ID ProjectHeaderId, qh.fld_int_ProjectQuoteId QuoteHeaderId  " +
            "FROM %portaldb.tblProjectQuote pq INNER JOIN  " +
            "%portaldb.tblProjectHeader ph ON ph.fld_int_id = pq.fld_int_ProjectHeaderID " +
            "INNER JOIN  %portaldb.tblQuoteHeader qh ON ph.fld_int_HelpDeskFaultId = qh.fld_int_HelpDeskFaultId " +
            "WHERE qh.fld_int_HelpDeskFaultId = :jobReference " +
            "AND pq.fld_str_QuoteRef = :quoteRef " +
            "AND qh.fld_str_DescriptionOfWorks = :descriptionOfWorks " +
            "AND qh.fld_str_proposedWorkingTimes = :proposedWorkingTimes " +
            "AND pq.fld_dat_QuoteDate > DATEADD(minute, -4, GETDATE()) ";

    @Autowired
    private EntityManager entityManager;
    @Autowired private String jdbc_portal_name;
    @Autowired private String jdbc_helpdesk_name ;

    /**
     * Fetch ProjectQuoteSummary entity by jobReference and quote header details
     * @param id
     * @return
     */
    public ProjectQuoteSummary getByJobReference(Integer jobReference, String quoteRef, String descriptionOfWorks, String proposedWorkingTimes) {
        String queryStatement = GET_PROJECT_QUOTE_HEADERS_SQL;
        queryStatement = dbUtilityHelper.dbNameSchemaReplacement(queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, ProjectQuoteSummary.class);
            query.setParameter("jobReference", jobReference);
            query.setParameter("quoteRef", quoteRef);
            query.setParameter("descriptionOfWorks", descriptionOfWorks);
            query.setParameter("proposedWorkingTimes", proposedWorkingTimes);
            return (ProjectQuoteSummary) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getRecordCount(Integer jobReference,  Map<String, Object> queryMap, String createdOn) {
        String queryStatement = GET_PROJECT_QUOTE_BASE_SQL;
        String whereClause = "";
        for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                whereClause = whereClause + " AND " + entry.getKey() + " LIKE :" + entry.getKey();
            } else if (value instanceof Integer) {
                whereClause = whereClause + " AND " + entry.getKey() + " = :" + entry.getKey();
            } else if (value instanceof Float) {
                whereClause = whereClause + " AND " + entry.getKey() + " = :" + entry.getKey();
            } else if (value instanceof Double) {
                whereClause = whereClause + " AND " + entry.getKey() + " = :" + entry.getKey();
            }  else {
                whereClause = whereClause + " AND " + entry.getKey() + " = :" + entry.getKey();
            }
        }

        queryStatement = GET_PROJECT_QUOTE_BASE_SQL + GET_PROJECT_QUOTE_BY_JOB_REFERENCE_WHERE_PARAMETER_SQL + whereClause;
        queryStatement = dbUtilityHelper.dbNameSchemaReplacement(queryStatement);

        try {
            Query query =  entityManager.createNativeQuery(queryStatement, ProjectQuoteSummary.class);
            query.setParameter("jobReference", jobReference);
            for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            List<ProjectQuoteSummary> projectQuoteSummary =  query.getResultList();
            return projectQuoteSummary.size();
        } catch (Exception e) {
            return null;
        }
    }
}
