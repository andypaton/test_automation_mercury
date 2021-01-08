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

import mercury.database.models.FundingRequest;
import mercury.helpers.DbUtilityHelper;

@Repository
public class FundingRequestDao {

    @Autowired private DbUtilityHelper dbUtilityHelper;

    private static final Logger logger = LogManager.getLogger();

    private static final String GET_FUNDING_REQUEST_BASE_SQL = "SELECT fr.Id, fr.ResourceAssignmentId, fr.StatusId, fr.RequestedAmount, fr.Description, fr.InitialRequestApproverId,"
            + " fr.AuthorisedOrRejectedById, fr.Notes, fr.FundingReasonId, fr.RejectionReasonId, fr.NoAmountReasonId, fr.CancellationReasonId, fr.InitialFunding, fr.AutomaticallyApproved,"
            + " fr.CreatedOn, fr.CreatedBy, fr.UpdatedOn, fr.UpdatedBy, fr.FundingRouteId"
            + " FROM FundingRequest fr"
            + " INNER JOIN ResourceAssignment ra ON fr.ResourceAssignmentId = ra.Id "
            + " INNER JOIN Job j ON ra.JobId = j.Id"
            + " INNER JOIN Resource r ON ra.ResourceId = r.Id";

    private static String WHERE = " WHERE ";
    private static String AND = " AND ";

    private static String JOB_REFERENCE = " j.JobReference = :jobReference ";
    private static String RESOURCE_NAME = " r.Name = :resourceName ";
    private static String INITIAL_FUNDING = " fr.InitialFunding = :initialFunding ";

    @Autowired
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<FundingRequest> getAll() {
        return entityManager.createQuery("from mercury.database.models.Job").getResultList();
    }

    public FundingRequest get(Integer id) {
        return entityManager.find(FundingRequest.class, id);
    }

    /**
     * Fetch Job entity by jobReference
     * @param id
     * @return
     */
    public FundingRequest getByJobReferenceResourceNameInitialFunding(int jobReference, String resourceName, String initialFunding) {
        String queryStatement = GET_FUNDING_REQUEST_BASE_SQL + WHERE + JOB_REFERENCE + AND + RESOURCE_NAME + AND + INITIAL_FUNDING;

        queryStatement = dbUtilityHelper.dbNameSchemaReplacement(queryStatement);

        logger.debug("getByJobReferenceResourceNameInitialFunding: " + queryStatement);

        Query query =  entityManager.createNativeQuery(queryStatement, FundingRequest.class);
        query.setParameter("jobReference", jobReference);
        query.setParameter("resourceName", resourceName);
        query.setParameter("initialFunding", initialFunding);
        return (FundingRequest) query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<FundingRequest> getByJobReference(int jobReference) {
        String queryStatement = GET_FUNDING_REQUEST_BASE_SQL + WHERE + JOB_REFERENCE;

        queryStatement = dbUtilityHelper.dbNameSchemaReplacement(queryStatement);
        logger.debug("getByJobReference: " + queryStatement);

        Query query =  entityManager.createNativeQuery(queryStatement, FundingRequest.class);
        query.setParameter("jobReference", jobReference);

        return query.getResultList();
    }

}