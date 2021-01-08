package mercury.helpers.dbhelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.object.GenericStoredProcedure;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mercury.database.config.DbConfigV2;
import mercury.helpers.DbUtilityHelper;

@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DbConfigV2.class)
public class DbHelperCompanies {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private NamedParameterJdbcTemplate jdbc_portal;
    @Autowired private NamedParameterJdbcTemplate jdbc_helpdesk;
    @Autowired private DbUtilityHelper dbUtilityHelper;
    @Autowired private DataSource helpdeskDataSource;


    public String getCompanyName(boolean isPrimary) {
        String sql = "SELECT TOP(1) Name FROM dbo.Company" +
                " WHERE IsPrimary = :isPrimary" +
                " AND Active = 1" +
                " ORDER BY NEWID()";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("isPrimary", isPrimary);

        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getCompanyType(String companyName) {
        String sql = " SELECT ct.Name FROM dbo.CompanyType ct" +
                "  INNER JOIN dbo.Company AS c ON ct.Id = c.CompanyTypeId" +
                "  WHERE c.Name LIKE :companyName";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("companyName", companyName);

        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }

    }

    public boolean isPrimaryCompany(String companyName) {
        String sql = String.format("SELECT COUNT(1)" +
                "  FROM dbo.Company" +
                "  WHERE Company.name = '%s'" +
                "  AND Company.IsPrimary = 1", companyName);
        MapSqlParameterSource params = new MapSqlParameterSource();
        boolean isPrimary = jdbc_helpdesk.queryForObject(sql, params, boolean.class);
        return isPrimary;
    }

    public void updateCompanyToPrimary(String companyName) {
        String sql = "UPDATE dbo.Company SET isPrimary = 1"
                + "  WHERE Company.name =:companyName ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("companyName", companyName);
        logger.debug("updateCompanyToPrimary: " + sql);
        jdbc_helpdesk.update(sql, params);
    }

    public boolean isPrimaryCompanyAvailable() {
        String sql = "SELECT COUNT (1)" +
                "  FROM Company" +
                "  WHERE IsPrimary = 1" +
                "  AND Active = 1";
        MapSqlParameterSource params = new MapSqlParameterSource();
        int count = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        return count == 1;
    }

    public void resetToPrimaryCompany(String primaryCompanyName) {
        StoredProcedure procedure = new GenericStoredProcedure();
        procedure.setDataSource(helpdeskDataSource);
        String sql = "%testdb.usp_UpdatePrimaryCompany";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        procedure.setSql(sql);
        procedure.setFunction(false);
        SqlParameter[] parameters = { new SqlParameter(Types.VARCHAR) };

        procedure.setParameters(parameters);
        procedure.compile();
        logger.debug("usp_UpdatePrimaryCompany: execute " + sql + " @primaryCompanyName = " + primaryCompanyName);
        procedure.execute(primaryCompanyName);
    }

