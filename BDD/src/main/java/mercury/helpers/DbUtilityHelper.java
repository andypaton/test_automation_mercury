package mercury.helpers;

import org.springframework.beans.factory.annotation.Autowired;

public class DbUtilityHelper {

    @Autowired private String jdbc_portal_name;
    @Autowired private String jdbc_helpdesk_name;
    @Autowired private String jdbc_test_name;
    @Autowired private String jdbc_portal_schema_name;
    @Autowired private String jdbc_helpdesk_schema_name;
    @Autowired private String jdbc_test_schema_name;

    public String dbNameSchemaReplacement(String sqlStatement) {
        String sql = sqlStatement;
        sql = sql.replaceAll("%portaldb", jdbc_portal_name + "." + jdbc_portal_schema_name);
        sql = sql.replaceAll("%helpdeskdb", jdbc_helpdesk_name + "." + jdbc_helpdesk_schema_name);
        sql = sql.replaceAll("%testdb", jdbc_test_name + "." + jdbc_test_schema_name);
        sql = sql.replaceAll("%iosdb", jdbc_portal_name + ".ios");
        sql = sql.replaceAll("%edidb", jdbc_portal_name + ".edi");
        sql = sql.replaceAll("%compliancedb", jdbc_portal_name + "." + "Compliance");
        return sql;
    }
}
