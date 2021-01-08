package mercury.api.models.organisationStructure.save;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "name", "isPrimary", "sitesToMove", "sites", "managers", "typeName", "resourceProfiles" })
public class OrganisationStructure {

    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("isPrimary")
    private Boolean isPrimary;
    
    @JsonProperty("sitesToMove")
    private List<Integer> sitesToMove = null;
    
    @JsonProperty("sites")
    private List<OrganisationStructureSite> sites = null;
    
    @JsonProperty("managers")
    private List<Manager> managers = null;
    
    @JsonProperty("typeName")
    private String typeName;
    
    @JsonProperty("resourceProfiles")
    private List<ResourceProfile> resourceProfiles = null;

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

    @JsonProperty("isPrimary")
    public Boolean getIsPrimary() {
        return isPrimary;
    }

    @JsonProperty("isPrimary")
    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    @JsonProperty("sitesToMove")
    public List<Integer> getSitesToMove() {
        return sitesToMove;
    }

    @JsonProperty("sitesToMove")
    public void setSitesToMove(List<Integer> sitesToMove) {
        this.sitesToMove = sitesToMove;
    }

    @JsonProperty("sites")
    public List<OrganisationStructureSite> getSites() {
        return sites;
    }

    @JsonProperty("sites")
    public void setSites(List<OrganisationStructureSite> sites) {
        this.sites = sites;
    }

    @JsonProperty("managers")
    public List<Manager> getManagers() {
        return managers;
    }

    @JsonProperty("managers")
    public void setManagers(List<Manager> managers) {
        this.managers = managers;
    }

    @JsonProperty("typeName")
    public String getTypeName() {
        return typeName;
    }

    @JsonProperty("typeName")
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @JsonProperty("resourceProfiles")
    public List<ResourceProfile> getResourceProfiles() {
        return resourceProfiles;
    }

    @JsonProperty("resourceProfiles")
    public void setResourceProfiles(List<ResourceProfile> resourceProfiles) {
        this.resourceProfiles = resourceProfiles;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("name", name).append("isPrimary", isPrimary).append("sitesToMove", sitesToMove).append("sites", sites).append("managers", managers)
                .append("typeName", typeName).append("resourceProfiles", resourceProfiles).toString();
    }

}