    public void resetToNonPrimaryCompany(String companyName) {
        String sql = String.format(" UPDATE dbo.Company" +
                "  SET IsPrimary = 0" +
                "  WHERE Company.Name = '%s'",companyName);
        logger.debug("resetPrimaryCompany: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public void resetToActiveCompany() {
        String sql = "UPDATE TOP(1) dbo.Company"  +
                "  SET Active = 1" +
                "  WHERE IsPrimary = 0" +
                "  AND Active = 0";
        logger.debug("resetToActiveCompany: " + sql);
        jdbc_helpdesk.update(sql, new MapSqlParameterSource());
    }

    public boolean isCompanyAlias(String companyName, String alias) {
        String sql = "SELECT COUNT(1) FROM dbo.CompanyAlias ca" +
                "  INNER JOIN dbo.Company AS c ON ca.CompanyId = c.Id" +
                "  WHERE c.Name = '%s'" +
                "  AND ca.Name = '%s'";
        sql = String.format(sql, companyName, alias);
        MapSqlParameterSource params = new MapSqlParameterSource();
        int count = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
        return count == 1;
    }

    public List<String> getCompanyRuleAssignments(String companyName) {
        String sql = "SELECT cart.name assignmentRule FROM CompanyAssignmentRuleType cart" +
                "    INNER JOIN CompanyAssignmentRules As car ON cart.id = car.CompanyAssignmentRuleTypeId" +
                "    INNER JOIN dbo.Company AS c ON car.CompanyId = c.id" +
                "    WHERE c.Name = :companyName";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("companyName", companyName);
        logger.debug("getCompanyRuleAssignments: " + sql);
        List<String> assignmentRules = jdbc_helpdesk.query(sql, params, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return (rs.getString("assignmentRule"));
            }
        });
        return assignmentRules;
    }

    public boolean isFundingRouteAssignmentRule(String companyName, String ruleDetail) {
        String sql = "Select COUNT(1)" +
                "    FROM dbo.FundingRoute fr" +
                "    INNER JOIN dbo.CompanyAssignmentRules car ON fr.Id = car.RuleDetailId" +
                "    INNER JOIN dbo.Company c ON car.companyid = c.Id" +
                "    WHERE c.Name = '%s' AND fr.Name = '%s'";
        sql = String.format(sql, companyName, ruleDetail);
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            int count = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return count == 1;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return false;
        }
    }

    public boolean isSiteTypesAssignmentRule(String companyName, String ruleDetail) {
        String sql = "Select COUNT(1)" +
                "    FROM dbo.SiteTypes st" +
                "    INNER JOIN dbo.CompanyAssignmentRules car ON st.Id = car.RuleDetailId" +
                "    INNER JOIN dbo.Company c ON car.companyid = c.Id" +
                "    WHERE c.Name = '%s' AND st.Name = '%s'";
        sql = String.format(sql, companyName, ruleDetail);
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            int count = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return count == 1;
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return false;
        }
    }

    public String getUnusedFundingRouteRule() {
        String sql = "SELECT TOP(1) fr.Name" +
                "  FROM dbo.FundingRoute fr" +
                "  LEFT JOIN dbo.CompanyAssignmentRules AS car ON car.RuleDetailId = fr.Id" +
                "  WHERE car.RuleDetailId IS NULL" +
                "  AND fr.Active = 1" +
                "  ORDER BY NEWID()";
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public void deleteRandomUsedFundingRouteRule() {
        String sql = "DELETE FROM dbo.CompanyAssignmentRules" +
                "  WHERE Id IN (SELECT TOP(1) car.id FROM dbo.CompanyAssignmentRules car LEFT JOIN dbo.FundingRoute fr ON car.RuleDetailId = fr.Id" +
                "  WHERE fr.Active = 1 ORDER BY NEWID())";
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("deleteRandomUsedFundingRouteRule: " + sql);
        jdbc_helpdesk.update(sql, params);
    }

    public void deleteRandomUsedSiteTypeRule() {
        String sql = "DELETE FROM dbo.CompanyAssignmentRules" +
                "  WHERE Id IN (SELECT TOP(1) car.id FROM dbo.CompanyAssignmentRules car LEFT JOIN dbo.SiteTypes st ON car.RuleDetailId = st.Id" +
                "  WHERE st.Active = 1 ORDER BY NEWID())";
        MapSqlParameterSource params = new MapSqlParameterSource();
        logger.debug("deleteRandomUsedFundingRouteRule: " + sql);
        jdbc_helpdesk.update(sql, params);
    }

    public String getUnusedSiteTypeRule() {
        String sql = "SELECT TOP(1) st.Name" +
                "  FROM dbo.SiteTypes st" +
                "  LEFT JOIN dbo.CompanyAssignmentRules AS car ON car.RuleDetailId = st.Id" +
                "  WHERE car.RuleDetailId IS NULL" +
                "  AND st.Active = 1" +
                "  ORDER BY NEWID()";
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String selectRandomSiteTypeName() {
        String sql = "SELECT TOP(1) Name" +
                "    FROM dbo.SiteTypes st";
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getUsedAssignmentRuleCompanyName(String ruleType) {
        String sql = "SELECT TOP(1) c.Name FROM dbo.Company c" +
                "  INNER JOIN dbo.CompanyAssignmentRules AS car ON c.id = car.companyid" +
                "  INNER JOIN dbo.CompanyAssignmentRuleType AS cart ON cart.id = car.companyAssignmentRuleTypeId" +
                "  WHERE cart.Name = :ruleType" +
                "  ORDER BY NEWID()";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ruleType", ruleType);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getIsClientProcessedCompanyName() {
        String sql = " SELECT TOP(1) c.Name FROM dbo.Company c" +
                "  WHERE c.IsClientProcessed = 1" +
                "  AND c.Active = 1" +
                "  ORDER BY NEWID()";
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }



    public String getSiteTypeRuleDetail(String companyName) {
        String sql = "SELECT TOP(1) st.Name FROM dbo.SiteTypes st" +
                "  INNER JOIN dbo.CompanyAssignmentRules AS car ON st.Id = car.ruledetailid" +
                "  INNER JOIN dbo.Company AS c ON car.companyid = c.Id" +
                "  WHERE car.CompanyAssignmentRuleTypeID = 3" +
                "  AND c.Name = :companyName" +
                "  ORDER BY NEWID()";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("companyName", companyName);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getFundingRouteRuleDetail(String companyName) {
        String sql = "SELECT TOP(1) fr.Name FROM dbo.FundingRoute fr" +
                "  INNER JOIN dbo.CompanyAssignmentRules AS car ON fr.Id = car.ruledetailid" +
                "  INNER JOIN dbo.Company AS c ON  car.companyid = c.Id" +
                "  WHERE car.CompanyAssignmentRuleTypeID = 2" +
                "  AND c.Name = :companyName" +
                "  ORDER BY NEWID()";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("companyName", companyName);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public boolean isAssignedRuleDetail(String ruleDetail) {
        String sql  = "SELECT COUNT(1) FROM dbo.CompanyAssignmentRules car" +
                "  LEFT JOIN dbo.SiteTypes AS st ON st.id = car.ruledetailid" +
                "  LEFT JOIN dbo.FundingRoute AS fr ON fr.Id = car.ruledetailid" +
                "  WHERE st.Name = :ruleDetail" +
                "  OR fr.Name = :ruleDetail";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ruleDetail", ruleDetail);
        try {
            int count = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return count == 1;

        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return false;
        }
    }

    public String getCompanyNameForRuleDetail(String ruleDetail, String ruleDetailType) {
        String sql = "SELECT c.Name FROM dbo.CompanyAssignmentRules car" +
                "  INNER JOIN dbo.Company AS c ON  car.companyid = c.Id" +
                "  LEFT JOIN dbo.SiteTypes AS st ON car.ruledetailid = st.id" +
                "  LEFT JOIN dbo.FundingRoute AS fr ON car.ruledetailid = fr.id" +
                "  LEFT JOIN dbo.CompanyAssignmentRuleType AS cart ON car.CompanyAssignmentRuleTypeId = cart.Id" +
                "  WHERE cart.Name = :ruleDetailType"+
                "  AND (fr.Name = :ruleDetail" +
                "  OR st.Name = :ruleDetail)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ruleDetailType", ruleDetailType);
        params.addValue("ruleDetail", ruleDetail);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public Integer getCompanyPOCount(String companyName) {
        String sql = "SELECT COUNT(1) from %portaldb.tblPO PO" +
                "  INNER JOIN %helpdeskdb.Company AS c ON c.ID = po.fld_int_CompanyId" +
                "  WHERE c.Name = :companyName";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("companyName", companyName);
        try {
            return jdbc_portal.queryForObject(sql, params, Integer.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public boolean isCompanyName(String companyName) {
        String sql = "SELECT COUNT(1) FROM dbo.Company" +
                "  WHERE Company.Name = :companyName";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("companyName", companyName);
        try {
            int count = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return count == 1;

        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return false;
        }
    }

    public String getCompanyWithExistingOrders(String ruleType) {
        String sql = "  SELECT TOP(1) c.Name from dbo.Company c" +
                "  INNER JOIN dbo.CompanyAssignmentRules AS car ON car.CompanyId = c.Id" +
                "  INNER JOIN dbo.CompanyAssignmentRuleType AS cart ON car.CompanyAssignmentRuleTypeId = cart.Id" +
                "  INNER JOIN %portaldb.tblPO AS PO ON c.Id = PO.fld_int_CompanyId" +
                "  WHERE cart.Name = :ruleType" +
                "  ORDER BY NEWID()";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ruleType", ruleType);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getCompanyWithExistingOrders() {
        String sql = "SELECT TOP(1) c.Name from dbo.Company c" +
                "  INNER JOIN %portaldb.tblPO AS PO ON c.Id = PO.fld_int_CompanyId" +
                "  ORDER BY NEWID()";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public boolean isPermanentAlias(String aliasName) {
        String sql = "SELECT COUNT(1) from dbo.CompanyAlias" +
                "  WHERE IsPermanent = 1" +
                "  AND Name = :aliasName";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("aliasName", aliasName);
        try {
            int count = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return count == 1;

        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return false;
        }

    }

    public String getPermanentAliasName(String companyName) {
        String sql = "SELECT TOP(1) ca.Name FROM dbo.CompanyAlias ca" +
                "  INNER JOIN %portaldb.Company AS c ON c.id = ca.CompanyId" +
                "  WHERE ca.IsPermanent = 1" +
                "  AND c.name = :companyName" +
                "  ORDER BY NEWID()";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("companyName", companyName);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public String getPermanentAliasCompanyName(String ruleType) {
        String sql = " SELECT TOP(1) c.Name from dbo.Company c" +
                "  INNER JOIN dbo.CompanyAssignmentRules AS car ON car.CompanyId = c.Id" +
                "  INNER JOIN dbo.CompanyAssignmentRuleType AS cart ON car.CompanyAssignmentRuleTypeId = cart.Id" +
                "  INNER JOIN dbo.CompanyAlias AS ca ON ca.CompanyId = c.Id" +
                "  WHERE ca.IsPermanent = 1" +
                "  AND cart.Name = :ruleType" +
                "  ORDER BY NEWID()";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ruleType", ruleType);
        try {
            return jdbc_helpdesk.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return null;
        }
    }

    public boolean isAnySiteTypeAvailable() {
        String sql = "SELECT COUNT(1) FROM dbo.CompanyAssignmentRules car" +
                "  RIGHT JOIN dbo.SiteTypes AS st ON st.id = car.ruledetailid" +
                "  WHERE st.Active = 1" +
                "  AND car.ruledetailid IS NULL";
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            int count = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return count >= 1;

        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return false;
        }
    }

    public boolean isAnyFundingRouteAvailable() {
        String sql = "SELECT COUNT(1) FROM dbo.CompanyAssignmentRules car" +
                "  RIGHT JOIN dbo.FundingRoute AS fr ON fr.Id = car.ruledetailid" +
                "  WHERE fr.Active = 1" +
                "  AND car.ruledetailid IS NULL";
        MapSqlParameterSource params = new MapSqlParameterSource();
        try {
            int count = jdbc_helpdesk.queryForObject(sql, params, Integer.class);
            return count >= 1;

        } catch (Exception e) {
            logger.debug("Error message" + e.getMessage());
            return false;
        }
    }
}

