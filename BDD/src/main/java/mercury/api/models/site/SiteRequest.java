package mercury.api.models.site;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "hasSnowLandlord", "openingHours", "openingHoursExceptions", "pfsConfiguration", "phoneNumbers", "siteLitigations", "usStateId", "geographicalRegionId", "languageId", "currency",
        "name", "siteCode", "siteTypeId", "brandId", "siteStatusId", "storeOpenDate", "town", "countryId", "postcode", "ianaTimezoneId", "hierarchyTitle", "pfsModel", "state", "opened", "closed",
        "location", "latitude", "longitude", "evidenceId", "locations" })
public class SiteRequest {

    @JsonProperty("hasSnowLandlord")
    private Boolean hasSnowLandlord;
    
    @JsonProperty("openingHours")
    private OpeningHours openingHours;
    
    @JsonProperty("openingHoursExceptions")
    private List<Object> openingHoursExceptions = null;
    
    @JsonProperty("pfsConfiguration")
    private Object pfsConfiguration;
    
    @JsonProperty("phoneNumbers")
    private List<PhoneNumber> phoneNumbers = null;
    
    @JsonProperty("siteLitigations")
    private List<Object> siteLitigations = null;
    
    @JsonProperty("usStateId")
    private String usStateId;
    
    @JsonProperty("geographicalRegionId")
    private String geographicalRegionId;
    
    @JsonProperty("languageId")
    private String languageId;
    
    @JsonProperty("currency")
    private String currency;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("siteCode")
    private String siteCode;
    
    @JsonProperty("siteTypeId")
    private Integer siteTypeId;
    
    @JsonProperty("brandId")
    private Integer brandId;
    
    @JsonProperty("siteStatusId")
    private Integer siteStatusId;
    
    @JsonProperty("storeOpenDate")
    private Object storeOpenDate;
    
    @JsonProperty("town")
    private String town;
    
    @JsonProperty("countryId")
    private Integer countryId;
    
    @JsonProperty("postcode")
    private String postcode;
    
    @JsonProperty("ianaTimezoneId")
    private Integer ianaTimezoneId;
    
    @JsonProperty("hierarchyTitle")
    private String hierarchyTitle;
    
    @JsonProperty("pfsModel")
    private PfsModel pfsModel;
    
    @JsonProperty("state")
    private String state;
    
    @JsonProperty("opened")
    private Object opened;
    
    @JsonProperty("closed")
    private Object closed;
    
    @JsonProperty("location")
    private String location;
    
    @JsonProperty("latitude")
    private String latitude;
    
    @JsonProperty("longitude")
    private String longitude;
    
    @JsonProperty("evidenceId")
    private Integer evidenceId;
    
    @JsonProperty("locations")
    private List<Location> locations = null;

    @JsonProperty("hasSnowLandlord")
    public Boolean getHasSnowLandlord() {
        return hasSnowLandlord;
    }

    @JsonProperty("hasSnowLandlord")
    public void setHasSnowLandlord(Boolean hasSnowLandlord) {
        this.hasSnowLandlord = hasSnowLandlord;
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
    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    @JsonProperty("phoneNumbers")
    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    @JsonProperty("siteLitigations")
    public List<Object> getSiteLitigations() {
        return siteLitigations;
    }

    @JsonProperty("siteLitigations")
    public void setSiteLitigations(List<Object> siteLitigations) {
        this.siteLitigations = siteLitigations;
    }

    @JsonProperty("usStateId")
    public String getUsStateId() {
        return usStateId;
    }

    @JsonProperty("usStateId")
    public void setUsStateId(String usStateId) {
        this.usStateId = usStateId;
    }

    @JsonProperty("geographicalRegionId")
    public String getGeographicalRegionId() {
        return geographicalRegionId;
    }

    @JsonProperty("geographicalRegionId")
    public void setGeographicalRegionId(String geographicalRegionId) {
        this.geographicalRegionId = geographicalRegionId;
    }

    @JsonProperty("languageId")
    public String getLanguageId() {
        return languageId;
    }

    @JsonProperty("languageId")
    public void setLanguageId(String languageId) {
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

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("siteCode")
    public String getSiteCode() {
        return siteCode;
    }

    @JsonProperty("siteCode")
    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
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

    @JsonProperty("town")
    public String getTown() {
        return town;
    }

    @JsonProperty("town")
    public void setTown(String town) {
        this.town = town;
    }

    @JsonProperty("countryId")
    public Integer getCountryId() {
        return countryId;
    }

    @JsonProperty("countryId")
    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    @JsonProperty("postcode")
    public String getPostcode() {
        return postcode;
    }

    @JsonProperty("postcode")
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @JsonProperty("ianaTimezoneId")
    public Integer getIanaTimezoneId() {
        return ianaTimezoneId;
    }

    @JsonProperty("ianaTimezoneId")
    public void setIanaTimezoneId(Integer ianaTimezoneId) {
        this.ianaTimezoneId = ianaTimezoneId;
    }

    @JsonProperty("hierarchyTitle")
    public String getHierarchyTitle() {
        return hierarchyTitle;
    }

    @JsonProperty("hierarchyTitle")
    public void setHierarchyTitle(String hierarchyTitle) {
        this.hierarchyTitle = hierarchyTitle;
    }

    @JsonProperty("pfsModel")
    public PfsModel getPfsModel() {
        return pfsModel;
    }

    @JsonProperty("pfsModel")
    public void setPfsModel(PfsModel pfsModel) {
        this.pfsModel = pfsModel;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("opened")
    public Object getOpened() {
        return opened;
    }

    @JsonProperty("opened")
    public void setOpened(Object opened) {
        this.opened = opened;
    }

    @JsonProperty("closed")
    public Object getClosed() {
        return closed;
    }

    @JsonProperty("closed")
    public void setClosed(Object closed) {
        this.closed = closed;
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
    public String getLatitude() {
        return latitude;
    }

    @JsonProperty("latitude")
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("longitude")
    public String getLongitude() {
        return longitude;
    }

    @JsonProperty("longitude")
    public void setLongitude(String longitude) {
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

    @JsonProperty("locations")
    public List<Location> getLocations() {
        return locations;
    }

    @JsonProperty("locations")
    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("hasSnowLandlord", hasSnowLandlord).append("openingHours", openingHours).append("openingHoursExceptions", openingHoursExceptions)
                .append("pfsConfiguration", pfsConfiguration).append("phoneNumbers", phoneNumbers).append("siteLitigations", siteLitigations).append("usStateId", usStateId)
                .append("geographicalRegionId", geographicalRegionId).append("languageId", languageId).append("currency", currency).append("name", name).append("siteCode", siteCode)
                .append("siteTypeId", siteTypeId).append("brandId", brandId).append("siteStatusId", siteStatusId).append("storeOpenDate", storeOpenDate).append("town", town)
                .append("countryId", countryId).append("postcode", postcode).append("ianaTimezoneId", ianaTimezoneId).append("hierarchyTitle", hierarchyTitle).append("pfsModel", pfsModel)
                .append("state", state).append("opened", opened).append("closed", closed).append("location", location).append("latitude", latitude).append("longitude", longitude)
                .append("evidenceId", evidenceId).append("locations", locations).toString();
    }

}