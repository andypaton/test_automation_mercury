package mercury.api.models.job;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "siteId", "jobReference", "jobTypeId", "faultTypeId", "responsePriorityId", "timestamp",
    "description", "isDeferred", "deferralNote", "deferralTypeId", "jobStatusId", "reasonNotLoggedAgainstAssetId",
    "assetId", "callerId", "assetClassificationId", "locationId", "specificResourceRequestId", "jobSourceId", "quoteAnswers",
    "fundingRouteId", "quotePriorityId", "contractorToQuoteId", "deferralDate", "id","CreateQuote", "createdBy" })
public class Job {

    @JsonProperty("siteId")
    private Integer siteId;

    @JsonProperty("jobReference")
    private Integer jobReference;

    @JsonProperty("jobTypeId")
    private Integer jobTypeId;

    @JsonProperty("faultTypeId")
    private Integer faultTypeId;

    @JsonProperty("responsePriorityId")
    private Integer responsePriorityId;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("description")
    private String description;

    @JsonProperty("isDeferred")
    private Boolean isDeferred;

    @JsonProperty("deferralNote")
    private String deferralNote;

    @JsonProperty("deferralTypeId")
    private Integer deferralTypeId;

    @JsonProperty("jobStatusId")
    private Integer jobStatusId;

    @JsonProperty("reasonNotLoggedAgainstAssetId")
    private Integer reasonNotLoggedAgainstAssetId;

    @JsonProperty("assetId")
    private Integer assetId;

    @JsonProperty("callerId")
    private Integer callerId;

    @JsonProperty("assetClassificationId")
    private Integer assetClassificationId;

    @JsonProperty("locationId")
    private Integer locationId;

    @JsonProperty("deferralDate")
    private String deferralDate;

    @JsonProperty("specificResourceRequestId")
    private Integer specificResourceRequestId;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("jobSourceId")
    private Integer jobSourceId;

    @JsonProperty("quoteAnswers")
    private Object quoteAnswers;

    @JsonProperty("fundingRouteId")
    private Integer fundingRouteId;

    @JsonProperty("quotePriorityId")
    private Integer quotePriorityId;

    @JsonProperty("contractorToQuoteId")
    private Object contractorToQuoteId;

    @JsonProperty("CreateQuote")
    private Boolean createQuote;

    @JsonProperty("createdBy")
    private String createdBy;

    @JsonProperty("siteId")
    public Integer getSiteId() {
        return siteId;
    }

    @JsonProperty("siteId")
    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    @JsonProperty("jobReference")
    public Integer getJobReference() {
        return jobReference;
    }

    @JsonProperty("jobReference")
    public void setJobReference(Integer jobReference) {
        this.jobReference = jobReference;
    }

    @JsonProperty("jobTypeId")
    public Integer getJobTypeId() {
        return jobTypeId;
    }

    @JsonProperty("jobTypeId")
    public void setJobTypeId(Integer jobTypeId) {
        this.jobTypeId = jobTypeId;
    }

    @JsonProperty("faultTypeId")
    public Integer getFaultTypeId() {
        return faultTypeId;
    }

    @JsonProperty("faultTypeId")
    public void setFaultTypeId(Integer faultTypeId) {
        this.faultTypeId = faultTypeId;
    }

    @JsonProperty("responsePriorityId")
    public Integer getFaultPriorityId() {
        return responsePriorityId;
    }

    @JsonProperty("responsePriorityId")
    public void setFaultPriorityId(Integer faultPriorityId) {
        this.responsePriorityId = faultPriorityId;
    }

