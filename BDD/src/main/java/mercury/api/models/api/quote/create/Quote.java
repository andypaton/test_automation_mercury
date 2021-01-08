package mercury.api.models.api.quote.create;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "callerId", "siteId", "assetClassificationId", "assetId", "assetSubTypeId",
		"locationId", "jobTypeId", "description", "faultTypeId", "faultPriorityId", "jobStatusId",
		"ppmJobId", "fundingRouteId", "quotePriorityId", "contractorToQuoteId"})
public class Quote extends modelBase<Quote>{

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("callerId")
	private Integer callerId;

	@JsonProperty("siteId")
	private Integer siteId;
	
	@JsonProperty("assetClassificationId")
	private Integer assetClassificationId;
	
	@JsonProperty("assetId")
	private Integer assetId;
	   
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
	private Integer ppmJobId;
	
	@JsonProperty("fundingRouteId")
	private Integer fundingRouteId;
	
	@JsonProperty("quotePriorityId")
	private Integer quotePriorityId;
	
	@JsonProperty("contractorToQuoteId")
	private Integer contractorToQuoteId;
	

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCallerId() {
        return callerId;
    }

    public void setCallerId(Integer callerId) {
        this.callerId = callerId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getAssetClassificationId() {
        return assetClassificationId;
    }

    public void setAssetClassificationId(Integer assetClassificationId) {
        this.assetClassificationId = assetClassificationId;
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public Integer getAssetSubTypeId() {
        return assetSubTypeId;
    }

    public void setAssetSubTypeId(Integer assetSubTypeId) {
        this.assetSubTypeId = assetSubTypeId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getJobTypeId() {
        return jobTypeId;
    }

    public void setJobTypeId(Integer jobTypeId) {
        this.jobTypeId = jobTypeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFaultTypeId() {
        return faultTypeId;
    }

    public void setFaultTypeId(Integer faultTypeId) {
        this.faultTypeId = faultTypeId;
    }

    public Integer getFaultPriorityId() {
        return faultPriorityId;
    }

    public void setFaultPriorityId(Integer faultPriorityId) {
        this.faultPriorityId = faultPriorityId;
    }

    public Integer getJobStatusId() {
        return jobStatusId;
    }

    public void setJobStatusId(Integer jobStatusId) {
        this.jobStatusId = jobStatusId;
    }

    public Integer getPpmJobId() {
        return ppmJobId;
    }

    public void setPpmJobId(Integer ppmJobId) {
        this.ppmJobId = ppmJobId;
    }

    public Integer getFundingRouteId() {
        return fundingRouteId;
    }

    public void setFundingRouteId(Integer fundingRouteId) {
        this.fundingRouteId = fundingRouteId;
    }

    public Integer getQuotePriorityId() {
        return quotePriorityId;
    }

    public void setQuotePriorityId(Integer quotePriorityId) {
        this.quotePriorityId = quotePriorityId;
    }

    public Integer getContractorToQuoteId() {
        return contractorToQuoteId;
    }

    public void setContractorToQuoteId(Integer contractorToQuoteId) {
        this.contractorToQuoteId = contractorToQuoteId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("callerId", callerId)
                .append("siteId", siteId)
                .append("assetClassificationId", assetClassificationId)
                .append("assetId", assetId)
                .append("assetSubTypeId", assetSubTypeId)
                .append("locationId", locationId)
                .append("jobTypeId", jobTypeId)
                .append("description", description)
                .append("faultTypeId", faultTypeId)
                .append("faultPriorityId", faultPriorityId)
                .append("jobStatusId", jobStatusId)
                .append("ppmJobId", ppmJobId)
                .append("fundingRouteId", fundingRouteId)
                .append("contractorToQuoteId", contractorToQuoteId)
                .append("id", id)
                .toString().replace(",", ", ");
    }
    
}
