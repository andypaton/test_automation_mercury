package mercury.api.models.mercuryportal.api.quotesAwaitingApprovalRetrieval;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "jobRef",
    "siteName",
    "siteId",
    "loggedDate",
    "scopeOfWorks",
    "scopeOfWorksId",
    "location",
    "subTypeClassification",
    "assetId",
    "assetSubTypeId",
    "assetClassificationId",
    "faultTypeId",
    "quotePriority",
    "quotePriorityId",
    "currentApproverIsTerminalApprover",
    "fundingRouteId",
    "processTypeId",
    "initialApproverName",
    "approverResourceProfileId",
    "approverLimit",
    "isMultiQuoteBypass",
    "isMultiQuote",
    "multiQuoteBypassNote",
    "multiQuoteBypassReasonName",
    "quoteInInternalQuery",
    "internalNotes"
})
public class Job extends modelBase<Job>  {

    @JsonProperty("jobRef")
    private Integer jobRef;
    
    @JsonProperty("siteName")
    private String siteName;
    
    @JsonProperty("siteId")
    private Integer siteId;
    
    @JsonProperty("loggedDate")
    private String loggedDate;
    
    @JsonProperty("scopeOfWorks")
    private String scopeOfWorks;
    
    @JsonProperty("scopeOfWorksId")
    private Integer scopeOfWorksId;
    
    @JsonProperty("location")
    private String location;
    
    @JsonProperty("subTypeClassification")
    private String subTypeClassification;
    
    @JsonProperty("assetId")
    private Object assetId;
    
    @JsonProperty("assetSubTypeId")
    private Object assetSubTypeId;
    
    @JsonProperty("assetClassificationId")
    private Integer assetClassificationId;
    
    @JsonProperty("faultTypeId")
    private Integer faultTypeId;
    
    @JsonProperty("quotePriority")
    private String quotePriority;
    
    @JsonProperty("quotePriorityId")
    private Integer quotePriorityId;
    
    @JsonProperty("currentApproverIsTerminalApprover")
    private Boolean currentApproverIsTerminalApprover;
    
    @JsonProperty("fundingRouteId")
    private Integer fundingRouteId;
    
    @JsonProperty("processTypeId")
    private Integer processTypeId;
    
    @JsonProperty("initialApproverName")
    private String initialApproverName;
    
    @JsonProperty("approverResourceProfileId")
    private Integer approverResourceProfileId;
    
    @JsonProperty("approverLimit")
    private Float approverLimit;
    
    @JsonProperty("isMultiQuoteBypass")
    private Boolean isMultiQuoteBypass;
    
    @JsonProperty("isMultiQuote")
    private Boolean isMultiQuote;
    
    @JsonProperty("multiQuoteBypassNote")
    private Object multiQuoteBypassNote;
    
    @JsonProperty("multiQuoteBypassReasonName")
    private Object multiQuoteBypassReasonName;
    
    @JsonProperty("quoteInInternalQuery")
    private Boolean quoteInInternalQuery;
    
    @JsonProperty("internalNotes")
    private List<Object> internalNotes = null;

    @JsonProperty("jobRef")
    public Integer getJobRef() {
        return jobRef;
    }

    @JsonProperty("jobRef")
    public void setJobRef(Integer jobRef) {
        this.jobRef = jobRef;
    }

    public Job withJobRef(Integer jobRef) {
        this.jobRef = jobRef;
        return this;
    }

    @JsonProperty("siteName")
    public String getSiteName() {
        return siteName;
    }

    @JsonProperty("siteName")
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Job withSiteName(String siteName) {
        this.siteName = siteName;
        return this;
    }

    @JsonProperty("siteId")
    public Integer getSiteId() {
        return siteId;
    }

    @JsonProperty("siteId")
    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Job withSiteId(Integer siteId) {
        this.siteId = siteId;
        return this;
    }

    @JsonProperty("loggedDate")
    public String getLoggedDate() {
        return loggedDate;
    }

    @JsonProperty("loggedDate")
    public void setLoggedDate(String loggedDate) {
        this.loggedDate = loggedDate;
    }

    public Job withLoggedDate(String loggedDate) {
        this.loggedDate = loggedDate;
        return this;
    }

    @JsonProperty("scopeOfWorks")
    public String getScopeOfWorks() {
        return scopeOfWorks;
    }

    @JsonProperty("scopeOfWorks")
    public void setScopeOfWorks(String scopeOfWorks) {
        this.scopeOfWorks = scopeOfWorks;
    }

    public Job withScopeOfWorks(String scopeOfWorks) {
        this.scopeOfWorks = scopeOfWorks;
        return this;
    }

    @JsonProperty("scopeOfWorksId")
    public Integer getScopeOfWorksId() {
        return scopeOfWorksId;
    }

    @JsonProperty("scopeOfWorksId")
    public void setScopeOfWorksId(Integer scopeOfWorksId) {
        this.scopeOfWorksId = scopeOfWorksId;
    }

