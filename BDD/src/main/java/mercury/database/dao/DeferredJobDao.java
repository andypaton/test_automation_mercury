package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.DeferredJob;
import mercury.database.models.Incident;

@Repository
public class DeferredJobDao {

    private static final Logger logger = LogManager.getLogger();

    private static final String BASE_SQL = "SELECT DISTINCT j.Id, j.Jobreference, ra.id AS ResourceAssignmentId, ra.deferraldate AS ResourceAssignmentDeferralDate, j.DeferralDate AS JobDeferralDate, j.SiteId, jd.FaultPriority, jd.AssetType,  jd.AssetSubtype, r.name AS ResourceName, r.Id AS ResourceId, rwh.Id AS RwhId , rwh.StartAt, rwh.EndAt, nmt.name AS NotificationMethod "
            + " FROM uvw_MonitorJobDeferred jd "
            + " INNER JOIN job j ON j.Jobreference = jd.Jobreference "
            + " LEFT JOIN ResourceAssignment ra ON ra.jobId = j.id "
            + " LEFT JOIN Resource r ON r.Id = ra.resourceId "
            + " LEFT join ResourceNotificationMethod rnm ON r.Id = rnm.ResourceId "
            + " LEFT JOIN NotificationMethodType nmt ON rnm.NotificationMethodTypeId = nmt.Id "
            + " LEFT JOIN resourceWorkingHours rwh ON rwh.ResourceId = r.Id "
            + " WHERE ((rwh.startdayoftheweek = Datepart(dw, Getdate()) AND rwh.enddayoftheweek = Datepart(dw, Getdate())) or rwh.id IS NULL) "
            + " AND r.Id NOT IN (SELECT ResourceId FROM uvw_ResourceAbsence WHERE CAST(AbsenceStartAt AS date) <= CAST(GETDATE() AS date) AND CAST(GETDATE() AS date) <= CAST(AbsenceEndAt AS date)) ";

    @Autowired
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<DeferredJob> getAll() {
        return entityManager.createQuery("from mercury.database.models.Incident").getResultList();
    }

    public Incident get(Integer id) {
        return entityManager.find(Incident.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<DeferredJob> getDeferredJobs() {
        logger.debug("getDeferredJobs: " + BASE_SQL);
        Query query =  entityManager.createNativeQuery(BASE_SQL, DeferredJob.class);
        return query.getResultList();
    }

    public DeferredJob getDeferredJob(int jobId) {
        String sql = BASE_SQL + String.format(" AND j.Id=%d", jobId);
        logger.debug("getDeferredJob: " + sql);
        Query query =  entityManager.createNativeQuery(sql, DeferredJob.class);
        try {
            return (DeferredJob) query.getSingleResult();
        }
        catch(Exception e) {
            return null;
        }
    }
}
