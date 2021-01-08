package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="AssetFaultMapping")
public class AssetFaultMapping {

    @Id
    @Column(name = "Id")
    private Integer id;

    @Column(name = "AssetTypeId")
    private Integer assetTypeId;

    @Column(name = "AssetTypeName")
    private String assetTypeName;

    @Column(name = "AssetSubTypeId")
    private Integer assetSubTypeId;

    @Column(name = "AssetSubTypeName")
    private String assetSubTypeName;

    @Column(name = "AssetClassificationId")
    private Integer assetClassificationId;

    @Column(name = "AssetClassificationName")
    private String assetClassificationName;

    @Column(name = "FaultTypeId")
    private Integer faultTypeId;

    @Column(name = "FaultTypeName")
    private String faultTypeName;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAssetTypeId() {
        return assetTypeId;
    }

    public void setAssetTypeId(Integer assetTypeId) {
        this.assetTypeId = assetTypeId;
    }

    public String getAssetTypeName() {
        return assetTypeName;
    }

    public void setAssetTypeName(String assetTypeName) {
        this.assetTypeName = assetTypeName;
    }

    public Integer getAssetSubTypeId() {
        return assetSubTypeId;
    }

    public void setAssetSubTypeId(Integer assetSubTypeId) {
        this.assetSubTypeId = assetSubTypeId;
    }

    public String getAssetSubTypeName() {
        return assetSubTypeName;
    }

    public void setAssetSubTypeName(String assetSubTypeName) {
        this.assetSubTypeName = assetSubTypeName;
    }

    public Integer getAssetClassificationId() {
        return assetClassificationId;
    }

    public void setAssetClassificationId(Integer assetClassificationId) {
        this.assetClassificationId = assetClassificationId;
    }

    public String getAssetClassificationName() {
        return assetClassificationName;
    }

    public void setAssetClassificationName(String assetClassificationName) {
        this.assetClassificationName = assetClassificationName;
    }

    public Integer getFaultTypeId() {
        return faultTypeId;
    }

    public void setFaultTypeId(Integer faultTypeId) {
        this.faultTypeId = faultTypeId;
    }

    public String getFaultTypeName() {
        return faultTypeName;
    }

    public void setFaultTypeName(String faultTypeName) {
        this.faultTypeName = faultTypeName;
    }

}

