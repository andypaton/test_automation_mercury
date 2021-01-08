package mercury.database.dao;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.QuoteApprovalScenarios;
import mercury.helpers.DbUtilityHelper;

@Repository
public class QuoteApprovalScenariosDao {

    @Autowired private DbUtilityHelper dbUtilityHelper;
    @Autowired private EntityManager entityManager;
    private static final Logger logger = LogManager.getLogger();


    private static final String GET_QUOTE_APPROVAL_SCENARIO_SQL  = "SELECT TOP(1) * FROM %portaldb.vw_QuoteApprovalScenarios WHERE faultId = :faultId " ;

    private static final String GET_QUOTE_APPROVAL_SCENARIO_BASE_SQL  = "SELECT isMultiQuote,ApprovalStatusName,ApprovalStatusDescription, "
            + "ApprovalStatusId, QuoteJobApprovalStatusName, QuoteJobApprovalStatusDescription, QuoteJobApprovalStatusId, AreaManagerId, Cost, CostMinusUplift, "
            + "Created, Description, FaultId, FaultReference, FaultType, FaultPriority, SubTypeClassification, Location, InvitationToQuoteFaultTimeId, "
            + "LoggedDate, MaximumQuoteCost, MostRecentQueryDate, MostRecentQueryReason, MostRecentQueryResponse, MostRecentQueryResponseDate, "
            + "MostRecentQueryText, NumberOfQuotesRequired, ProjectHeaderId, Queried, QuoteApprovalScenarioId, QuoteDueDate, QuotePriority, "
            + "QuotePriorityId, ScopeOfWorks, ScopeOfWorksId, ScopeOfWorksAddedDate, ScopeOfWorksAddedFaultTimeId, Store, StoreClusterId, "
            + "StoreId, StoreDivision, StoreDivisionId, ResourceName, ResourceId, ManagerApproverID, ManagerApproverName, SeniorManagerApproverName, "
            + "SeniorManagerApproverID, QuoteRequestPriority, CallerTypeID, FundingRouteID, ProcessTypeId, IsRetrospectiveQuote, MultiQuoteBypassReasonId, "
            + "MultiQuoteBypassReasonName, MultiQuoteBypassNote,RequiresMultiQuote, ResourceHasAccepted, ContactInfo, ResourceTypeName  "
            + "FROM %portaldb.vw_QuoteApprovalScenarios " ;
    private static final String GET_QUOTE_APPROVAL_SCENARIO_BY_JOB_REFERENCE_WHERE_PARAMETER_SQL = "WHERE faultId = :faultId ";

    public QuoteApprovalScenarios getQuoteByFaultId(Integer faultId) {
        String sql = GET_QUOTE_APPROVAL_SCENARIO_SQL;
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        try {
            entityManager.clear();
            Query query = entityManager.createNativeQuery(sql, QuoteApprovalScenarios.class);
            query.setParameter("faultId", faultId);
            return (QuoteApprovalScenarios) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getRecordCount(Integer faultId,  Map<String, Object> queryMap ) {
        String sql = GET_QUOTE_APPROVAL_SCENARIO_BASE_SQL;
        String whereClause = "";
        for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                whereClause = whereClause + " AND " + entry.getKey() + " LIKE :" + entry.getKey();
            } else {
                whereClause = whereClause + " AND " + entry.getKey() + " = :" + entry.getKey();
            }
        }

        sql = GET_QUOTE_APPROVAL_SCENARIO_BASE_SQL + GET_QUOTE_APPROVAL_SCENARIO_BY_JOB_REFERENCE_WHERE_PARAMETER_SQL
                + whereClause;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        String outputSQL = sql.replaceAll(":faultId", faultId.toString());
        for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
            outputSQL = outputSQL.replaceAll(":" + entry.getKey(), entry.getValue().toString());
        }
        logger.debug(outputSQL);
        try {
            Query query = entityManager.createNativeQuery(sql, QuoteApprovalScenarios.class);
            query.setParameter("faultId", faultId);
            for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            return query.getResultList().size();
        } catch (Exception e) {
            return null;
        }
    }

}
