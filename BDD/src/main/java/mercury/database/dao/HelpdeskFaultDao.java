package mercury.database.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.HelpdeskFault;
import mercury.helpers.DbUtilityHelper;

@Repository
public class HelpdeskFaultDao {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private EntityManager entityManager;
    @Autowired private DbUtilityHelper dbUtilityHelper;

    private static final String GET_HELPDESK_FAULT_SQL = "SELECT * FROM %portaldb.tblHelpDeskFault" +
            "   WHERE fld_int_ID = :jobReference";


    /**
     * Fetch ResourceId, UserName and JobReference entity who has jobs in the the given status and profile of profileName
     * used in portal/quotes/job awaiting quote screen
     * @param profileName
     * @param quoteApprovalStatusName
     * @return UserJob entity
     */
    public HelpdeskFault getJobInHelpdeskFault(Integer jobReference) {
        String sql = GET_HELPDESK_FAULT_SQL;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug(sql.replaceFirst(":jobReference", jobReference.toString()));

        try {
            Query query = entityManager.createNativeQuery(sql, HelpdeskFault.class);
            query.setParameter("jobReference", jobReference);
            HelpdeskFault helpdeskFault = (HelpdeskFault) query.getSingleResult();
            return helpdeskFault;
        } catch (Exception e) {
            return null;
        }
    }
}
