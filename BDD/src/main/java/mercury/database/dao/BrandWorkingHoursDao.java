package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import mercury.database.models.BrandWorkingHours;

@Repository
public class BrandWorkingHoursDao {

    private static final Logger logger = LogManager.getLogger();

    private static final String ALL_FIELDS = " bwh.Id, DayOfTheWeek, CAST(bwh.StartAt AS VARCHAR(15)) AS StartAt, CAST(bwh.EndAt AS VARCHAR(15))as EndAt ";

    private static final String BASE_SQL = "SELECT "+ ALL_FIELDS + " FROM BrandWorkingHours bwh ";

    private static final String GET_SITE_WORKING_HOURS = "SELECT DISTINCT " + ALL_FIELDS +
            " FROM BrandWorkingHours bwh" +
            " LEFT JOIN brandWorkingHoursGroup bwhg ON bwhg.BrandWorkingHoursId = bwh.Id" +
            " LEFT JOIN Site s ON s.BrandId = bwhg.BrandId ";

    private static final String GET_BRAND_WORKING_HOURS = "SELECT DISTINCT " + ALL_FIELDS +
            " FROM BrandWorkingHours bwh" +
            " LEFT JOIN brandWorkingHoursGroup bwhg ON bwhg.BrandWorkingHoursId = bwh.Id " +
            " WHERE bwhg.BrandId = %s ";

    private static final String AND = " AND ";

    private static final String TODAY = " bwh.DayOfTheWeek = DATEPART(dw,GETDATE()) ";


    private static final String FOR_SITE_TODAY = " WHERE s.Id = %s and DayOfTheWeek = DATEPART(dw,GETDATE())";

    @Autowired
    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<BrandWorkingHours> getAll() {
        return entityManager.createQuery(BASE_SQL).getResultList();
    }

    public BrandWorkingHours get(Integer id) {
        return entityManager.find(BrandWorkingHours.class, id);
    }

    @Modifying(clearAutomatically = true)
    public BrandWorkingHours getBrandWorkingHoursForSiteId(Integer siteId) {
        String jpql = GET_SITE_WORKING_HOURS + String.format(FOR_SITE_TODAY, siteId);

        String queryStatement = String.format(jpql, siteId);
        logger.debug("getBrandWorkingHoursForSiteId: " + queryStatement);

        try {
            Query query =  entityManager.createNativeQuery(queryStatement, BrandWorkingHours.class);
            return (BrandWorkingHours) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Modifying(clearAutomatically = true)
    public BrandWorkingHours getBrandWorkingHoursToday(Integer brandId) {

        String sql = String.format(GET_BRAND_WORKING_HOURS, brandId) + AND + TODAY;

        logger.debug("getBrandWorkingHoursToday: " + sql);

        try {
            Query query =  entityManager.createNativeQuery(sql, BrandWorkingHours.class);
            return (BrandWorkingHours) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /*
	  public boolean isBrandInHours(Integer siteId, String timezone) {

		String jpql = GET_SITE_WORKING_HOURS + AND_SITE_IN_HOURS.replace(":siteId", String.valueOf(siteId)).replace(":timezone", timezone);
		String queryStatement = String.format(jpql, siteId);
		logger.debug("isBrandInHours: " + queryStatement);

		Query query =  entityManager.createNativeQuery(queryStatement, BrandWorkingHours.class);
	    return query.getResultList().size() > 0;
	}
     */

}
