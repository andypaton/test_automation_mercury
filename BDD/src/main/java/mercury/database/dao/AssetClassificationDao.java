package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.AssetClassification;


@Repository
public class AssetClassificationDao {

    private static final Logger logger = LogManager.getLogger();

    private static final String SQL = "SELECT acs.id, acs.SiteId, acs.AssetClassificationId, acscm.ResourceId, acscm.Priority FROM AssetClassificationSite acs " +
            "INNER JOIN AssetClassificationSiteContractorMapping acscm ON acscm.AssetClassificationSiteId = acs.Id " +
            "WHERE ResourceId IN (SELECT MIN(resourceId) FROM AssetClassificationSiteContractorMapping) " +
            "AND SiteId IN (SELECT MIN(siteId) FROM AssetClassificationSite)";


    @Autowired
    private EntityManager entityManager;


    @SuppressWarnings("unchecked")
    public List<AssetClassification> getRandomAssetClassificationList() {
        logger.debug("getAssetClassificationResourceSite: " + SQL);
        return (List<AssetClassification>) entityManager.createNativeQuery(SQL, AssetClassification.class).getResultList();
    }
    
}
