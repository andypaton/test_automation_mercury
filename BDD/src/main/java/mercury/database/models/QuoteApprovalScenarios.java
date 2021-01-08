package mercury.database.models;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.beans.BeanUtils;

@Entity
public class QuoteApprovalScenarios {

    @Id

    @Column(name = "IsMultiQuote")
    boolean isMultiQuote;

    @Column(name = "ApprovalStatusName")
    String approvalStatusName;

    @Column(name = "ApprovalStatusDescription")
    String approvalStatusDescription;

    @Column(name = "ApprovalStatusId")
    Integer approvalStatusId;

    @Column(name = "QuoteJobApprovalStatusName")
    String quoteJobApprovalStatusName;

    @Column(name = "QuoteJobApprovalStatusDescription")
    String quoteJobApprovalStatusDescription;

    @Column(name = "QuoteJobApprovalStatusId")
    Integer quoteJobApprovalStatusId;

    @Column(name = "AreaManagerId")
    Integer areaManagerId;

    @Column(name = "Cost")
    java.math.BigDecimal cost;

    @Column(name = "CostMinusUplift")
    java.math.BigDecimal costMinusUplift;

    @Column(name = "Created")
    Date created;

    @Column(name = "Description")
    String description;

    @Column(name = "FaultId")
    Integer faultId;

    @Column(name = "FaultReference")
    String faultReference;

    @Column(name = "FaultType")
    String faultType;

    @Column(name = "FaultPriority")
    String faultPriority;

    @Column(name = "SubTypeClassification")
    String subTypeClassification;

    @Column(name = "Location")
    String location;

    @Column(name = "InvitationToQuoteFaultTimeId")
    Integer invitationToQuoteFaultTimeId;

    @Column(name = "LoggedDate")
    Date loggedDate;

    @Column(name = "MaximumQuoteCost")
    java.math.BigDecimal maximumQuoteCost;

    @Column(name = "MostRecentQueryDate")
    Date mostRecentQueryDate;

    @Column(name = "MostRecentQueryReason")
    String mostRecentQueryReason;

    @Column(name = "MostRecentQueryResponse")
    String mostRecentQueryResponse;

    @Column(name = "MostRecentQueryResponseDate")
    Date mostRecentQueryResponseDate;

    @Column(name = "MostRecentQueryText")
    String mostRecentQueryText;

    @Column(name = "NumberOfQuotesRequired")
    Integer numberOfQuotesRequired;

    @Column(name = "ProjectHeaderId")
    Integer projectHeaderId;

    @Column(name = "Queried")
    boolean queried;

    @Column(name = "QuoteApprovalScenarioId")
    Integer quoteApprovalScenarioId;

    @Column(name = "QuoteDueDate")
    Date quoteDueDate;

    @Column(name = "QuotePriority")
    String quotePriority;

    @Column(name = "QuotePriorityId")
    Integer quotePriorityId;

    @Column(name = "ScopeOfWorks")
    String scopeOfWorks;

    @Column(name = "ScopeOfWorksId")
    Integer scopeOfWorksId;

    @Column(name = "ScopeOfWorksAddedDate")
    Date scopeOfWorksAddedDate;

    @Column(name = "ScopeOfWorksAddedFaultTimeId")
    Integer scopeOfWorksAddedFaultTimeId;

    @Column(name = "Store")
    String store;

    @Column(name = "StoreClusterId")
    Integer storeClusterId;

    @Column(name = "StoreId")
    Integer storeId;

    @Column(name = "StoreDivision")
    String storeDivision;

    @Column(name = "StoreDivisionId")
    Integer storeDivisionId;

    @Column(name = "ResourceName")
    String resourceName;

    @Column(name = "ResourceId")
    Integer resourceId;

    @Column(name = "ManagerApproverID")
    Integer managerApproverID;

    @Column(name = "ManagerApproverName")
    String managerApproverName;

    @Column(name = "SeniorManagerApproverName")
    String seniorManagerApproverName;

    @Column(name = "SeniorManagerApproverID")
    Integer seniorManagerApproverID;

