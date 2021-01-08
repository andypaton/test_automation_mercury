package mercury.api.models.organisationStructure.get;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.organisationStructure.get.OrganisationStructureSite;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "name", "typeName", "isPrimary", "sitesToMove", "sites", "managers", "resourceProfiles" })
public class OrganisationStructure {

    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("typeName")
    private String typeName;
    
    @JsonProperty("isPrimary")
    private Boolean isPrimary;
    
    @JsonProperty("sitesToMove")
    private List<Integer> sitesToMove;
    
    @JsonProperty("sites")
    private List<OrganisationStructureSite> sites = null;
    
    @JsonProperty("managers")
    private List<Manager> managers = null;
    
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

    @JsonProperty("typeName")
    public String getTypeName() {
        return typeName;
    }

    @JsonProperty("typeName")
    public void setTypeName(String typeName) {
        this.typeName = typeName;
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
        return new ToStringBuilder(this).append("id", id).append("name", name).append("typeName", typeName).append("isPrimary", isPrimary).append("sitesToMove", sitesToMove).append("sites", sites)
                .append("managers", managers).append("resourceProfiles", resourceProfiles).toString();
    }

    /**
     * ARRRGGGGHHH!!! different models for GET and SAVE .... convert the model from GET to a suitable one to POST
     * @return
     */
    public mercury.api.models.organisationStructure.save.OrganisationStructure getPost() {
        mercury.api.models.organisationStructure.save.OrganisationStructure os = new mercury.api.models.organisationStructure.save.OrganisationStructure();
        
        os.setId(this.getId());
        os.setIsPrimary(this.getIsPrimary());
        os.setName(this.getName());
        os.setTypeName(this.getTypeName());
        
        List<mercury.api.models.organisationStructure.save.Manager> managers = new ArrayList<>();
        for (mercury.api.models.organisationStructure.get.Manager m : this.getManagers()) {
            mercury.api.models.organisationStructure.save.Manager manager = new mercury.api.models.organisationStructure.save.Manager();
            manager.setFirstName(m.getFirstName());
            manager.setId(m.getId());
            manager.setIsActive(m.getActive());
            manager.setLastName(m.getLastName());
            manager.setResourceId(m.getResourceId());
            manager.setRole(m.getRole());
            manager.setToBeRemoved(false);
            managers.add(manager);
        }
        os.setManagers(managers);
        
        List<mercury.api.models.organisationStructure.save.ResourceProfile> resourceProfiles = new ArrayList<>();
        for (mercury.api.models.organisationStructure.get.ResourceProfile rp : this.getResourceProfiles()) {
            mercury.api.models.organisationStructure.save.ResourceProfile resourceProfile = new mercury.api.models.organisationStructure.save.ResourceProfile();
            resourceProfile.setFollowsPlannedReactiveRota(rp.getFollowsPlannedReactiveRota());
            resourceProfile.setId(rp.getId());
            resourceProfile.setName(rp.getName());
            resourceProfile.setSiteTypeId(rp.getSiteTypeId());
            resourceProfiles.add(resourceProfile);
        }
        os.setResourceProfiles(resourceProfiles);
        
        List<mercury.api.models.organisationStructure.save.OrganisationStructureSite> sites = new ArrayList<>();
        for (mercury.api.models.organisationStructure.get.OrganisationStructureSite oss : this.getSites()) {
            mercury.api.models.organisationStructure.save.OrganisationStructureSite site = new mercury.api.models.organisationStructure.save.OrganisationStructureSite();
            site.setId(oss.getId());
            site.setIsActive(true);
            site.setIsNewSite(false);
            site.setMoveSiteToId(0);
            site.setName(oss.getName());
            sites.add(site);
        }
        os.setSites(sites);
        
        List<Integer> sitesToMove = new ArrayList<>();
        os.setSitesToMove(sitesToMove);
                
        return os;
    }
}