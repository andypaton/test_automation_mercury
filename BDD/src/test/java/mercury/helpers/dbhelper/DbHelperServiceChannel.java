package mercury.helpers.dbhelper;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mercury.database.config.DbConfigV2;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfigV2.class)
public class DbHelperServiceChannel {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private NamedParameterJdbcTemplate jdbc_serviceChannel;
    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;
    @Autowired private NamedParameterJdbcTemplate jdbc_serviceChannelRecon;
    @Autowired private NamedParameterJdbcTemplate jdbc_testCity;

    public Map<String, Object> getMercuryJobDetails(int jobReference) {
        String sql = "SELECT TOP(1) j.JobReference, l.Id LocationId, l.Name Location, at.Id AssetTypeId,"
                + " at.Name AssetType, ast.Id AssetSubTypeId, ast.Name AssetSubType,"
                + " ac.Id AssetClassificationId, ac.Name AssetClassification, fp.Id PriorityId,"
                + " ft.Name FaultType, j.Description, j.JobTypeId, js.Name JobStatus,"
                + " j.CreatedOn JobCreatedOn, s.SiteCode, s.Address1, s.Address2, s.County,"
                + " s.Postcode, s.TelNo, qas.FundingRouteID, jrr.RepairTargetDate,"
                + " ft.Id FaultTypeId"
                + " FROM Job j"
                + " JOIN AssetClassification ac ON j.AssetClassificationId = ac.Id"
                + " JOIN AssetSubType ast ON ac.AssetSubTypeId = ast.Id"
                + " JOIN AssetType at ON ast.AssetTypeId = at.Id"
                + " JOIN Location l ON j.LocationId = l.Id"
                + " JOIN FaultPriority fp ON j.RepairPriorityId = fp.Id"
                + " JOIN FaultType ft ON j.FaultTypeId = ft.Id"
                + " JOIN JobStatus js ON j.JobStatusId = js.Id"
                + " JOIN Site s ON j.SiteId = s.Id"
                + " LEFT JOIN Portal.vw_QuoteApprovalScenarios qas ON j.JobReference = qas.FaultId"
                + " LEFT JOIN JobRepairResponse jrr ON j.Id = jrr.JobId"
                + " WHERE j.JobReference = %d";

        sql = String.format(sql, jobReference);
        logger.debug("getMercuryJobDetails: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            logger.debug("Exception which causes null value - " + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getMercuryPpmJobDetails(int jobReference) {
        String sql = "SELECT ppm.PpmStatusId, ppmt.Id PpmTypeId, ppmt.Name PpmType,"
                + " s.SiteCode, s.Address1, s.Town, s.County, s.Postcode, s.TelNo"
                + " FROM Ppm ppm"
                + " JOIN PpmType ppmt ON ppm.PpmTypeId = ppmt.Id"
                + " JOIN Site s ON ppm.SiteId = s.Id"
                + " WHERE ppm.Id = %d";

        sql = String.format(sql, jobReference);
        logger.debug("getMercuryPpmJobDetails: " + sql);
        try {
            return jdbc_helpdesk.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            logger.debug("Exception which causes null value - " + e.getMessage());
            return null;
        }
    }

    public String getServiceChannelEquipmentName(int assetTypeId, int assetSubTypeId, int assetClassificationId) {
        String sql = "SELECT ServiceChannelEquipmentName"
                + " FROM EquipmentMapping"
                + " WHERE MercuryAssetTypeId = %d"
                + " AND MercuryAssetSubTypeId = %d"
                + " AND MercuryAssetClassificationId = %d";

        sql = String.format(sql, assetTypeId, assetSubTypeId, assetClassificationId);
        logger.debug("getServiceChannelEquipmentName: " + sql);
        try {
            return jdbc_serviceChannel.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            logger.debug("Exception which causes null value - " + e.getMessage());
            return null;
        }
    }

    public String getServiceChannelProblemCode(int faultTypeId) {
        String sql = "SELECT ServiceChannelProblemCode"
                + " FROM ProblemCodeMapping"
                + " WHERE MercuryFaultTypeId = %d";

        sql = String.format(sql, faultTypeId);
        logger.debug("getServiceChannelProblemCode: " + sql);
        try {
            return jdbc_serviceChannel.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            logger.debug("Exception which causes null value - " + e.getMessage());
            return null;
        }
    }

    public String getServiceChannelArea(int locationId) {
        String sql = "SELECT TOP(1) ServiceChannelAreaName"
                + " FROM AreaMapping"
                + " WHERE MercuryLocationId = %d";

        sql = String.format(sql, locationId);
        logger.debug("getServiceChannelArea: " + sql);
        try {
            return jdbc_serviceChannel.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            logger.debug("Exception which causes null value - " + e.getMessage());
            return null;
        }
    }

    public String getServiceChannelTradeName(int assetTypeId, int assetSubTypeId, int assetClassificationId) {
        String sql = "SELECT ServiceChannelTradeName"
                + " FROM TradeMapping"
                + " WHERE MercuryAssetTypeId = %d"
                + " AND MercuryAssetSubTypeId = %d"
                + " AND MercuryAssetClassificationId = %d";

        sql = String.format(sql, assetTypeId, assetSubTypeId, assetClassificationId);
        logger.debug("getServiceChannelTradeName: " + sql);
        try {
            return jdbc_serviceChannel.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            logger.debug("Exception which causes null value - " + e.getMessage());
            return null;
        }
    }

    public String getServiceChannelPpmTradeName(int ppmTypeId) {
        String sql = "SELECT ServiceChannelTradeName"
                + " FROM TradeMapping"
                + " WHERE MercuryPPMTypeId = %d";

        sql = String.format(sql, ppmTypeId);
        logger.debug("getServiceChannelPpmTradeName: " + sql);
        try {
            return jdbc_serviceChannel.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            logger.debug("Exception which causes null value - " + e.getMessage());
            return null;
        }
    }

    public String getServiceChannelPriority(int priorityId) {
        String sql = "SELECT ServiceChannelPriority"
                + " FROM PriorityMapping"
                + " WHERE MercuryPriorityId = %d";

        sql = String.format(sql, priorityId);
        logger.debug("getServiceChannelPriority: " + sql);
        try {
            return jdbc_serviceChannel.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            logger.debug("Exception which causes null value - " + e.getMessage());
            return null;
        }
    }

    public String getServiceChannelPpmPriority(String priorityName) {
        String sql = "SELECT ServiceChannelPriority"
                + " FROM PriorityMapping"
                + " WHERE MercuryPriorityName = '%s'";

        sql = String.format(sql, priorityName);
        logger.debug("getServiceChannelPpmPriority: " + sql);
        try {
            return jdbc_serviceChannel.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            logger.debug("Exception which causes null value - " + e.getMessage());
            return null;
        }
    }

    public String getServiceChannelCategory(String jobType, Integer fundingRouteId) {
        String sql = "SELECT ServiceChannelCategory"
                + " FROM CategoryMapping"
                + " WHERE MercuryCategoryName = '%s'";

        sql = String.format(sql, jobType);

        if (fundingRouteId != null) {
            sql += " AND MercuryFundingRouteId = %d";
            sql = String.format(sql, fundingRouteId);
        }
        logger.debug("getServiceChannelCategory: " + sql);
        try {
            return jdbc_serviceChannel.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            logger.debug("Exception which causes null value - " + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getServiceChannelPpmStatus(int ppmStatusId) {
        String sql = "SELECT ServiceChannelStatus, ServiceChannelExtendedStatus"
                + " FROM PpmResetStatusMapping"
                + " WHERE PpmStatusId = %d";

        sql = String.format(sql, ppmStatusId);
        logger.debug("getServiceChannelPpmStatus: " + sql);
        try {
            return jdbc_serviceChannel.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            logger.debug("Exception which causes null value - " + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getServiceChannelStatus(int jobTypeId, String jobStatus) {
        String sql = "SELECT ServiceChannelStatus, ServiceChannelExtendedStatus"
                + " FROM StatusMapping"
                + " WHERE MercuryJobTypeId = %d"
                + " AND MercuryJobStatusName = '%s'";

        sql = String.format(sql, jobTypeId, jobStatus);
        logger.debug("getServiceChannelStatus: " + sql);
        try {
            return jdbc_serviceChannel.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            logger.debug("Exception which causes null value - " + e.getMessage());
            return null;
        }
    }

    public String getServiceChannelAreaName(int locationId) {
        String sql = "SELECT ServiceChannelAreaName"
                + " FROM AreaMapping"
                + " WHERE MercuryLocationId = %d";

        sql = String.format(sql, locationId);
        logger.debug("getServiceChannelAreaName: " + sql);
        try {
            return jdbc_serviceChannel.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            logger.debug("Exception which causes null value - " + e.getMessage());
            return null;
        }
    }

    public String getServiceChannelAssetTypeName(int assetTypeId, int assetSubTypeId, int assetClassificationId) {
        String sql = "SELECT ServiceChannelAssetTypeName"
                + " FROM AssetMapping"
                + " WHERE MercuryAssetTypeId = %d"
                + " AND MercuryAssetSubTypeId = %d"
                + " AND MercuryAssetClassificationId = %d";

        sql = String.format(sql, assetTypeId, assetSubTypeId, assetClassificationId);
        logger.debug("getServiceChannelAssetTypeName: " + sql);
        try {
            return jdbc_serviceChannel.queryForObject(sql, new MapSqlParameterSource(), String.class);
        } catch(Exception e) {
            logger.debug("Exception which causes null value - " + e.getMessage());
            return null;
        }
    }

    public Integer getServiceChannelTrackingNumber(int jobReference) {
        String sql = "SELECT ExternalJobReference"
                + " FROM JobLinkedExternalReferences"
                + " WHERE MercuryJobReference = %d";

        sql = String.format(sql, jobReference);
        logger.debug("getServiceChannelTrackingNumber: " + sql);
        try {
            return jdbc_helpdesk.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
        } catch(Exception e) {
            logger.debug("Exception which causes null value - " + e.getMessage());
            return null;
        }
    }

    public List<Integer> getJobsWithoutWorkOrderOnSC() {
        String sql = "SELECT MercuryJobReference"
                + " FROM WorkOrdersNotOnSC"
                + " WHERE IsPpm = 0";

        logger.debug("getJobsWithoutWorkOrderOnSC: " + sql);
        try {
            return jdbc_serviceChannelRecon.queryForList(sql, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            logger.debug("Exception which causes null value - " + e.getMessage());
            return null;
        }
    }

    public void purgeJobChannelEventTable() {
        String sql = "TRUNCATE TABLE jc.JobChannelEvent";

        logger.debug("purgeJobChannelEventTable: " + sql);
        try {
            jdbc_testCity.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Exception " + e.getMessage());
        }
    }

    public void purgeSctServiceChannelEventTable() {
        String sql = "TRUNCATE TABLE sct.ServiceChannelEvent";

        logger.debug("purgeSctServiceChannelEventTable: " + sql);
        try {
            jdbc_testCity.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Exception " + e.getMessage());
        }
    }

    public void purgeSctFaultTable() {
        String sql = "TRUNCATE TABLE sct_fault.fault";

        logger.debug("purgeSctFaultTable: " + sql);
        try {
            jdbc_testCity.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Exception " + e.getMessage());
        }
    }

    public void purgeSclServiceChannelEventTable() {
        String sql = "TRUNCATE TABLE scl.ServiceChannelEvent";

        logger.debug("purgeSclServiceChannelEventTable: " + sql);
        try {
            jdbc_testCity.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Exception " + e.getMessage());
        }
    }

    public void purgeSclFaultTable() {
        String sql = "TRUNCATE TABLE scl_fault.fault";

        logger.debug("purgeSclFaultTable: " + sql);
        try {
            jdbc_testCity.update(sql, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.debug("Exception " + e.getMessage());
        }
    }

    public Map<String, Object> getWorkOrderQueueDetails(String jobId) {
        String sql = "SELECT jce.id AS [CorrelationId], jce.JobId AS [JobId], jce.Title AS [EventTitle],"
                + " jce.LoggedAt AS [EventLoggedAt], jce.PublishedAt AS [EventPublishedAt],"
                + " scte.TransformedEventJson AS [TransformedEventJson], scte.PublishedAt AS [TransformedPublishedAt],"
                + " sctf.Exception AS [TransformException], sctf.Timestamp AS [TransformExceptionAt],"
                + " scle.AlreadyExisted AS [AlreadyExistedInServiceChannel], scle.SentToServiceChannelAt AS [SentToServiceChannelAt],"
                + " scle.ServiceChannelEventJson AS [ServiceChannelPayload], scle.WorkOrderId AS [WorkOrderId],"
                + " scle.FinalizedAt AS [FinalizedAt], sclf.Exception AS [LoadException], sclf.Timestamp AS [LoadExceptionAt]"
                + " FROM jc.JobChannelEvent jce"
                + " LEFT JOIN sct.ServiceChannelEvent scte ON scte.CorrelationId = jce.id"
                + " LEFT JOIN sct_fault.fault sctf ON sctf.correlationid = jce.id"
                + " LEFT JOIN scl.ServiceChannelEvent scle ON scle.CorrelationId = jce.id"
                + " LEFT JOIN scl_fault.fault sclf ON sclf.correlationid = jce.id"
                + " WHERE jce.jobid = '%s'";

        sql = String.format(sql, jobId);
        logger.debug("getWorkOrderQueueDetails: " + sql);
        try {
            return jdbc_testCity.queryForList(sql, new MapSqlParameterSource()).get(0);
        } catch(Exception e) {
            logger.debug("Exception which causes null value - " + e.getMessage());
            return null;
        }
    }
}