    @Column(name = "QuoteRequestPriority")
    boolean quoteRequestPriority;

    @Column(name = "CallerTypeID")
    Integer callerTypeID;

    @Column(name = "FundingRouteID")
    Integer fundingRouteID;

    @Column(name = "ProcessTypeId")
    Integer processTypeId;

    @Column(name = "IsRetrospectiveQuote")
    boolean isRetrospectiveQuote;

    @Column(name = "MultiQuoteBypassReasonId")
    Integer multiQuoteBypassReasonId;

    @Column(name = "MultiQuoteBypassReasonName")
    String multiQuoteBypassReasonName;

    @Column(name = "MultiQuoteBypassNote")
    String multiQuoteBypassNote;

    @Column(name = "RequiresMultiQuote")
    boolean requiresMultiQuote;

    @Column(name = "ResourceHasAccepted")
    Boolean resourceHasAccepted;

    @Column(name = "ContactInfo")
    String contactInfo;

    @Column(name = "ResourceTypeName")
    String resourceTypeName;

    public boolean isMultiQuote() {
        return isMultiQuote;
    }

    public void setMultiQuote(boolean isMultiQuote) {
        this.isMultiQuote = isMultiQuote;
    }

    public String getApprovalStatusName() {
        return approvalStatusName;
    }

    public void setApprovalStatusName(String approvalStatusName) {
        this.approvalStatusName = approvalStatusName;
    }

    public String getApprovalStatusDescription() {
        return approvalStatusDescription;
    }

    public void setApprovalStatusDescription(String approvalStatusDescription) {
        this.approvalStatusDescription = approvalStatusDescription;
    }

    public Integer getApprovalStatusId() {
        return approvalStatusId;
    }

    public void setApprovalStatusId(Integer approvalStatusId) {
        this.approvalStatusId = approvalStatusId;
    }

    public String getQuoteJobApprovalStatusName() {
        return quoteJobApprovalStatusName;
    }

    public void setQuoteJobApprovalStatusName(String quoteJobApprovalStatusName) {
        this.quoteJobApprovalStatusName = quoteJobApprovalStatusName;
    }

    public String getQuoteJobApprovalStatusDescription() {
        return quoteJobApprovalStatusDescription;
    }

    public void setQuoteJobApprovalStatusDescription(String quoteJobApprovalStatusDescription) {
        this.quoteJobApprovalStatusDescription = quoteJobApprovalStatusDescription;
    }

    public Integer getQuoteJobApprovalStatusId() {
        return quoteJobApprovalStatusId;
    }

    public void setQuoteJobApprovalStatusId(Integer quoteJobApprovalStatusId) {
        this.quoteJobApprovalStatusId = quoteJobApprovalStatusId;
    }

    public Integer getAreaManagerId() {
        return areaManagerId;
    }

    public void setAreaManagerId(Integer areaManagerId) {
        this.areaManagerId = areaManagerId;
    }

    public java.math.BigDecimal getCost() {
        return cost;
    }

    public void setCost(java.math.BigDecimal cost) {
        this.cost = cost;
    }

    public java.math.BigDecimal getCostMinusUplift() {
        return costMinusUplift;
    }

