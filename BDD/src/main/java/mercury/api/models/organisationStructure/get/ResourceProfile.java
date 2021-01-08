package mercury.api.models.organisationStructure.get;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "name", "followsPlannedReactiveRota", "siteTypeId" })
public class ResourceProfile {

    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("followsPlannedReactiveRota")
    private Boolean followsPlannedReactiveRota;
    
    @JsonProperty("siteTypeId")
    private Integer siteTypeId;

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

    @JsonProperty("followsPlannedReactiveRota")
    public Boolean getFollowsPlannedReactiveRota() {
        return followsPlannedReactiveRota;
    }

    @JsonProperty("followsPlannedReactiveRota")
    public void setFollowsPlannedReactiveRota(Boolean followsPlannedReactiveRota) {
        this.followsPlannedReactiveRota = followsPlannedReactiveRota;
    }

    @JsonProperty("siteTypeId")
    public Integer getSiteTypeId() {
        return siteTypeId;
    }

    @JsonProperty("siteTypeId")
    public void setSiteTypeId(Integer siteTypeId) {
        this.siteTypeId = siteTypeId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("name", name).append("followsPlannedReactiveRota", followsPlannedReactiveRota).append("siteTypeId", siteTypeId).toString();
    }

}
