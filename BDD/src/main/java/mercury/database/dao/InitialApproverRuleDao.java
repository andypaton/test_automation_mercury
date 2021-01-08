package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import mercury.database.models.InitialApproverRule;
import mercury.helpers.DbUtilityHelper;

public class InitialApproverRuleDao {

    @Autowired private DbUtilityHelper dbUtilityHelper;
    @Autowired private EntityManager entityManager;

    private static final Logger logger = LogManager.getLogger();


    private static final String GET_ALL_ADDITIONAL_APPROVER_RULE_SQL  = "SELECT TOP(1) * FROM %helpdeskdb.AdditionalApproverRule" ;

    private static final String GET_ALL_ADDITIONAL_APPROVER_RULE_WHERE_FUNDING_ROUTE_SQL  = " WHERE FundingRouteID = :fundingRouteID ";

    @SuppressWarnings("unchecked")
    public List<InitialApproverRule> getAllAdditionalApproverRule() {
        String sql = GET_ALL_ADDITIONAL_APPROVER_RULE_SQL;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        try {
            entityManager.clear();
            Query query = entityManager.createNativeQuery(sql, InitialApproverRule.class);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<InitialApproverRule> getAllAdditionalApproverRuleByFundingRoute(Integer fundingRouteID) {
        String sql = GET_ALL_ADDITIONAL_APPROVER_RULE_SQL + GET_ALL_ADDITIONAL_APPROVER_RULE_WHERE_FUNDING_ROUTE_SQL;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug(sql);
        try {
            Query query = entityManager.createNativeQuery(sql, InitialApproverRule.class);
            query.setParameter("fundingRouteID", fundingRouteID);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }
}
