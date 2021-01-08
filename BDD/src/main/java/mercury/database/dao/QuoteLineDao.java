package mercury.database.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;

import mercury.database.models.QuoteLine;
import mercury.helpers.DbUtilityHelper;

public class QuoteLineDao {
    @Autowired private EntityManager entityManager;
    @Autowired private DbUtilityHelper dbUtilityHelper;

    private static final String GET_QUOTE_LINE_BASE_SQL = "SELECT ql.fld_int_ID Id " + "      ,ql.fld_int_ProjectQuoteID ProjectQuoteId "
            + ",ql.fld_str_PartCode PartCode "
            + ",ql.fld_str_PartDescription PartDescription "
            + ",ql.fld_str_PartCodeDetail PartCodeDetail "
            + ",ql.fld_cur_UnitPrice UnitPrice "
            + ",ql.fld_dec_Quantity Quantity "
            + ",ql.fld_cur_TotalCost TotalCost "
            + ",ql.fld_dat_CreatedDate CreatedDate "
            + ",ql.fld_bit_DoesPartRequireSupplierOrder DoesPartRequireSupplierOrder "
            + ",ql.fld_bit_PartExistsInPriceBook PartExistsInPriceBook "
            + ",ql.fld_int_PartOrderSupplierResourceId PartOrderSupplierResourceId "
            + ",ql.fld_int_HelpdekResourceProfileId HelpdekResourceProfileId "
            + ",ql.fld_int_HelpdekResourceProfileLabourRateTypeId HelpdekResourceProfileLabourRateTypeId "
            + "FROM %portaldb.tblQuoteLine ql INNER JOIN %portaldb.tblQuoteHeader q  "
            + "ON q.fld_int_ProjectQuoteId = ql.fld_int_ProjectQuoteID ";

    private static final String GET_QUOTE_LINE_BY_JOB_REFERENCE_WHERE_SQL = " WHERE q.fld_int_HelpDeskFaultId = :jobReference";

    @SuppressWarnings("unchecked")
    public List<QuoteLine> getAll() {
        return entityManager.createQuery("from mercury.database.models.Quote").getResultList();
    }

    /**
     * Fetch QuoteLine entity by jobReference
     * @param id
     * @return
     */
    public QuoteLine getByJobReference(Integer jobReference) {
        String sql = GET_QUOTE_LINE_BASE_SQL + GET_QUOTE_LINE_BY_JOB_REFERENCE_WHERE_SQL;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        try {
            Query query = entityManager.createNativeQuery(sql, QuoteLine.class);
            query.setParameter("jobReference", jobReference);
            return (QuoteLine) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }


    @SuppressWarnings("unchecked")
    public Integer getRecordCount(Integer jobReference,  Map<String, Object> queryMap, String createdOn) {
        String sql = GET_QUOTE_LINE_BASE_SQL + GET_QUOTE_LINE_BY_JOB_REFERENCE_WHERE_SQL;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        String whereClause = "";
        for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                whereClause = whereClause + " AND " + entry.getKey() + " LIKE :" + entry.getKey();
            } else if (value instanceof Integer) {
                whereClause = whereClause + " AND " + entry.getKey() + " = :" + entry.getKey();
            } else if (value instanceof Float) {
                // Need to convert to 2 decimal places to protect against 2499.99 turning into 2499.989990234375
                whereClause = whereClause + " AND " + entry.getKey() + " = convert(decimal(10, 2), :" + entry.getKey() + ")";
            } else if (value instanceof Double) {
                whereClause = whereClause + " AND " + entry.getKey() + " = :" + entry.getKey();
            }  else {
                whereClause = whereClause + " AND " + entry.getKey() + " = :" + entry.getKey();
            }
        }
        sql = sql + whereClause;

        try {
            Query query = entityManager.createNativeQuery(sql, QuoteLine.class);
            query.setParameter("jobReference", jobReference);
            for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
            List<QuoteLine> quoteLines = query.getResultList();
            return quoteLines.size();
        } catch (Exception e) {
            return null;
        }
    }



}
