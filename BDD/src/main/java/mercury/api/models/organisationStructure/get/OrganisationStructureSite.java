package mercury.api.models.organisationStructure.get;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "name", "isVipStore" })
public class OrganisationStructureSite {

    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("isVipStore")
    private Boolean isVipStore;

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

    @JsonProperty("isVipStore")
    public Boolean getIsVipStore() {
        return isVipStore;
    }

    @JsonProperty("isVipStore")
    public void setIsVipStore(Boolean isVipStore) {
        this.isVipStore = isVipStore;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("name", name).append("isVipStore", isVipStore).toString();
    }

}