package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class SiteAsset {

    @Id
    @Column(name = "Id")
    private Integer id;

    @Column(name= "name")
    private String name;

    @Column(name = "SiteCode")
    private String siteCode;

    @Column(name = "AssetTag")
    private String assetTag;

    @Column(name = "SerialNo")
    private String serialNo;

    @Column(name = "LocalIdentifier")
    private String localIdentifier;

    @Column(name = "SubLocationName")
    private String subLocationName;

    @Column(name = "AssetTypeName")
    private String assetTypeName;

    @Column(name = "AssetSubTypeName")
    private String assetSubTypeName;

    @Column(name =  "AssetClassificationName")
    private String assetClassificationName;

    @Column(name = "FaultTypeName")
    private String faultTypeName;

    @Column(name = "FaultPriority")
    private String faultPriority;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getAssetTag() {
        return assetTag;
    }

    public void setAssetTag(String assetTag) {
        this.assetTag = assetTag;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getLocalIdentifier() {
        return localIdentifier;
    }

    public void setLocalIdentifier(String localIdentifier) {
        this.localIdentifier = localIdentifier;
    }

    public String getSubLocationName() {
        return subLocationName;
    }

    public void setSubLocationName(String subLocationName) {
        this.subLocationName = subLocationName;
    }

    public String getAssetTypeName() {
        return assetTypeName;
    }

    public void setAssetTypeName(String assetTypeName) {
        this.assetTypeName = assetTypeName;
    }

    public String getAssetSubTypeName() {
        return assetSubTypeName;
    }

    public void setAssetSubTypeName(String assetSubTypeName) {
        this.assetSubTypeName = assetSubTypeName;
    }

    public String getAssetClassificationName() {
        return assetClassificationName;
    }

    public void setAssetClassificationName(String assetClassificationName) {
        this.assetClassificationName = assetClassificationName;
    }

    public String getFaultTypeName() {
        return faultTypeName;
    }

    public void setFaultTypeName(String faultTypeName) {
        this.faultTypeName = faultTypeName;
    }

    public String getFaultPriority() {
        return faultPriority;
    }

    public void setFaultPriority(String faultPriority) {
        this.faultPriority = faultPriority;
    }


}
