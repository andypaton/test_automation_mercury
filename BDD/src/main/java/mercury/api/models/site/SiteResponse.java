package mercury.api.models.site;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "name", "siteTypeId", "brandId", "siteCode", "siteStatusId", "storeOpenDate", "storeClosingDate", "isVipStore", "vipReason", "storeLink", "address1", "address2", "address3",
        "town", "county", "postcode", "telNo", "geographicalRegionId", "languageId", "currency", "timezone", "organisationStructureId", "countryId", "usStateId", "mobileAppPreferredDistance",
        "location", "latitude", "longitude", "evidenceId", "timeStamp", "allowLowPriorityJobs", "hasSnowLandlord", "hasCarPark", "grittingCritical", "hasGenerator", "hasPfs", "accessRoadExists",
        "inLitigation", "openingHours", "openingHoursExceptions", "pfsConfiguration", "phoneNumbers", "locations", "siteAdditionalInformation", "ianaTimezoneId" })
public class SiteResponse {

    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("siteTypeId")
    private Integer siteTypeId;
    
    @JsonProperty("brandId")
    private Integer brandId;
    
    @JsonProperty("siteCode")
    private String siteCode;
    
    @JsonProperty("siteStatusId")
    private Integer siteStatusId;
    
    @JsonProperty("storeOpenDate")
    private Object storeOpenDate;
    
    @JsonProperty("storeClosingDate")
    private Object storeClosingDate;
    
    @JsonProperty("isVipStore")
    private Object isVipStore;
    
    @JsonProperty("vipReason")
    private Object vipReason;
    
    @JsonProperty("storeLink")
    private Object storeLink;
    
    @JsonProperty("address1")
    private Object address1;
    
    @JsonProperty("address2")
    private Object address2;
    
    @JsonProperty("address3")
    private Object address3;
    
    @JsonProperty("town")
    private String town;
    
    @JsonProperty("county")
    private Object county;
    
    @JsonProperty("postcode")
    private String postcode;
    
    @JsonProperty("telNo")
    private Object telNo;
    
    @JsonProperty("geographicalRegionId")
    private Object geographicalRegionId;
    
    @JsonProperty("languageId")
    private Object languageId;
    
    @JsonProperty("currency")
    private String currency;
    
    @JsonProperty("timezone")
    private Object timezone;
    
    @JsonProperty("organisationStructureId")
    private Object organisationStructureId;
    
    @JsonProperty("countryId")
    private Integer countryId;
    
    @JsonProperty("usStateId")
    private Object usStateId;
    
    @JsonProperty("mobileAppPreferredDistance")
    private Object mobileAppPreferredDistance;
    
    @JsonProperty("location")
    private String location;
    
    @JsonProperty("latitude")
    private Object latitude;
    
    @JsonProperty("longitude")
    private Object longitude;
    
    @JsonProperty("evidenceId")
    private Integer evidenceId;
    
    @JsonProperty("timeStamp")
    private String timeStamp;
    
    @JsonProperty("allowLowPriorityJobs")
    private Object allowLowPriorityJobs;
    
    @JsonProperty("hasSnowLandlord")
    private Boolean hasSnowLandlord;
    
    @JsonProperty("hasCarPark")
    private Object hasCarPark;
    
    @JsonProperty("grittingCritical")
    private Object grittingCritical;
    
    @JsonProperty("hasGenerator")
    private Object hasGenerator;
    
    @JsonProperty("hasPfs")
    private Object hasPfs;
    
    @JsonProperty("accessRoadExists")
    private Object accessRoadExists;
    
    @JsonProperty("inLitigation")
    private Boolean inLitigation;
    
    @JsonProperty("openingHours")
    private OpeningHours openingHours;
    
    @JsonProperty("openingHoursExceptions")
    private List<Object> openingHoursExceptions = null;
    
    @JsonProperty("pfsConfiguration")
    private Object pfsConfiguration;
    
    @JsonProperty("phoneNumbers")
    private List<Object> phoneNumbers = null;
    
