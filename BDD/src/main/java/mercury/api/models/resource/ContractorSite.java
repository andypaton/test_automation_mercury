package mercury.api.models.resource;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "siteId", "siteName", "classifications" })
public class ContractorSite {

    @JsonProperty("siteId")
    private Integer siteId;
    @JsonProperty("siteName")
    private String siteName;
    @JsonProperty("classifications")
    private List<Classification> classifications = null;

    @JsonProperty("siteId")
    public Integer getSiteId() {
        return siteId;
    }

    @JsonProperty("siteId")
    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    @JsonProperty("siteName")
    public String getSiteName() {
        return siteName;
    }

    @JsonProperty("siteName")
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    @JsonProperty("classifications")
    public List<Classification> getClassifications() {
        return classifications;
    }

    @JsonProperty("classifications")
    public void setClassifications(List<Classification> classifications) {
        this.classifications = classifications;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("siteId", siteId).append("siteName", siteName).append("classifications", classifications).toString();
    }

}