package mercury.database.dao;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.SiteVisitCylinderDetails;

@Repository
public class SiteVisitCylinderDetailsDao {

    private static final Logger logger = LogManager.getLogger();

    private static final String FIELD_LIST = " svcd.*  ";

    private static final String SELECT_TOP_SQL = "SELECT TOP(1) ";

    private static final String GET_SITE_VISIT_GAS_DETAILS_SELECT = FIELD_LIST +
            " FROM Job j LEFT JOIN ResourceAssignment ra ON j.Id = ra.JobId " +
            " LEFT JOIN SiteVisits sv ON ra.Id = sv.ResourceAssignmentId  " +
            " LEFT JOIN SiteVisitGasDetails svgd ON svgd.SiteVisitId = sv.Id " +
            " LEFT JOIN SiteVisitCylinderDetails svcd ON svcd.SiteVisitGasDetailsId = sv.id";

    private static final String GET_SITE_VISIT_GAS_DETAILS_BY_JOB_REFERENCE_WHERE =  " WHERE j.JobReference = :jobReference ";

    private static final String GET_SITE_VISIT_GAS_DETAILS_BY_JOB_REFERENCE_SPECIFIC_WHERE =  " WHERE j.JobReference = :jobReference "
            + " AND svcd.GasSourceTypeId = :gasSourceTypeId "
            + " AND svcd.GasCylinderTypeId = :gasCylinderTypeId "
            + " AND svcd.BottleQuantity = :bottleQuantity "
            + " AND svcd.CreatedOn >= CAST(:createdOn AS datetime2(7)) ";
    private static final String UPDATED_ON_ORDER_BY = " ORDER BY svgd.UpdatedOn DESC";

    @Autowired
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<SiteVisitCylinderDetails> getAll() {
        return entityManager.createQuery("from mercury.database.models.SiteVisitGasDetails").getResultList();
    }

    public SiteVisitCylinderDetails getSiteVisitsCylinderDetailsForJobReference(Integer jobReference) {
        String jpql = SELECT_TOP_SQL + GET_SITE_VISIT_GAS_DETAILS_SELECT  + GET_SITE_VISIT_GAS_DETAILS_BY_JOB_REFERENCE_WHERE + UPDATED_ON_ORDER_BY;
        logger.debug("getSiteVisitsCylinderDetailsForJobReference :".concat(jpql));
        try {
            Query query =  entityManager.createNativeQuery(jpql, SiteVisitCylinderDetails.class);

            query.setParameter("jobReference", jobReference);
            SiteVisitCylinderDetails queryResult = (SiteVisitCylinderDetails) query.getSingleResult();
            return queryResult;
        } catch (Exception e) {
            return null;
        }
    }

    public SiteVisitCylinderDetails getSiteVisitsCylinderDetailsForJobReference(Integer jobReference, Integer gasSourceTypeId, Integer gasCylinderTypeId, BigDecimal bottleQuantity, String createdOn) {
        String jpql = SELECT_TOP_SQL + GET_SITE_VISIT_GAS_DETAILS_SELECT  + GET_SITE_VISIT_GAS_DETAILS_BY_JOB_REFERENCE_SPECIFIC_WHERE + UPDATED_ON_ORDER_BY;
        logger.debug("getSiteVisitsCylinderDetailsForJobReference :".concat(jpql).replaceAll(":jobReference", jobReference.toString())
                .replaceAll(":gasSourceTypeId", gasSourceTypeId.toString())
                .replaceAll(":gasCylinderTypeId", gasCylinderTypeId.toString())
                .replaceAll(":bottleQuantity", bottleQuantity.toString())
                .replaceAll(":createdOn", createdOn));
        try {
            Query query =  entityManager.createNativeQuery(jpql, SiteVisitCylinderDetails.class);

            query.setParameter("jobReference", jobReference);
            query.setParameter("gasSourceTypeId", gasSourceTypeId);
            query.setParameter("gasCylinderTypeId", gasCylinderTypeId);
            query.setParameter("bottleQuantity", bottleQuantity);
            query.setParameter("createdOn", createdOn);

            SiteVisitCylinderDetails queryResult = (SiteVisitCylinderDetails) query.getSingleResult();
            return queryResult;
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<SiteVisitCylinderDetails> getForSiteVisitId(Integer siteVisitId) {
        String sql = String.format("SELECT * FROM SiteVisitCylinderDetails WHERE Active = 1 AND SiteVisitGasDetailsId = %d", siteVisitId);
        logger.debug("getForSiteVisitId: " + sql);
        Query query =  entityManager.createNativeQuery(sql, SiteVisitCylinderDetails.class);
        return query.getResultList();
    }

}