    public void setCostMinusUplift(java.math.BigDecimal costMinusUplift) {
        this.costMinusUplift = costMinusUplift;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFaultId() {
        return faultId;
    }

    public void setFaultId(Integer faultId) {
        this.faultId = faultId;
    }

    public String getFaultReference() {
        return faultReference;
    }

    public void setFaultReference(String faultReference) {
        this.faultReference = faultReference;
    }

    public String getFaultType() {
        return faultType;
    }

    public void setFaultType(String faultType) {
        this.faultType = faultType;
    }

    public String getFaultPriority() {
        return faultPriority;
    }

    public void setFaultPriority(String faultPriority) {
        this.faultPriority = faultPriority;
    }

    public String getSubTypeClassification() {
        return subTypeClassification;
    }

    public void setSubTypeClassification(String subTypeClassification) {
        this.subTypeClassification = subTypeClassification;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getInvitationToQuoteFaultTimeId() {
        return invitationToQuoteFaultTimeId;
    }

    public void setInvitationToQuoteFaultTimeId(Integer invitationToQuoteFaultTimeId) {
        this.invitationToQuoteFaultTimeId = invitationToQuoteFaultTimeId;
    }

    public Date getLoggedDate() {
        return loggedDate;
    }

    public void setLoggedDate(Date loggedDate) {
        this.loggedDate = loggedDate;
    }

    public java.math.BigDecimal getMaximumQuoteCost() {
        return maximumQuoteCost;
    }

    public void setMaximumQuoteCost(java.math.BigDecimal maximumQuoteCost) {
        this.maximumQuoteCost = maximumQuoteCost;
    }

    public Date getMostRecentQueryDate() {
        return mostRecentQueryDate;
    }

    public void setMostRecentQueryDate(Date mostRecentQueryDate) {
        this.mostRecentQueryDate = mostRecentQueryDate;
    }

    public String getMostRecentQueryReason() {
        return mostRecentQueryReason;
    }

    public void setMostRecentQueryReason(String mostRecentQueryReason) {
        this.mostRecentQueryReason = mostRecentQueryReason;
    }

    public String getMostRecentQueryResponse() {
        return mostRecentQueryResponse;
    }

    public void setMostRecentQueryResponse(String mostRecentQueryResponse) {
        this.mostRecentQueryResponse = mostRecentQueryResponse;
    }

    public Date getMostRecentQueryResponseDate() {
        return mostRecentQueryResponseDate;
    }

    public void setMostRecentQueryResponseDate(Date mostRecentQueryResponseDate) {
        this.mostRecentQueryResponseDate = mostRecentQueryResponseDate;
    }

    public String getMostRecentQueryText() {
        return mostRecentQueryText;
    }

    public void setMostRecentQueryText(String mostRecentQueryText) {
        this.mostRecentQueryText = mostRecentQueryText;
    }

    public Integer getNumberOfQuotesRequired() {
        return numberOfQuotesRequired;
    }

    public void setNumberOfQuotesRequired(Integer numberOfQuotesRequired) {
        this.numberOfQuotesRequired = numberOfQuotesRequired;
    }

    public Integer getProjectHeaderId() {
        return projectHeaderId;
    }

    public void setProjectHeaderId(Integer projectHeaderId) {
        this.projectHeaderId = projectHeaderId;
    }

    public boolean isQueried() {
        return queried;
    }

    public void setQueried(boolean queried) {
        this.queried = queried;
    }

    public Integer getQuoteApprovalScenarioId() {
        return quoteApprovalScenarioId;
    }

    public void setQuoteApprovalScenarioId(Integer quoteApprovalScenarioId) {
        this.quoteApprovalScenarioId = quoteApprovalScenarioId;
    }

    public Date getQuoteDueDate() {
        return quoteDueDate;
    }

    public void setQuoteDueDate(Date quoteDueDate) {
        this.quoteDueDate = quoteDueDate;
    }

    public String getQuotePriority() {
        return quotePriority;
    }

    public void setQuotePriority(String quotePriority) {
        this.quotePriority = quotePriority;
    }

    public Integer getQuotePriorityId() {
        return quotePriorityId;
    }

    public void setQuotePriorityId(Integer quotePriorityId) {
        this.quotePriorityId = quotePriorityId;
    }

    public String getScopeOfWorks() {
        return scopeOfWorks;
    }

    public void setScopeOfWorks(String scopeOfWorks) {
        this.scopeOfWorks = scopeOfWorks;
    }

    public Integer getScopeOfWorksId() {
        return scopeOfWorksId;
    }

    public void setScopeOfWorksId(Integer scopeOfWorksId) {
        this.scopeOfWorksId = scopeOfWorksId;
    }

    public Date getScopeOfWorksAddedDate() {
        return scopeOfWorksAddedDate;
    }

    public void setScopeOfWorksAddedDate(Date scopeOfWorksAddedDate) {
        this.scopeOfWorksAddedDate = scopeOfWorksAddedDate;
    }

    public Integer getScopeOfWorksAddedFaultTimeId() {
        return scopeOfWorksAddedFaultTimeId;
    }

    public void setScopeOfWorksAddedFaultTimeId(Integer scopeOfWorksAddedFaultTimeId) {
        this.scopeOfWorksAddedFaultTimeId = scopeOfWorksAddedFaultTimeId;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public Integer getStoreClusterId() {
        return storeClusterId;
    }

    public void setStoreClusterId(Integer storeClusterId) {
        this.storeClusterId = storeClusterId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getStoreDivision() {
        return storeDivision;
    }

    public void setStoreDivision(String storeDivision) {
        this.storeDivision = storeDivision;
    }

    public Integer getStoreDivisionId() {
        return storeDivisionId;
    }

    public void setStoreDivisionId(Integer storeDivisionId) {
        this.storeDivisionId = storeDivisionId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getManagerApproverID() {
        return managerApproverID;
    }

    public void setManagerApproverID(Integer managerApproverID) {
        this.managerApproverID = managerApproverID;
    }

    public String getManagerApproverName() {
        return managerApproverName;
    }

    public void setManagerApproverName(String managerApproverName) {
        this.managerApproverName = managerApproverName;
    }

    public String getSeniorManagerApproverName() {
        return seniorManagerApproverName;
    }

    public void setSeniorManagerApproverName(String seniorManagerApproverName) {
        this.seniorManagerApproverName = seniorManagerApproverName;
    }

    public Integer getSeniorManagerApproverID() {
        return seniorManagerApproverID;
    }

    public void setSeniorManagerApproverID(Integer seniorManagerApproverID) {
        this.seniorManagerApproverID = seniorManagerApproverID;
    }

    public boolean isQuoteRequestPriority() {
        return quoteRequestPriority;
    }

    public void setQuoteRequestPriority(boolean quoteRequestPriority) {
        this.quoteRequestPriority = quoteRequestPriority;
    }

    public Integer getCallerTypeID() {
        return callerTypeID;
    }

    public void setCallerTypeID(Integer callerTypeID) {
        this.callerTypeID = callerTypeID;
    }

    public Integer getFundingRouteID() {
        return fundingRouteID;
    }

    public void setFundingRouteID(Integer fundingRouteID) {
        this.fundingRouteID = fundingRouteID;
    }

    public Integer getProcessTypeId() {
        return processTypeId;
    }

    public void setProcessTypeId(Integer processTypeId) {
        this.processTypeId = processTypeId;
    }

    public boolean isRetrospectiveQuote() {
        return isRetrospectiveQuote;
    }

    public void setRetrospectiveQuote(boolean isRetrospectiveQuote) {
        this.isRetrospectiveQuote = isRetrospectiveQuote;
    }

    public Integer getMultiQuoteBypassReasonId() {
        return multiQuoteBypassReasonId;
    }

    public void setMultiQuoteBypassReasonId(Integer multiQuoteBypassReasonId) {
        this.multiQuoteBypassReasonId = multiQuoteBypassReasonId;
    }

    public String getMultiQuoteBypassReasonName() {
        return multiQuoteBypassReasonName;
    }

    public void setMultiQuoteBypassReasonName(String multiQuoteBypassReasonName) {
        this.multiQuoteBypassReasonName = multiQuoteBypassReasonName;
    }

    public String getMultiQuoteBypassNote() {
        return multiQuoteBypassNote;
    }

    public void setMultiQuoteBypassNote(String multiQuoteBypassNote) {
        this.multiQuoteBypassNote = multiQuoteBypassNote;
    }

    public boolean isRequiresMultiQuote() {
        return requiresMultiQuote;
    }

    public void setRequiresMultiQuote(boolean requiresMultiQuote) {
        this.requiresMultiQuote = requiresMultiQuote;
    }

    public boolean isResourceHasAccepted() {
        return resourceHasAccepted;
    }

    public void setResourceHasAccepted(boolean resourceHasAccepted) {
        this.resourceHasAccepted = resourceHasAccepted;
    }

    public void copy(QuoteApprovalScenarios quoteApprovalScenarios) {
        BeanUtils.copyProperties(quoteApprovalScenarios, this);
    }
}
