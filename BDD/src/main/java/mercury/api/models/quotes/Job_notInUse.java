package mercury.api.models.quotes;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "callerId",
    "siteId",
    "assetClassificationId",
    "assetId",
    "assetSubTypeId",
    "locationId",
    "jobTypeId",
    "description",
    "faultTypeId",
    "faultPriorityId",
    "jobStatusId",
    "ppmJobId",
    "fundingRouteId",
    "fundingRoutes",
    "quotePriorityId",
    "contractorToQuoteId",
    "quoteAnswers",
    "createdBy"
})
public class Job_notInUse {

    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("callerId")
    private Integer callerId;
    
    @JsonProperty("siteId")
    private Integer siteId;
    
    @JsonProperty("assetClassificationId")
    private Integer assetClassificationId;
    
    @JsonProperty("assetId")
    private Object assetId;
    
    @JsonProperty("assetSubTypeId")
    private Integer assetSubTypeId;
    
    @JsonProperty("locationId")
    private Integer locationId;
    
    @JsonProperty("jobTypeId")
    private Integer jobTypeId;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("faultTypeId")
    private Integer faultTypeId;
    
    @JsonProperty("faultPriorityId")
    private Integer faultPriorityId;
    
    @JsonProperty("jobStatusId")
    private Integer jobStatusId;
    
    @JsonProperty("ppmJobId")
    private Object ppmJobId;
    
    @JsonProperty("fundingRouteId")
    private Integer fundingRouteId;
    
    @JsonProperty("fundingRoutes")
    private Object fundingRoutes;
    
    @JsonProperty("quotePriorityId")
    private Integer quotePriorityId;
    
    @JsonProperty("contractorToQuoteId")
    private Object contractorToQuoteId;
    
    @JsonProperty("quoteAnswers")
    private List<Object> quoteAnswers = null;
    
    @JsonProperty("createdBy")
    private String createdBy;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("callerId")
    public Integer getCallerId() {
        return callerId;
    }

    @JsonProperty("callerId")
    public void setCallerId(Integer callerId) {
        this.callerId = callerId;
    }

    @JsonProperty("siteId")
    public Integer getSiteId() {
        return siteId;
    }

    @JsonProperty("siteId")
    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    @JsonProperty("assetClassificationId")
    public Integer getAssetClassificationId() {
        return assetClassificationId;
    }

    @JsonProperty("assetClassificationId")
    public void setAssetClassificationId(Integer assetClassificationId) {
        this.assetClassificationId = assetClassificationId;
    }

    @JsonProperty("assetId")
    public Object getAssetId() {
        return assetId;
    }

    @JsonProperty("assetId")
    public void setAssetId(Object assetId) {
        this.assetId = assetId;
    }

    @JsonProperty("assetSubTypeId")
    public Integer getAssetSubTypeId() {
        return assetSubTypeId;
    }

    @JsonProperty("assetSubTypeId")
    public void setAssetSubTypeId(Integer assetSubTypeId) {
        this.assetSubTypeId = assetSubTypeId;
    }

    @JsonProperty("locationId")
    public Integer getLocationId() {
        return locationId;
    }

    @JsonProperty("locationId")
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    @JsonProperty("jobTypeId")
    public Integer getJobTypeId() {
        return jobTypeId;
    }

    @JsonProperty("jobTypeId")
    public void setJobTypeId(Integer jobTypeId) {
        this.jobTypeId = jobTypeId;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("faultTypeId")
    public Integer getFaultTypeId() {
        return faultTypeId;
    }

    @JsonProperty("faultTypeId")
    public void setFaultTypeId(Integer faultTypeId) {
        this.faultTypeId = faultTypeId;
    }

    @JsonProperty("faultPriorityId")
    public Integer getFaultPriorityId() {
        return faultPriorityId;
    }

    @JsonProperty("faultPriorityId")
    public void setFaultPriorityId(Integer faultPriorityId) {
        this.faultPriorityId = faultPriorityId;
    }

    @JsonProperty("jobStatusId")
    public Integer getJobStatusId() {
        return jobStatusId;
    }

    @JsonProperty("jobStatusId")
    public void setJobStatusId(Integer jobStatusId) {
        this.jobStatusId = jobStatusId;
    }

    @JsonProperty("ppmJobId")
    public Object getPpmJobId() {
        return ppmJobId;
    }

    @JsonProperty("ppmJobId")
    public void setPpmJobId(Object ppmJobId) {
        this.ppmJobId = ppmJobId;
    }

    @JsonProperty("fundingRouteId")
    public Integer getFundingRouteId() {
        return fundingRouteId;
    }

    @JsonProperty("fundingRouteId")
    public void setFundingRouteId(Integer fundingRouteId) {
        this.fundingRouteId = fundingRouteId;
    }

    @JsonProperty("fundingRoutes")
    public Object getFundingRoutes() {
        return fundingRoutes;
    }

    @JsonProperty("fundingRoutes")
    public void setFundingRoutes(Object fundingRoutes) {
        this.fundingRoutes = fundingRoutes;
    }

    @JsonProperty("quotePriorityId")
    public Integer getQuotePriorityId() {
        return quotePriorityId;
    }

    @JsonProperty("quotePriorityId")
    public void setQuotePriorityId(Integer quotePriorityId) {
        this.quotePriorityId = quotePriorityId;
    }

    @JsonProperty("contractorToQuoteId")
    public Object getContractorToQuoteId() {
        return contractorToQuoteId;
    }

    @JsonProperty("contractorToQuoteId")
    public void setContractorToQuoteId(Object contractorToQuoteId) {
        this.contractorToQuoteId = contractorToQuoteId;
    }

    @JsonProperty("quoteAnswers")
    public List<Object> getQuoteAnswers() {
        return quoteAnswers;
    }

    @JsonProperty("quoteAnswers")
    public void setQuoteAnswers(List<Object> quoteAnswers) {
        this.quoteAnswers = quoteAnswers;
    }

    @JsonProperty("createdBy")
    public String getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("createdBy")
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("callerId", callerId).append("siteId", siteId).append("assetClassificationId", assetClassificationId).append("assetId", assetId).append("assetSubTypeId", assetSubTypeId).append("locationId", locationId).append("jobTypeId", jobTypeId).append("description", description).append("faultTypeId", faultTypeId).append("faultPriorityId", faultPriorityId).append("jobStatusId", jobStatusId).append("ppmJobId", ppmJobId).append("fundingRouteId", fundingRouteId).append("fundingRoutes", fundingRoutes).append("quotePriorityId", quotePriorityId).append("contractorToQuoteId", contractorToQuoteId).append("quoteAnswers", quoteAnswers).append("createdBy", createdBy).toString();
    }

}