package mercury.api.models.web.api.ResourceAction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "jobId",
    "resourceId",
    "eta",
    "etaWindowId",
    "useSlaAsEta",
    "contractorReference",
    "resourceAssignmentId",
    "etaAdvisedToSite"
})
public class AcceptJob extends modelBase<AcceptJob>{

    @JsonProperty("jobId")
    private Integer jobId;
    
    @JsonProperty("resourceId")
    private Integer resourceId;
    
    @JsonProperty("eta")
    private String eta;
    
    @JsonProperty("etaWindowId")
    private Integer etaWindowId;
    
    @JsonProperty("useSlaAsEta")
    private Boolean useSlaAsEta;
    
    @JsonProperty("contractorReference")
    private String contractorReference;
    
    @JsonProperty("resourceAssignmentId")
    private Integer resourceAssignmentId;
    
    @JsonProperty("etaAdvisedToSite")
    private Boolean etaAdvisedToSite;

    @JsonProperty("jobId")
    public Integer getJobId() {
        return jobId;
    }

    @JsonProperty("jobId")
    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public AcceptJob withJobId(Integer jobId) {
        this.jobId = jobId;
        return this;
    }

    @JsonProperty("resourceId")
    public Integer getResourceId() {
        return resourceId;
    }

    @JsonProperty("resourceId")
    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public AcceptJob withResourceId(Integer resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    @JsonProperty("eta")
    public String getEta() {
        return eta;
    }

    @JsonProperty("eta")
    public void setEta(String eta) {
        this.eta = eta;
    }

    public AcceptJob withEta(String eta) {
        this.eta = eta;
        return this;
    }

    @JsonProperty("etaWindowId")
    public Integer getEtaWindowId() {
        return etaWindowId;
    }

    @JsonProperty("etaWindowId")
    public void setEtaWindowId(Integer etaWindowId) {
        this.etaWindowId = etaWindowId;
    }

    public AcceptJob withEtaWindowId(Integer etaWindowId) {
        this.etaWindowId = etaWindowId;
        return this;
    }

    @JsonProperty("useSlaAsEta")
    public Boolean getUseSlaAsEta() {
        return useSlaAsEta;
    }

    @JsonProperty("useSlaAsEta")
    public void setUseSlaAsEta(Boolean useSlaAsEta) {
        this.useSlaAsEta = useSlaAsEta;
    }

    public AcceptJob withUseSlaAsEta(Boolean useSlaAsEta) {
        this.useSlaAsEta = useSlaAsEta;
        return this;
    }

    @JsonProperty("contractorReference")
    public String getContractorReference() {
        return contractorReference;
    }

    @JsonProperty("contractorReference")
    public void setContractorReference(String contractorReference) {
        this.contractorReference = contractorReference;
    }

    public AcceptJob withContractorReference(String contractorReference) {
        this.contractorReference = contractorReference;
        return this;
    }

    @JsonProperty("resourceAssignmentId")
    public Integer getResourceAssignmentId() {
        return resourceAssignmentId;
    }

    @JsonProperty("resourceAssignmentId")
    public void setResourceAssignmentId(Integer resourceAssignmentId) {
        this.resourceAssignmentId = resourceAssignmentId;
    }

    public AcceptJob withResourceAssignmentId(Integer resourceAssignmentId) {
        this.resourceAssignmentId = resourceAssignmentId;
        return this;
    }

    @JsonProperty("etaAdvisedToSite")
    public Boolean getEtaAdvisedToSite() {
        return etaAdvisedToSite;
    }

    @JsonProperty("etaAdvisedToSite")
    public void setEtaAdvisedToSite(Boolean etaAdvisedToSite) {
        this.etaAdvisedToSite = etaAdvisedToSite;
    }

    public AcceptJob withEtaAdvisedToSite(Boolean etaAdvisedToSite) {
        this.etaAdvisedToSite = etaAdvisedToSite;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("jobId", jobId).append("resourceId", resourceId).append("eta", eta).append("etaWindowId", etaWindowId).append("useSlaAsEta", useSlaAsEta).append("contractorReference", contractorReference).append("resourceAssignmentId", resourceAssignmentId).append("etaAdvisedToSite", etaAdvisedToSite).toString();
    }

}


