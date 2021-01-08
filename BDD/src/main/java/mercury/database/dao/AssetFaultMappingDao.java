package mercury.database.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mercury.database.models.AssetFaultMapping;

@Repository
public class AssetFaultMappingDao {

    private static final Logger logger = LogManager.getLogger();

    @Autowired private EntityManager entityManager;

    private static final String ALL_ASSETS_SQL = "SELECT ROW_NUMBER() OVER(ORDER BY at.Name DESC) AS Id, ftm.AssetTypeId, at.Name AS AssetTypeName, ftm.AssetSubTypeId, ast.Name AS AssetSubTypeName, AssetClassificationId, ac.Name AS AssetClassificationName, FaultTypeId, ft.Name AS FaultTypeName "
            + " FROM ( "
            + " SELECT AssetTypeId, AssetSubTypeId, AssetClassificationId, FaultTypeId, ROW_NUMBER() OVER(PARTITION BY  AssetTypeId, AssetSubTypeId, AssetClassificationId ORDER BY NEWID()) AS RowNum  "
            + " FROM FaultTypeMapping ftm "
            + " INNER JOIN FaultType ft ON ft.Id = ftm.FaultTypeId "
            + " WHERE  ft.Active = 1 "
            + "  ) ftm "
            + " INNER JOIN AssetType at ON at.Id = ftm.AssetTypeId "
            + " INNER JOIN AssetSubType ast ON ast.Id = AssetSubTypeId "
            + " INNER JOIN AssetClassification ac ON ac.Id = AssetClassificationId "
            + " INNER JOIN FaultType ft ON ft.Id = FaultTypeId  "
            + " WHERE "
            + " at.Active = 1 AND "
            + " ast.Active = 1 AND "
            + " ac.Active = 1 ";

    private static final String PER_RANDOM_FAULT = " AND RowNum = 1 " ;
    private static final String NON_RHVAC = " AND IsRHVAC = 0 " ;
    private static final String FOR_CLASSIFICATIONS = " AND ac.Name IN (%s) " ;
    private static final String ORDER = " ORDER BY ftm.AssetTypeId, at.Name, ftm.AssetSubTypeId, ast.Name, AssetClassificationId";


    @SuppressWarnings("unchecked")
    public List<AssetFaultMapping> getAllAssetsToAllFaults() {
        try {
            String sql = ALL_ASSETS_SQL + ORDER;
            logger.debug("getAllAssetsToAllFaults: " + sql);
            return entityManager.createNativeQuery(sql, AssetFaultMapping.class).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<AssetFaultMapping> getAllAssetsToRandomFault() {
        try {
            String sql = ALL_ASSETS_SQL + PER_RANDOM_FAULT + ORDER;
            logger.debug("getAllAssetsToRandomFault: " + sql);
            return entityManager.createNativeQuery(sql, AssetFaultMapping.class).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<AssetFaultMapping> getAllNonRhvacAssetsToRandomFault() {
        try {
            String sql = ALL_ASSETS_SQL + PER_RANDOM_FAULT + NON_RHVAC + ORDER;
            logger.debug("getAllNonRhvacAssetsToRandomFault: " + sql);
            return entityManager.createNativeQuery(sql, AssetFaultMapping.class).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<AssetFaultMapping> getAssetsToAllFaults(String classifications) {
        try {
            String sql = ALL_ASSETS_SQL + String.format(FOR_CLASSIFICATIONS, classifications) + ORDER;
            logger.debug("getAssetsToAllFaults: " + sql);
            return entityManager.createNativeQuery(sql, AssetFaultMapping.class).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

}
