package mercury.database.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.JobTimelineEvent;
import mercury.database.models.Quote;
import mercury.helpers.DbUtilityHelper;

@Repository
public class QuoteDao {
    private static final Logger logger = LogManager.getLogger();

    @Autowired private EntityManager entityManager;
    @Autowired private DbUtilityHelper dbUtilityHelper;

    private static final String GET_QUOTE_BASE_SQL = "SELECT q.* " +
            "FROM " +
            "Job j INNER JOIN Quote q ON j.id = q.jobid ";

    private static final String GET_QUOTE_BY_JOB_REFERENCE_WHERE_SQL = "WHERE j.jobreference = '%s' ";

    private static final String GET_QUOTE_ASSERTION_QUERY_BASE_SQL = "SELECT q.* FROM %helpdeskdb.Job j  " +
            "INNER JOIN %helpdeskdb.Quote q ON j.id = q.jobid  " +
            "LEFT JOIN %portaldb.vw_QuoteApprovalScenarios qas ON qas.FaultReference = j.JobReference " +
            "LEFT JOIN %portaldb.tblQuoteRecommendation qr ON qas.QuoteApprovalScenarioId = qr.fld_int_QuoteApprovalScenarioId " +
            "LEFT JOIN %portaldb.tblQuery qry ON qas.ScopeOfWorksId = qry.fld_int_ScopeOfWorksId ";

    private static final String GET_QUOTE_ASSERTION_QUERY_BASE_WHERE_SQL = " WHERE j.jobreference = :jobReference ";


    @SuppressWarnings("unchecked")
    public List<Quote> getAll() {
        return entityManager.createQuery("from mercury.database.models.Quote").getResultList();
    }

    /**
     * Fetch quote entity by jobReference
     * @param id
     * @return
     */
    public Quote getByJobReference(Integer jobreference) {
        String jpql = GET_QUOTE_BASE_SQL + GET_QUOTE_BY_JOB_REFERENCE_WHERE_SQL;

        String queryStatement = String.format(jpql, jobreference);
        logger.debug(queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, Quote.class);
            return (Quote) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }


    @SuppressWarnings("unchecked")
    public Integer getRecordCount(Integer jobReference,  Map<String, Object> queryMap, String createdOn ) {
        String sql = GET_QUOTE_ASSERTION_QUERY_BASE_SQL;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

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

        sql = GET_QUOTE_ASSERTION_QUERY_BASE_SQL + GET_QUOTE_ASSERTION_QUERY_BASE_WHERE_SQL
                + whereClause;
        logger.debug(sql);
        try {
            Query query = entityManager.createNativeQuery(sql, JobTimelineEvent.class);
            query.setParameter("jobReference", jobReference);
            for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            List<Quote> quote =  query.getResultList();
            return quote.size();
        } catch (Exception e) {
            return null;
        }
    }


}