    public Job withScopeOfWorksId(Integer scopeOfWorksId) {
        this.scopeOfWorksId = scopeOfWorksId;
        return this;
    }

    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(String location) {
        this.location = location;
    }

    public Job withLocation(String location) {
        this.location = location;
        return this;
    }

    @JsonProperty("subTypeClassification")
    public String getSubTypeClassification() {
        return subTypeClassification;
    }

    @JsonProperty("subTypeClassification")
    public void setSubTypeClassification(String subTypeClassification) {
        this.subTypeClassification = subTypeClassification;
    }

    public Job withSubTypeClassification(String subTypeClassification) {
        this.subTypeClassification = subTypeClassification;
        return this;
    }

    @JsonProperty("assetId")
    public Object getAssetId() {
        return assetId;
    }

    @JsonProperty("assetId")
    public void setAssetId(Object assetId) {
        this.assetId = assetId;
    }

    public Job withAssetId(Object assetId) {
        this.assetId = assetId;
        return this;
    }

    @JsonProperty("assetSubTypeId")
    public Object getAssetSubTypeId() {
        return assetSubTypeId;
    }

    @JsonProperty("assetSubTypeId")
    public void setAssetSubTypeId(Object assetSubTypeId) {
        this.assetSubTypeId = assetSubTypeId;
    }

    public Job withAssetSubTypeId(Object assetSubTypeId) {
        this.assetSubTypeId = assetSubTypeId;
        return this;
    }

    @JsonProperty("assetClassificationId")
    public Integer getAssetClassificationId() {
        return assetClassificationId;
    }

    @JsonProperty("assetClassificationId")
    public void setAssetClassificationId(Integer assetClassificationId) {
        this.assetClassificationId = assetClassificationId;
    }

    public Job withAssetClassificationId(Integer assetClassificationId) {
        this.assetClassificationId = assetClassificationId;
        return this;
    }

    @JsonProperty("faultTypeId")
    public Integer getFaultTypeId() {
        return faultTypeId;
    }

    @JsonProperty("faultTypeId")
    public void setFaultTypeId(Integer faultTypeId) {
        this.faultTypeId = faultTypeId;
    }

    public Job withFaultTypeId(Integer faultTypeId) {
        this.faultTypeId = faultTypeId;
        return this;
    }

    @JsonProperty("quotePriority")
    public String getQuotePriority() {
        return quotePriority;
    }

    @JsonProperty("quotePriority")
    public void setQuotePriority(String quotePriority) {
        this.quotePriority = quotePriority;
    }

    public Job withQuotePriority(String quotePriority) {
        this.quotePriority = quotePriority;
        return this;
    }

    @JsonProperty("quotePriorityId")
    public Integer getQuotePriorityId() {
        return quotePriorityId;
    }

    @JsonProperty("quotePriorityId")
    public void setQuotePriorityId(Integer quotePriorityId) {
        this.quotePriorityId = quotePriorityId;
    }

    public Job withQuotePriorityId(Integer quotePriorityId) {
        this.quotePriorityId = quotePriorityId;
        return this;
    }

    @JsonProperty("currentApproverIsTerminalApprover")
    public Boolean getCurrentApproverIsTerminalApprover() {
        return currentApproverIsTerminalApprover;
    }

    @JsonProperty("currentApproverIsTerminalApprover")
    public void setCurrentApproverIsTerminalApprover(Boolean currentApproverIsTerminalApprover) {
        this.currentApproverIsTerminalApprover = currentApproverIsTerminalApprover;
    }

    public Job withCurrentApproverIsTerminalApprover(Boolean currentApproverIsTerminalApprover) {
        this.currentApproverIsTerminalApprover = currentApproverIsTerminalApprover;
        return this;
    }

    @JsonProperty("fundingRouteId")
    public Integer getFundingRouteId() {
        return fundingRouteId;
    }

    @JsonProperty("fundingRouteId")
    public void setFundingRouteId(Integer fundingRouteId) {
        this.fundingRouteId = fundingRouteId;
    }

    public Job withFundingRouteId(Integer fundingRouteId) {
        this.fundingRouteId = fundingRouteId;
        return this;
    }

    @JsonProperty("processTypeId")
    public Integer getProcessTypeId() {
        return processTypeId;
    }

    @JsonProperty("processTypeId")
    public void setProcessTypeId(Integer processTypeId) {
        this.processTypeId = processTypeId;
    }

    public Job withProcessTypeId(Integer processTypeId) {
        this.processTypeId = processTypeId;
        return this;
    }

    @JsonProperty("initialApproverName")
    public String getInitialApproverName() {
        return initialApproverName;
    }

    @JsonProperty("initialApproverName")
    public void setInitialApproverName(String initialApproverName) {
        this.initialApproverName = initialApproverName;
    }

    public Job withInitialApproverName(String initialApproverName) {
        this.initialApproverName = initialApproverName;
        return this;
    }

    @JsonProperty("approverResourceProfileId")
    public Integer getApproverResourceProfileId() {
        return approverResourceProfileId;
    }