    @JsonProperty("locations")
    private Object locations;
    
    @JsonProperty("siteAdditionalInformation")
    private Object siteAdditionalInformation;
    
    @JsonProperty("ianaTimezoneId")
    private Integer ianaTimezoneId;


    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("siteTypeId")
    public Integer getSiteTypeId() {
        return siteTypeId;
    }

    @JsonProperty("siteTypeId")
    public void setSiteTypeId(Integer siteTypeId) {
        this.siteTypeId = siteTypeId;
    }

    @JsonProperty("brandId")
    public Integer getBrandId() {
        return brandId;
    }

    @JsonProperty("brandId")
    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    @JsonProperty("siteCode")
    public String getSiteCode() {
        return siteCode;
    }

    @JsonProperty("siteCode")
    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    @JsonProperty("siteStatusId")
    public Integer getSiteStatusId() {
        return siteStatusId;
    }

    @JsonProperty("siteStatusId")
    public void setSiteStatusId(Integer siteStatusId) {
        this.siteStatusId = siteStatusId;
    }

    @JsonProperty("storeOpenDate")
    public Object getStoreOpenDate() {
        return storeOpenDate;
    }

    @JsonProperty("storeOpenDate")
    public void setStoreOpenDate(Object storeOpenDate) {
        this.storeOpenDate = storeOpenDate;
    }

    @JsonProperty("storeClosingDate")
    public Object getStoreClosingDate() {
        return storeClosingDate;
    }

    @JsonProperty("storeClosingDate")
    public void setStoreClosingDate(Object storeClosingDate) {
        this.storeClosingDate = storeClosingDate;
    }

    @JsonProperty("isVipStore")
    public Object getIsVipStore() {
        return isVipStore;
    }

    @JsonProperty("isVipStore")
    public void setIsVipStore(Object isVipStore) {
        this.isVipStore = isVipStore;
    }

    @JsonProperty("vipReason")
    public Object getVipReason() {
        return vipReason;
    }

    @JsonProperty("vipReason")
    public void setVipReason(Object vipReason) {
        this.vipReason = vipReason;
    }

    @JsonProperty("storeLink")
    public Object getStoreLink() {
        return storeLink;
    }

    @JsonProperty("storeLink")
    public void setStoreLink(Object storeLink) {
        this.storeLink = storeLink;
    }

    @JsonProperty("address1")
    public Object getAddress1() {
        return address1;
    }

    @JsonProperty("address1")
    public void setAddress1(Object address1) {
        this.address1 = address1;
    }

    @JsonProperty("address2")
    public Object getAddress2() {
        return address2;
    }

    @JsonProperty("address2")
    public void setAddress2(Object address2) {
        this.address2 = address2;
    }

    @JsonProperty("address3")
    public Object getAddress3() {
        return address3;
    }

    @JsonProperty("address3")
    public void setAddress3(Object address3) {
        this.address3 = address3;
    }

    @JsonProperty("town")
    public String getTown() {
        return town;
    }

    @JsonProperty("town")
    public void setTown(String town) {
        this.town = town;
    }

    @JsonProperty("county")
    public Object getCounty() {
        return county;
    }

    @JsonProperty("county")
    public void setCounty(Object county) {
        this.county = county;
    }

    @JsonProperty("postcode")
    public String getPostcode() {
        return postcode;
    }

    @JsonProperty("postcode")
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @JsonProperty("telNo")
    public Object getTelNo() {
        return telNo;
    }

    @JsonProperty("telNo")
    public void setTelNo(Object telNo) {
        this.telNo = telNo;
    }

    @JsonProperty("geographicalRegionId")
    public Object getGeographicalRegionId() {
        return geographicalRegionId;
    }

    @JsonProperty("geographicalRegionId")
    public void setGeographicalRegionId(Object geographicalRegionId) {
        this.geographicalRegionId = geographicalRegionId;
    }

    @JsonProperty("languageId")
    public Object getLanguageId() {
        return languageId;
    }

