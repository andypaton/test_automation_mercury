
package mercury.database.dao;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.SiteLocation;


@Repository
public class SiteLocationDao {

    private static final String GET_RANDOM_SITE_LOCATION_SQL = "SELECT TOP(1) s.Id, s.Name, l.Id LocationId, l.Name LocationName " +
            " FROM Site s LEFT JOIN SiteLocationMapping slm ON s.Id = slm.SiteId " +
            " LEFT JOIN Location l ON slm.LocationId = l.Id " +
            " ORDER BY NEWID() ";

    @Autowired private EntityManager entityManager;

    public SiteLocation getRandomSiteLocation(String siteName) {
        String jpql = GET_RANDOM_SITE_LOCATION_SQL;
        String queryStatement = String.format(jpql, siteName);
        try {
            return (SiteLocation) entityManager.createNativeQuery(queryStatement, SiteLocation.class).getResultList();
        } catch (Exception e) {
            return null;
        }
    }
}