    @JsonProperty("approverResourceProfileId")
    public void setApproverResourceProfileId(Integer approverResourceProfileId) {
        this.approverResourceProfileId = approverResourceProfileId;
    }

    public Job withApproverResourceProfileId(Integer approverResourceProfileId) {
        this.approverResourceProfileId = approverResourceProfileId;
        return this;
    }

    @JsonProperty("approverLimit")
    public Float getApproverLimit() {
        return approverLimit;
    }

    @JsonProperty("approverLimit")
    public void setApproverLimit(Float approverLimit) {
        this.approverLimit = approverLimit;
    }

    public Job withApproverLimit(Float approverLimit) {
        this.approverLimit = approverLimit;
        return this;
    }

    @JsonProperty("isMultiQuoteBypass")
    public Boolean getIsMultiQuoteBypass() {
        return isMultiQuoteBypass;
    }

    @JsonProperty("isMultiQuoteBypass")
    public void setIsMultiQuoteBypass(Boolean isMultiQuoteBypass) {
        this.isMultiQuoteBypass = isMultiQuoteBypass;
    }

    public Job withIsMultiQuoteBypass(Boolean isMultiQuoteBypass) {
        this.isMultiQuoteBypass = isMultiQuoteBypass;
        return this;
    }

    @JsonProperty("isMultiQuote")
    public Boolean getIsMultiQuote() {
        return isMultiQuote;
    }

    @JsonProperty("isMultiQuote")
    public void setIsMultiQuote(Boolean isMultiQuote) {
        this.isMultiQuote = isMultiQuote;
    }

    public Job withIsMultiQuote(Boolean isMultiQuote) {
        this.isMultiQuote = isMultiQuote;
        return this;
    }

    @JsonProperty("multiQuoteBypassNote")
    public Object getMultiQuoteBypassNote() {
        return multiQuoteBypassNote;
    }

    @JsonProperty("multiQuoteBypassNote")
    public void setMultiQuoteBypassNote(Object multiQuoteBypassNote) {
        this.multiQuoteBypassNote = multiQuoteBypassNote;
    }

    public Job withMultiQuoteBypassNote(Object multiQuoteBypassNote) {
        this.multiQuoteBypassNote = multiQuoteBypassNote;
        return this;
    }

    @JsonProperty("multiQuoteBypassReasonName")
    public Object getMultiQuoteBypassReasonName() {
        return multiQuoteBypassReasonName;
    }

    @JsonProperty("multiQuoteBypassReasonName")
    public void setMultiQuoteBypassReasonName(Object multiQuoteBypassReasonName) {
        this.multiQuoteBypassReasonName = multiQuoteBypassReasonName;
    }

    public Job withMultiQuoteBypassReasonName(Object multiQuoteBypassReasonName) {
        this.multiQuoteBypassReasonName = multiQuoteBypassReasonName;
        return this;
    }

    @JsonProperty("quoteInInternalQuery")
    public Boolean getQuoteInInternalQuery() {
        return quoteInInternalQuery;
    }

    @JsonProperty("quoteInInternalQuery")
    public void setQuoteInInternalQuery(Boolean quoteInInternalQuery) {
        this.quoteInInternalQuery = quoteInInternalQuery;
    }

    public Job withQuoteInInternalQuery(Boolean quoteInInternalQuery) {
        this.quoteInInternalQuery = quoteInInternalQuery;
        return this;
    }

    @JsonProperty("internalNotes")
    public List<Object> getInternalNotes() {
        return internalNotes;
    }

    @JsonProperty("internalNotes")
    public void setInternalNotes(List<Object> internalNotes) {
        this.internalNotes = internalNotes;
    }

    public Job withInternalNotes(List<Object> internalNotes) {
        this.internalNotes = internalNotes;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("jobRef", jobRef).append("siteName", siteName).append("siteId", siteId).append("loggedDate", loggedDate).append("scopeOfWorks", scopeOfWorks).append("scopeOfWorksId", scopeOfWorksId).append("location", location).append("subTypeClassification", subTypeClassification).append("assetId", assetId).append("assetSubTypeId", assetSubTypeId).append("assetClassificationId", assetClassificationId).append("faultTypeId", faultTypeId).append("quotePriority", quotePriority).append("quotePriorityId", quotePriorityId).append("currentApproverIsTerminalApprover", currentApproverIsTerminalApprover).append("fundingRouteId", fundingRouteId).append("processTypeId", processTypeId).append("initialApproverName", initialApproverName).append("approverResourceProfileId", approverResourceProfileId).append("approverLimit", approverLimit).append("isMultiQuoteBypass", isMultiQuoteBypass).append("isMultiQuote", isMultiQuote).append("multiQuoteBypassNote", multiQuoteBypassNote).append("multiQuoteBypassReasonName", multiQuoteBypassReasonName).append("quoteInInternalQuery", quoteInInternalQuery).append("internalNotes", internalNotes).toString();
    }

}