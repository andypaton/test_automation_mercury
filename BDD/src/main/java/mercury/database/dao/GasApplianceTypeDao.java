package mercury.database.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.GasApplianceType;
import mercury.helpers.DbUtilityHelper;

@Repository
public class GasApplianceTypeDao {

    @Autowired
    private DbUtilityHelper dbUtilityHelper;

    @Autowired
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    private static final Logger logger = LogManager.getLogger();

    private static final String GET_GAS_APPLIANCE_TYPE = "SELECT * FROM GasApplianceType ";

    public GasApplianceType get(Integer id) {
        return entityManager.find(GasApplianceType.class, id);
    }

    public GasApplianceType getGasApplianceTypeDao(String name) throws Exception {
        String sql = GET_GAS_APPLIANCE_TYPE;

        sql = dbUtilityHelper.dbNameSchemaReplacement(sql);

        Query query = entityManager.createNativeQuery(sql, GasApplianceType.class);

        query.setParameter("name", name);

        logger.debug("getGasApplianceTypeDao: " + sql);
        try {
            GasApplianceType queryResult = (GasApplianceType) query.getSingleResult();
            return queryResult;
        } catch (Exception e) {
            return null;
        }
    }
}
