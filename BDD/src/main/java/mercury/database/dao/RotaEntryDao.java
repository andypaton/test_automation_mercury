package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.RotaEntry;

@Repository
public class RotaEntryDao {

    private static final Logger logger = LogManager.getLogger();

    private static final String BASE_SQL = "SELECT DISTINCT oss.SiteId, ret.name AS RotaEntryType, rp.name AS ResourceProfileName, re.ResourceId, re.StartAt, re.EndAt " +
            " FROM RotaEntry re " +
            " INNER JOIN OrganisationStructureSite oss ON oss.id=re.OrganisationStructureSiteId " +
            " LEFT JOIN RotaEntryType ret ON ret.Id=re.RotaEntryTypeId " +
            " LEFT JOIN ResourceProfile rp ON rp.Id=re.ResourceProfileId";

    private static final String FOR_RESOURCE_AND_SITE = " WHERE re.Resourceid=%d AND oss.SiteId=%d ";
    private static final String FOR_SITE = " WHERE oss.SiteId=%d ";
    private static final String AND_ACTIVE = " AND re.StartAt < SWITCHOFFSET(GETDATE(), ':timezone') AND SWITCHOFFSET(GETDATE(), ':timezone') < coalesce(re.EndAt, '2999-12-01') ";

    @Autowired
    private EntityManager entityManager;


    public RotaEntry getRotaEntryOnCall(Integer resourceId, Integer siteId, String timezone) {
        String jpql = BASE_SQL + String.format(FOR_RESOURCE_AND_SITE, resourceId, siteId) + AND_ACTIVE.replace(":timezone", timezone);

        logger.debug("getRotaEntryOnCall: " + jpql);

        try {
            Query query =  entityManager.createNativeQuery(jpql, RotaEntry.class);
            return (RotaEntry) query.getResultList().get(0);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<RotaEntry> getRotaEntriesForSite(Integer siteId, String timezone) {
        String jpql = BASE_SQL + String.format(FOR_SITE, siteId) + AND_ACTIVE.replace(":timezone", timezone);

        logger.debug("getRotaEntriesForSite: " + jpql);

        try {
            Query query =  entityManager.createNativeQuery(jpql, RotaEntry.class);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

}
