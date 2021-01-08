package mercury.database.dao;

import java.util.List;

import static mercury.helpers.StringHelper.quote;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import mercury.database.models.QuotePriority;
import mercury.helpers.DbUtilityHelper;

import org.springframework.stereotype.Repository;

@Repository
public class QuotePriorityDao {
    @Autowired private EntityManager entityManager;
    @Autowired private DbUtilityHelper dbUtilityHelper;

    private static final Logger logger = LogManager.getLogger();

    private static final String BASE_SQL = "SELECT qp.* " +
            " FROM %helpdeskdb.uvw_QuotePriority qp " +
            " INNER JOIN %helpdeskdb.FundingRoute fr " +
            " ON qp.FundingRouteId = fr.id ";

    private static final String SELECT_TOP_SQL = "SELECT TOP(1) qp.* ";
    private static final String FROM_SQL = 	" FROM %helpdeskdb.uvw_QuotePriority qp " +
            " INNER JOIN %helpdeskdb.FundingRoute fr " +
            " ON qp.FundingRouteId = fr.id ";

    private static final String QUERY_BY_FUNDING_ROUTE_WHERE = " WHERE fr.Name = :fundingRouteName ";
    private static final String QUERY_BY_FUNDING_ROUTE_ID_WHERE = " WHERE fr.Id = :fundingRouteId ";
    private static final String QUERY_BY_NUMBER_OF_QUOTES_REQUIRED_WHERE = " qp.MinimumQuotesRequired = 1 ";
    private static final String QUERY_BY_FUNDINGROUTE_NAME_WHERE = 	" WHERE fr.Name = :fundingRouteName AND  qp.QuoteRequestPriority = CAST(:urgency AS bit) ";
    private static final String QUERY_BY_FUNDINGROUTE_ID_WHERE =     " WHERE qp.FundingRouteId = :fundingRouteId AND qp.Id = :quotePriorityId ";
    private static final String ORDER_BY_SQL = " ORDER BY NEWID() ";

    /**
     * @param fundingRouteName - CAPEX, OPEX etc
     * @return - all quote priorities for the given funding route name
     */
    @SuppressWarnings("unchecked")
    public List<QuotePriority> getQuotePriorityByFundingRoute(String fundingRouteName) {
        String sql = BASE_SQL + QUERY_BY_FUNDINGROUTE_NAME_WHERE;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        Query query = entityManager.createNativeQuery(sql, QuotePriority.class);
        try {
            query.setParameter("fundingRouteName", fundingRouteName);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }


    public QuotePriority getQuotePriorityByUrgencyAndFundingRoute(Boolean urgency, String fundingRouteName) {
        String sql = SELECT_TOP_SQL + FROM_SQL
                + QUERY_BY_FUNDINGROUTE_NAME_WHERE
                + ORDER_BY_SQL;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        Query query = entityManager.createNativeQuery(sql, QuotePriority.class);
        try {
            query.setParameter("fundingRouteName", fundingRouteName);
            query.setParameter("urgency", urgency);
            String bool = urgency ? "1" : "0";
            logger.debug("getQuotePriorityByUrgencyAndFundingRoute: " + sql.replace(":fundingRouteName", quote(fundingRouteName)).replace(":urgency", bool));
            return (QuotePriority) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public QuotePriority getRandomQuotePriority(Boolean singleQuote) {
        String sql = SELECT_TOP_SQL + FROM_SQL;
        if (singleQuote) {
            sql = sql.concat(" WHERE ").concat(QUERY_BY_NUMBER_OF_QUOTES_REQUIRED_WHERE);
        }
        sql = sql.concat(ORDER_BY_SQL);

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getRandomQuotePriority : " + sql);

        Query query = entityManager.createNativeQuery(sql, QuotePriority.class);
        try {
            return (QuotePriority) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public QuotePriority getRandomQuotePriority(Boolean singleQuote, String fundingRouteName) {
        String sql = SELECT_TOP_SQL + FROM_SQL
                + QUERY_BY_FUNDING_ROUTE_WHERE;
        if (singleQuote) {
            sql = sql.
                    concat(" AND ").
                    concat(QUERY_BY_NUMBER_OF_QUOTES_REQUIRED_WHERE);
        }
        sql = sql.concat(ORDER_BY_SQL);

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getRandomQuotePriority : " + sql.replaceAll(":fundingRouteName", fundingRouteName));

        Query query = entityManager.createNativeQuery(sql, QuotePriority.class);
        try {
            query.setParameter("fundingRouteName", fundingRouteName);
            return (QuotePriority) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public QuotePriority getRandomQuotePriority(Boolean singleQuote, Integer fundingRouteId) {
        String sql = SELECT_TOP_SQL + FROM_SQL
                + QUERY_BY_FUNDING_ROUTE_ID_WHERE;
        if (singleQuote) {
            sql = sql.
                    concat(" AND ").
                    concat(QUERY_BY_NUMBER_OF_QUOTES_REQUIRED_WHERE);
        }
        sql = sql.concat(ORDER_BY_SQL);

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug(" getRandomQuotePriority " + sql.replaceAll(":fundingRouteId", fundingRouteId.toString()));

        Query query = entityManager.createNativeQuery(sql, QuotePriority.class);
        try {
            query.setParameter("fundingRouteId", fundingRouteId);
            return (QuotePriority) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }


    public QuotePriority getQuotePriorityByFundingRouteId(Integer fundingRouteId, Integer quotePriorityId) {
        String sql = BASE_SQL + QUERY_BY_FUNDINGROUTE_ID_WHERE;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        Query query = entityManager.createNativeQuery(sql, QuotePriority.class);
        try {
            query.setParameter("fundingRouteId", fundingRouteId);
            query.setParameter("quotePriorityId", quotePriorityId);
            return (QuotePriority) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
