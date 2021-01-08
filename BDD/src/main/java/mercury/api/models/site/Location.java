package mercury.api.models.site;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "locationId", "areaName", "locationName", "subLocationName", "isEnabled", "isOptional", "isDefault", "level", "fullName" })
public class Location {

    @JsonProperty("locationId")
    private Integer locationId;
    
    @JsonProperty("areaName")
    private String areaName;
    
    @JsonProperty("locationName")
    private Object locationName;
    
    @JsonProperty("subLocationName")
    private Object subLocationName;
    
    @JsonProperty("isEnabled")
    private Boolean isEnabled;
    
    @JsonProperty("isOptional")
    private Boolean isOptional;
    
    @JsonProperty("isDefault")
    private Boolean isDefault;
    
    @JsonProperty("level")
    private Integer level;
    
    @JsonProperty("fullName")
    private String fullName;



    @JsonProperty("locationId")
    public Integer getLocationId() {
        return locationId;
    }

    @JsonProperty("locationId")
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    @JsonProperty("areaName")
    public String getAreaName() {
        return areaName;
    }

    @JsonProperty("areaName")
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    @JsonProperty("locationName")
    public Object getLocationName() {
        return locationName;
    }

    @JsonProperty("locationName")
    public void setLocationName(Object locationName) {
        this.locationName = locationName;
    }

    @JsonProperty("subLocationName")
    public Object getSubLocationName() {
        return subLocationName;
    }

    @JsonProperty("subLocationName")
    public void setSubLocationName(Object subLocationName) {
        this.subLocationName = subLocationName;
    }

    @JsonProperty("isEnabled")
    public Boolean getIsEnabled() {
        return isEnabled;
    }

    @JsonProperty("isEnabled")
    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @JsonProperty("isOptional")
    public Boolean getIsOptional() {
        return isOptional;
    }

    @JsonProperty("isOptional")
    public void setIsOptional(Boolean isOptional) {
        this.isOptional = isOptional;
    }

    @JsonProperty("isDefault")
    public Boolean getIsDefault() {
        return isDefault;
    }

    @JsonProperty("isDefault")
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    @JsonProperty("level")
    public Integer getLevel() {
        return level;
    }

    @JsonProperty("level")
    public void setLevel(Integer level) {
        this.level = level;
    }

    @JsonProperty("fullName")
    public String getFullName() {
        return fullName;
    }

    @JsonProperty("fullName")
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this).append("locationId", locationId).append("areaName", areaName).append("locationName", locationName).append("subLocationName", subLocationName)
                .append("isEnabled", isEnabled).append("isOptional", isOptional).append("isDefault", isDefault).append("level", level).append("fullName", fullName)
                .toString();
    }

}