    @JsonProperty("languageId")
    public void setLanguageId(Object languageId) {
        this.languageId = languageId;
    }

    @JsonProperty("currency")
    public String getCurrency() {
        return currency;
    }

    @JsonProperty("currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonProperty("timezone")
    public Object getTimezone() {
        return timezone;
    }

    @JsonProperty("timezone")
    public void setTimezone(Object timezone) {
        this.timezone = timezone;
    }

    @JsonProperty("organisationStructureId")
    public Object getOrganisationStructureId() {
        return organisationStructureId;
    }

    @JsonProperty("organisationStructureId")
    public void setOrganisationStructureId(Object organisationStructureId) {
        this.organisationStructureId = organisationStructureId;
    }

    @JsonProperty("countryId")
    public Integer getCountryId() {
        return countryId;
    }

    @JsonProperty("countryId")
    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    @JsonProperty("usStateId")
    public Object getUsStateId() {
        return usStateId;
    }

    @JsonProperty("usStateId")
    public void setUsStateId(Object usStateId) {
        this.usStateId = usStateId;
    }

    @JsonProperty("mobileAppPreferredDistance")
    public Object getMobileAppPreferredDistance() {
        return mobileAppPreferredDistance;
    }

    @JsonProperty("mobileAppPreferredDistance")
    public void setMobileAppPreferredDistance(Object mobileAppPreferredDistance) {
        this.mobileAppPreferredDistance = mobileAppPreferredDistance;
    }

    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(String location) {
        this.location = location;
    }

    @JsonProperty("latitude")
    public Object getLatitude() {
        return latitude;
    }

    @JsonProperty("latitude")
    public void setLatitude(Object latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("longitude")
    public Object getLongitude() {
        return longitude;
    }

    @JsonProperty("longitude")
    public void setLongitude(Object longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("evidenceId")
    public Integer getEvidenceId() {
        return evidenceId;
    }

    @JsonProperty("evidenceId")
    public void setEvidenceId(Integer evidenceId) {
        this.evidenceId = evidenceId;
    }

    @JsonProperty("timeStamp")
    public String getTimeStamp() {
        return timeStamp;
    }

    @JsonProperty("timeStamp")
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @JsonProperty("allowLowPriorityJobs")
    public Object getAllowLowPriorityJobs() {
        return allowLowPriorityJobs;
    }

    @JsonProperty("allowLowPriorityJobs")
    public void setAllowLowPriorityJobs(Object allowLowPriorityJobs) {
        this.allowLowPriorityJobs = allowLowPriorityJobs;
    }

    @JsonProperty("hasSnowLandlord")
    public Boolean getHasSnowLandlord() {
        return hasSnowLandlord;
    }

    @JsonProperty("hasSnowLandlord")
    public void setHasSnowLandlord(Boolean hasSnowLandlord) {
        this.hasSnowLandlord = hasSnowLandlord;
    }

    @JsonProperty("hasCarPark")
    public Object getHasCarPark() {
        return hasCarPark;
    }

    @JsonProperty("hasCarPark")
    public void setHasCarPark(Object hasCarPark) {
        this.hasCarPark = hasCarPark;
    }

    @JsonProperty("grittingCritical")
    public Object getGrittingCritical() {
        return grittingCritical;
    }

    @JsonProperty("grittingCritical")
    public void setGrittingCritical(Object grittingCritical) {
        this.grittingCritical = grittingCritical;
    }

    @JsonProperty("hasGenerator")
    public Object getHasGenerator() {
        return hasGenerator;
    }

    @JsonProperty("hasGenerator")
    public void setHasGenerator(Object hasGenerator) {
        this.hasGenerator = hasGenerator;
    }

    @JsonProperty("hasPfs")
    public Object getHasPfs() {
        return hasPfs;
    }

    @JsonProperty("hasPfs")
    public void setHasPfs(Object hasPfs) {
        this.hasPfs = hasPfs;
    }

    @JsonProperty("accessRoadExists")
    public Object getAccessRoadExists() {
        return accessRoadExists;
    }

    @JsonProperty("accessRoadExists")
    public void setAccessRoadExists(Object accessRoadExists) {
        this.accessRoadExists = accessRoadExists;
    }

    @JsonProperty("inLitigation")
    public Boolean getInLitigation() {
        return inLitigation;
    }

    @JsonProperty("inLitigation")
    public void setInLitigation(Boolean inLitigation) {
        this.inLitigation = inLitigation;
    }

    @JsonProperty("openingHours")
    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    @JsonProperty("openingHours")
    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    @JsonProperty("openingHoursExceptions")
    public List<Object> getOpeningHoursExceptions() {
        return openingHoursExceptions;
    }

    @JsonProperty("openingHoursExceptions")
    public void setOpeningHoursExceptions(List<Object> openingHoursExceptions) {
        this.openingHoursExceptions = openingHoursExceptions;
    }

    @JsonProperty("pfsConfiguration")
    public Object getPfsConfiguration() {
        return pfsConfiguration;
    }

    @JsonProperty("pfsConfiguration")
    public void setPfsConfiguration(Object pfsConfiguration) {
        this.pfsConfiguration = pfsConfiguration;
    }

    @JsonProperty("phoneNumbers")
    public List<Object> getPhoneNumbers() {
        return phoneNumbers;
    }

    @JsonProperty("phoneNumbers")
    public void setPhoneNumbers(List<Object> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    @JsonProperty("locations")
    public Object getLocations() {
        return locations;
    }

    @JsonProperty("locations")
    public void setLocations(Object locations) {
        this.locations = locations;
    }

    @JsonProperty("siteAdditionalInformation")
    public Object getSiteAdditionalInformation() {
        return siteAdditionalInformation;
    }

    @JsonProperty("siteAdditionalInformation")
    public void setSiteAdditionalInformation(Object siteAdditionalInformation) {
        this.siteAdditionalInformation = siteAdditionalInformation;
    }

    @JsonProperty("ianaTimezoneId")
    public Integer getIanaTimezoneId() {
        return ianaTimezoneId;
    }

    @JsonProperty("ianaTimezoneId")
    public void setIanaTimezoneId(Integer ianaTimezoneId) {
        this.ianaTimezoneId = ianaTimezoneId;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("name", name).append("siteTypeId", siteTypeId).append("brandId", brandId).append("siteCode", siteCode)
                .append("siteStatusId", siteStatusId).append("storeOpenDate", storeOpenDate).append("storeClosingDate", storeClosingDate).append("isVipStore", isVipStore)
                .append("vipReason", vipReason).append("storeLink", storeLink).append("address1", address1).append("address2", address2).append("address3", address3).append("town", town)
                .append("county", county).append("postcode", postcode).append("telNo", telNo).append("geographicalRegionId", geographicalRegionId).append("languageId", languageId)
                .append("currency", currency).append("timezone", timezone).append("organisationStructureId", organisationStructureId).append("countryId", countryId).append("usStateId", usStateId)
                .append("mobileAppPreferredDistance", mobileAppPreferredDistance).append("location", location).append("latitude", latitude).append("longitude", longitude)
                .append("evidenceId", evidenceId).append("timeStamp", timeStamp).append("allowLowPriorityJobs", allowLowPriorityJobs).append("hasSnowLandlord", hasSnowLandlord)
                .append("hasCarPark", hasCarPark).append("grittingCritical", grittingCritical).append("hasGenerator", hasGenerator).append("hasPfs", hasPfs)
                .append("accessRoadExists", accessRoadExists).append("inLitigation", inLitigation).append("openingHours", openingHours).append("openingHoursExceptions", openingHoursExceptions)
                .append("pfsConfiguration", pfsConfiguration).append("phoneNumbers", phoneNumbers).append("locations", locations).append("ianaTimezoneId", ianaTimezoneId)
                .toString();
    }

}