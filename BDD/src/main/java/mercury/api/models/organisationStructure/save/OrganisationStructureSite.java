package mercury.api.models.organisationStructure.save;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "name", "moveSiteToId", "isNewSite", "isActive" })
public class OrganisationStructureSite {

    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("moveSiteToId")
    private Integer moveSiteToId;
    
    @JsonProperty("isNewSite")
    private Boolean isNewSite;
    
    @JsonProperty("isActive")
    private Boolean isActive;

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

    @JsonProperty("moveSiteToId")
    public Integer getMoveSiteToId() {
        return moveSiteToId;
    }

    @JsonProperty("moveSiteToId")
    public void setMoveSiteToId(Integer moveSiteToId) {
        this.moveSiteToId = moveSiteToId;
    }

    @JsonProperty("isNewSite")
    public Boolean getIsNewSite() {
        return isNewSite;
    }

    @JsonProperty("isNewSite")
    public void setIsNewSite(Boolean isNewSite) {
        this.isNewSite = isNewSite;
    }

    @JsonProperty("isActive")
    public Boolean getIsActive() {
        return isActive;
    }

    @JsonProperty("isActive")
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("name", name).append("moveSiteToId", moveSiteToId).append("isNewSite", isNewSite).append("isActive", isActive).toString();
    }

}