    @JsonProperty("timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("isDeferred")
    public Boolean getIsDeferred() {
        return isDeferred;
    }

    @JsonProperty("isDeferred")
    public void setIsDeferred(Boolean isDeferred) {
        this.isDeferred = isDeferred;
    }

    @JsonProperty("createdBy")
    public String getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("createdBy")
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("deferralNote")
    public Object getDeferralNote() {
        return deferralNote;
    }

    @JsonProperty("deferralNote")
    public void setDeferralNote(String deferralNote) {
        this.deferralNote = deferralNote;
    }

    @JsonProperty("deferralTypeId")
    public Object getDeferralTypeId() {
        return deferralTypeId;
    }

    @JsonProperty("deferralTypeId")
    public void setDeferralTypeId(Integer deferralTypeId) {
        this.deferralTypeId = deferralTypeId;
    }

    @JsonProperty("jobStatusId")
    public Integer getJobStatusId() {
        return jobStatusId;
    }

    @JsonProperty("jobStatusId")
    public void setJobStatusId(Integer jobStatusId) {
        this.jobStatusId = jobStatusId;
    }

    @JsonProperty("reasonNotLoggedAgainstAssetId")
    public Object getReasonNotLoggedAgainstAssetId() {
        return reasonNotLoggedAgainstAssetId;
    }

    @JsonProperty("reasonNotLoggedAgainstAssetId")
    public void setReasonNotLoggedAgainstAssetId(Integer reasonNotLoggedAgainstAssetId) {
        this.reasonNotLoggedAgainstAssetId = reasonNotLoggedAgainstAssetId;
    }

    @JsonProperty("assetId")
    public Integer getAssetId() {
        return assetId;
    }

    @JsonProperty("assetId")
    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    @JsonProperty("callerId")
    public Integer getCallerId() {
        return callerId;
    }

    @JsonProperty("callerId")
    public void setCallerId(Integer callerId) {
        this.callerId = callerId;
    }

    @JsonProperty("assetClassificationId")
    public Integer getAssetClassificationId() {
        return assetClassificationId;
    }

    @JsonProperty("assetClassificationId")
    public void setAssetClassificationId(Integer assetClassificationId) {
        this.assetClassificationId = assetClassificationId;
    }

    @JsonProperty("locationId")
    public Integer getLocationId() {
        return locationId;
    }

    @JsonProperty("locationId")
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    @JsonProperty("deferralDate")
    public Object getDeferralDate() {
        return deferralDate;
    }

    @JsonProperty("deferralDate")
    public void setDeferralDate(String deferralDate) {
        this.deferralDate = deferralDate;
    }

    @JsonProperty("specificResourceRequestId")
    public Integer getSpecificResourceRequestId() {
        return specificResourceRequestId;
    }

    @JsonProperty("specificResourceRequestId")
    public void setSpecificResourceRequestId(Integer specificResourceRequestId) {
        this.specificResourceRequestId = specificResourceRequestId;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("jobSourceId")
    public Integer getJobSourceId() {
        return jobSourceId;
    }

    @JsonProperty("jobSourceId")
    public void setJobSourceId(Integer jobSourceId) {
        this.jobSourceId = jobSourceId;
    }

    @JsonProperty("quoteAnswers")
    public Object getQuoteAnswers() {
        return quoteAnswers;
    }

    @JsonProperty("quoteAnswers")
    public void setQuoteAnswers(Object quoteAnswers) {
        this.quoteAnswers = quoteAnswers;
    }

    @JsonProperty("fundingRouteId")
    public Integer getFundingRouteId() {
        return fundingRouteId;
    }

    @JsonProperty("fundingRouteId")
    public void setFundingRouteId(Integer fundingRouteId) {
        this.fundingRouteId = fundingRouteId;
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

    @JsonProperty("CreateQuote")
    public Boolean getCreateQuote() {
        return createQuote;
    }

    @JsonProperty("CreateQuote")
    public void setCreateQuote(Boolean createQuote) {
        this.createQuote = createQuote;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("siteId", siteId)
                .append("jobReference", jobReference)
                .append("jobTypeId", jobTypeId)
                .append("faultTypeId", faultTypeId)
                .append("responsePriorityId", responsePriorityId)
                .append("timestamp", timestamp)
                .append("description", description)
                .append("isDeferred", isDeferred)
                .append("deferralNote", deferralNote)
                .append("deferralTypeId", deferralTypeId)
                .append("jobStatusId", jobStatusId)
                .append("reasonNotLoggedAgainstAssetId", reasonNotLoggedAgainstAssetId)
                .append("assetId", assetId)
                .append("callerId", callerId)
                .append("assetClassificationId", assetClassificationId)
                .append("locationId", locationId)
                .append("jobSourceId", jobSourceId)
                .append("quoteAnswers", quoteAnswers)
                .append("fundingRouteId", fundingRouteId)
                .append("quotePriorityId", quotePriorityId)
                .append("contractorToQuoteId", contractorToQuoteId)
                .append("id", id)
                .append("specificResourceRequestId", specificResourceRequestId)
                .append("createdBy", createdBy)
                .append("createQuote", createQuote).toString()
                .toString().replace(",", ", ");
    }

}
