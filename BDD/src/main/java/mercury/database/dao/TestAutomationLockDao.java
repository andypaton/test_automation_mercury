package mercury.database.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mercury.database.models.TestAutomationLock;
import mercury.helpers.DbUtilityHelper;

@Repository
public class TestAutomationLockDao {

    private static final Logger logger = LogManager.getLogger();

    @Autowired
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @Autowired NamedParameterJdbcTemplate jdbc_test;
    @Autowired private DbUtilityHelper dbUtilityHelper;

    /**
     * Create new TestAutomationLock entity
     * @param
     */
    @Transactional
    public void create(TestAutomationLock lock) {
        String sql = "INSERT INTO %testdb.TestAutomation_Locks(Reference, Type, CreatedOn, Reason, Detail) " +
                "VALUES ( :reference, :type, GETUTCDATE(), :reason, :detail)";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            params.addValue("reference", lock.getReference());
            params.addValue("type", lock.getType());
            params.addValue("reason", lock.getReason());
            params.addValue("detail", lock.getDetail());
            jdbc_test.update(sql, params);
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }

    }

    /**
     * Update existing contact entity
     * @param c
     */
    @Transactional
    public void delete(TestAutomationLock lock) {
        String sql = "DELETE %testdb.TestAutomation_Locks " +
                "WHERE Reference = :reference " +
                "AND Type = :type " +
                "AND Reason = :reason";
        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);
        MapSqlParameterSource params = new MapSqlParameterSource();

        try {
            params.addValue("reference", lock.getReference());
            params.addValue("type", lock.getType());
            params.addValue("reason", lock.getReason());
            jdbc_test.update(sql, params);
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }
}
