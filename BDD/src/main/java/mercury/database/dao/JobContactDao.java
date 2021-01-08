package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.JobContact;

@Repository
public class JobContactDao {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private EntityManager entityManager;

    private String GET_JOB_CONTACT_SQL = "SELECT " +
            "r.Id Id,  " +
            "r.Name ContactName,  " +
            "COALESCE(rp.Alias, rp.Name) ContactType,  " +
            "rpn.PhoneNumber ContactNumber " +
            "FROM " +
            "Job AS j  " +
            "LEFT JOIN JobContact jc ON j.Id = jc.JobId " +
            "LEFT JOIN Caller c ON jc.CallerId = c.id " +
            "LEFT JOIN ResourceCaller AS rc ON rc.Id = c.Id " +
            "LEFT JOIN Resource AS r  ON r.Id = rc.ResourceId " +
            "LEFT JOIN ResourceProfile rp  ON r.ResourceProfileId = rp.Id " +
            "LEFT JOIN ResourcePhoneNumber rpn  ON r.Id = rpn.ResourceId  " +
            "WHERE " +
            "j.id = %s " +
            "AND r.Id IS NOT NULL ";

    private String GET_JOB_CONTACT_UNION_SQL = "UNION " +
            "SELECT  cc.Id Id,       " +
            "cc.Name ContactName,      " +
            "cc.JobTitle ContactType,   " +
            "cc.PhoneNumber ContactNumber  " +
            "FROM " +
            "Job AS j " +
            "INNER  JOIN JobContact jc  ON j.Id = jc.JobId " +
            "INNER  JOIN ClientCaller AS cc  ON jc.CallerId = cc.Id  " +
            "WHERE j.id =  %s ";

    @SuppressWarnings("unchecked")
    public List<JobContact> getAll() {
        return entityManager.createQuery("from mercury.database.models.JobContact").getResultList();
    }


    /**
     * Fetch JobContact entity by primary key
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<JobContact> get(String jobId) {
        String queryStatement = String.format(GET_JOB_CONTACT_SQL, jobId).concat(String.format(GET_JOB_CONTACT_UNION_SQL, jobId));
        String methodName = new Object() {}
        .getClass()
        .getEnclosingMethod()
        .getName();
        logger.debug(this.getClass().getName() + ":" + methodName + ":" + queryStatement);
        try {
            Query query =  entityManager.createNativeQuery(queryStatement, JobContact.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return null;
        }

    }
}
