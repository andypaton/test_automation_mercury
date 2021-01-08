package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.Reason;


@Repository
public class ReasonDao {

    private static final Logger logger = LogManager.getLogger();

    private static final String GET_REASON_BASE_SQL = "SELECT DISTINCT  Id, Code, Name, ReasonCategory,  " +
            "CreatedOn, CreatedBy, UpdatedOn, UpdatedBy, Active " +
            "FROM Reason ";
    private static final String GET_REASON_WHERE_SQL = " WHERE ReasonCategory = :reasonCategory AND Active = 1";
    private static final String GET_REASON_BY_NAME_CATEGORY_SQL = "WHERE Name = :name AND ReasonCategory = :reasonCategory AND Active = 1 ";
    private static final String GET_REASON_BY_ID_SQL = "WHERE Id = :id ";

    private static final String GET_RANDOM_REASON_BASE_SQL = "SELECT TOP(1)  Id, Code, Name, ReasonCategory,  " +
            "CreatedOn, CreatedBy, UpdatedOn, UpdatedBy, Active " +
            "FROM Reason ";
    private static final String GET_RANDOM_REASON_WHERE_SQL = " WHERE ReasonCategory = :reasonCategory AND Active = 1";
    private static final String GET_RANDOM_REASON_ORDER_SQL =  " ORDER BY NEWID() ";

    @Autowired
    private EntityManager entityManager;

    /**
     * Fetch Decline Reasons for given reasonCategory
     * @param reasonCategory
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Reason> getReasonByReasonCategory(String reasonCategory) {
        try {
            String jpql = GET_REASON_BASE_SQL + GET_REASON_WHERE_SQL;
            logger.debug("getReasonCategory: " +jpql);

            Query query =  entityManager.createNativeQuery(jpql, Reason.class);
            query.setParameter("reasonCategory", reasonCategory);

            return query.getResultList();
        } catch (Exception e) {
            logger.debug(e.getMessage());
            throw e;
        }
    }

    /**
     * Fetch an Reason for given reasonCategory
     * @param reasonCategory
     * @return single record
     */
    public Reason getRandomReasonByReasonCategory(String reasonCategory) {
        try {
            String jpql = GET_RANDOM_REASON_BASE_SQL +
                    GET_RANDOM_REASON_WHERE_SQL +
                    GET_RANDOM_REASON_ORDER_SQL;
            logger.debug(jpql);
            Query query =  entityManager.createNativeQuery(jpql, Reason.class);
            query.setParameter("reasonCategory", reasonCategory);
            return (Reason) query.getSingleResult();
        } catch (Exception e) {
            logger.debug(e.getMessage());
            throw e;
        }
    }

    /**
     * Fetch an Reason for given name and reasonCategory
     * @param name
     * @param reasonCategory
     * @return single record
     */
    public Reason getReasonByname(String name, String reasonCategory) {
        try {
            String jpql = GET_REASON_BASE_SQL + GET_REASON_BY_NAME_CATEGORY_SQL;
            logger.debug(jpql);
            Query query = entityManager.createNativeQuery(jpql, Reason.class);
            query.setParameter("reasonCategory", reasonCategory);
            query.setParameter("name", name);
            return (Reason) query.getSingleResult();
        } catch (Exception e) {
            logger.debug(e.getMessage());
            throw e;
        }
    }

    public Reason getReasonById(int id) {
        try {
            String jpql = GET_REASON_BASE_SQL + GET_REASON_BY_ID_SQL;
            String queryStatement = String.format(jpql, id);
            logger.debug(queryStatement);
            Query query = entityManager.createNativeQuery(queryStatement, Reason.class);
            query.setParameter("id", id);

            return (Reason) query.getSingleResult();
        } catch (Exception e) {
            logger.debug(e.getMessage());
            throw e;
        }
    }
}
