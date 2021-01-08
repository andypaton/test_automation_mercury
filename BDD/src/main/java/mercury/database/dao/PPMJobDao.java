package mercury.database.dao;

import mercury.database.models.PPMJob;
import mercury.helpers.DbUtilityHelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class PPMJobDao {
    @Autowired private DbUtilityHelper dbUtilityHelper;
    @Autowired private EntityManager entityManager;

    private static final Logger logger = LogManager.getLogger();

    private static final String GET_PPM_RESOURCE_STATUS_SQL = "SELECT * FROM " +
            "%portaldb.uvw_report_PPMScheduleAll_ART_Active";


    private static final String GET_PPM_JOB_COMPLETION_STATUS = "SELECT * FROM " +
            "%portaldb.uvw_report_PPMScheduleAll_ART";



    @SuppressWarnings("unchecked")
    public List<PPMJob> getAll() {
        return entityManager.createQuery("from mercury.database.models.PPMJob").getResultList();
    }

    /**
     * Fetch Job entity by jobReference
     * @param id
     * @param ppmid
     * @param jobReference
     * @return
     */
    public PPMJob getPPMJobStatusByPpmId(Integer jobReference ) {
        String sql = GET_PPM_RESOURCE_STATUS_SQL + " WHERE PPMID = :jobReference";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getByJobReference: " + sql);
        try {
            Query query = entityManager.createNativeQuery(sql, PPMJob.class);
            query.setParameter("jobReference", jobReference);
            return (PPMJob) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public PPMJob getPPMJobCompletionStatusByPpmId(Integer jobReference) {
        String sql = GET_PPM_JOB_COMPLETION_STATUS + " WHERE PPMID = :jobReference";

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        logger.debug("getByJobReference: " + sql);
        try {
            Query query = entityManager.createNativeQuery(sql, PPMJob.class);
            query.setParameter("jobReference", jobReference);
            return (PPMJob) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
