package mercury.api.models.mercuryportal.api.quotesAwaitingApprovalRetrieval;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "quoteRef",
    "quoteDate",
    "quoteValue",
    "resource",
    "resourceId",
    "portalResourceId",
    "itqAwaitingAcceptance",
    "hasQuoteSubmitted",
    "hasQueryPending",
    "isQueried",
    "quoteDocumentLink",
    "supportingDocumentsLink",
    "descriptionOfWorks",
    "proposedWorkingTimes",
    "worksAreHighRisk",
    "highRiskWorkTypes",
    "quoteLines",
    "initialApproverRecommendation",
    "seniorManagerRecommendation"
})
public class ResourceQuote extends modelBase<ResourceQuote>  {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("quoteRef")
    private String quoteRef;

    @JsonProperty("quoteDate")
    private String quoteDate;

    @JsonProperty("quoteValue")
    private Float quoteValue;

    @JsonProperty("resource")
    private String resource;

    @JsonProperty("resourceId")
    private Integer resourceId;

    @JsonProperty("portalResourceId")
    private Integer portalResourceId;

    @JsonProperty("itqAwaitingAcceptance")
    private Boolean itqAwaitingAcceptance;

    @JsonProperty("hasQuoteSubmitted")
    private Boolean hasQuoteSubmitted;

    @JsonProperty("hasQueryPending")
    private Boolean hasQueryPending;

    @JsonProperty("isQueried")
    private Boolean isQueried;

    @JsonProperty("quoteDocumentLink")
    private String quoteDocumentLink;

    @JsonProperty("supportingDocumentsLink")
    private String supportingDocumentsLink;

    @JsonProperty("descriptionOfWorks")
    private String descriptionOfWorks;

    @JsonProperty("proposedWorkingTimes")
    private String proposedWorkingTimes;

    @JsonProperty("worksAreHighRisk")
    private Boolean worksAreHighRisk;

    @JsonProperty("highRiskWorkTypes")
    private String highRiskWorkTypes;

    @JsonProperty("quoteLines")
    private List<QuoteLine> quoteLines = null;

    @JsonProperty("initialApproverRecommendation")
    private Object initialApproverRecommendation;

    @JsonProperty("seniorManagerRecommendation")
    private Object seniorManagerRecommendation;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    public ResourceQuote withId(Integer id) {
        this.id = id;
        return this;
    }

    @JsonProperty("quoteRef")
    public String getQuoteRef() {
        return quoteRef;
    }

    @JsonProperty("quoteRef")
    public void setQuoteRef(String quoteRef) {
        this.quoteRef = quoteRef;
    }

    public ResourceQuote withQuoteRef(String quoteRef) {
        this.quoteRef = quoteRef;
        return this;
    }

    @JsonProperty("quoteDate")
    public String getQuoteDate() {
        return quoteDate;
    }

    @JsonProperty("quoteDate")
    public void setQuoteDate(String quoteDate) {
        this.quoteDate = quoteDate;
    }

    public ResourceQuote withQuoteDate(String quoteDate) {
        this.quoteDate = quoteDate;
        return this;
    }

    @JsonProperty("quoteValue")
    public Float getQuoteValue() {
        return quoteValue;
    }

    @JsonProperty("quoteValue")
    public void setQuoteValue(Float quoteValue) {
        this.quoteValue = quoteValue;
    }

    public ResourceQuote withQuoteValue(Float quoteValue) {
        this.quoteValue = quoteValue;
        return this;
    }

    @JsonProperty("resource")
    public String getResource() {
        return resource;
    }

    @JsonProperty("resource")
    public void setResource(String resource) {
        this.resource = resource;
    }

    public ResourceQuote withResource(String resource) {
        this.resource = resource;
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

    public ResourceQuote withResourceId(Integer resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    @JsonProperty("portalResourceId")
    public Integer getPortalResourceId() {
        return portalResourceId;
    }

    @JsonProperty("portalResourceId")
    public void setPortalResourceId(Integer portalResourceId) {
        this.portalResourceId = portalResourceId;
    }

    public ResourceQuote withPortalResourceId(Integer portalResourceId) {
        this.portalResourceId = portalResourceId;
        return this;
    }

    @JsonProperty("itqAwaitingAcceptance")
    public Boolean getItqAwaitingAcceptance() {
        return itqAwaitingAcceptance;
    }

    @JsonProperty("itqAwaitingAcceptance")
    public void setItqAwaitingAcceptance(Boolean itqAwaitingAcceptance) {
        this.itqAwaitingAcceptance = itqAwaitingAcceptance;
    }

    public ResourceQuote withItqAwaitingAcceptance(Boolean itqAwaitingAcceptance) {
        this.itqAwaitingAcceptance = itqAwaitingAcceptance;
        return this;
    }

    @JsonProperty("hasQuoteSubmitted")
    public Boolean getHasQuoteSubmitted() {
        return hasQuoteSubmitted;
    }

    @JsonProperty("hasQuoteSubmitted")
    public void setHasQuoteSubmitted(Boolean hasQuoteSubmitted) {
        this.hasQuoteSubmitted = hasQuoteSubmitted;
    }

    public ResourceQuote withHasQuoteSubmitted(Boolean hasQuoteSubmitted) {
        this.hasQuoteSubmitted = hasQuoteSubmitted;
        return this;
    }

    @JsonProperty("hasQueryPending")
    public Boolean getHasQueryPending() {
        return hasQueryPending;
    }

    @JsonProperty("hasQueryPending")
    public void setHasQueryPending(Boolean hasQueryPending) {
        this.hasQueryPending = hasQueryPending;
    }

    public ResourceQuote withHasQueryPending(Boolean hasQueryPending) {
        this.hasQueryPending = hasQueryPending;
        return this;
    }

    @JsonProperty("isQueried")
    public Boolean getIsQueried() {
        return isQueried;
    }

    @JsonProperty("isQueried")
    public void setIsQueried(Boolean isQueried) {
        this.isQueried = isQueried;
    }

    public ResourceQuote withIsQueried(Boolean isQueried) {
        this.isQueried = isQueried;
        return this;
    }

    @JsonProperty("quoteDocumentLink")
    public String getQuoteDocumentLink() {
        return quoteDocumentLink;
    }

    @JsonProperty("quoteDocumentLink")
    public void setQuoteDocumentLink(String quoteDocumentLink) {
        this.quoteDocumentLink = quoteDocumentLink;
    }

    public ResourceQuote withQuoteDocumentLink(String quoteDocumentLink) {
        this.quoteDocumentLink = quoteDocumentLink;
        return this;
    }

    @JsonProperty("supportingDocumentsLink")
    public String getSupportingDocumentsLink() {
        return supportingDocumentsLink;
    }

    @JsonProperty("supportingDocumentsLink")
    public void setSupportingDocumentsLink(String supportingDocumentsLink) {
        this.supportingDocumentsLink = supportingDocumentsLink;
    }

    public ResourceQuote withSupportingDocumentsLink(String supportingDocumentsLink) {
        this.supportingDocumentsLink = supportingDocumentsLink;
        return this;
    }

    @JsonProperty("descriptionOfWorks")
    public String getDescriptionOfWorks() {
        return descriptionOfWorks;
    }

    @JsonProperty("descriptionOfWorks")
    public void setDescriptionOfWorks(String descriptionOfWorks) {
        this.descriptionOfWorks = descriptionOfWorks;
    }

    public ResourceQuote withDescriptionOfWorks(String descriptionOfWorks) {
        this.descriptionOfWorks = descriptionOfWorks;
        return this;
    }

    @JsonProperty("proposedWorkingTimes")
    public String getProposedWorkingTimes() {
        return proposedWorkingTimes;
    }

    @JsonProperty("proposedWorkingTimes")
    public void setProposedWorkingTimes(String proposedWorkingTimes) {
        this.proposedWorkingTimes = proposedWorkingTimes;
    }

    public ResourceQuote withProposedWorkingTimes(String proposedWorkingTimes) {
        this.proposedWorkingTimes = proposedWorkingTimes;
        return this;
    }

    @JsonProperty("worksAreHighRisk")
    public Boolean getWorksAreHighRisk() {
        return worksAreHighRisk;
    }

    @JsonProperty("worksAreHighRisk")
    public void setWorksAreHighRisk(Boolean worksAreHighRisk) {
        this.worksAreHighRisk = worksAreHighRisk;
    }

    public ResourceQuote withWorksAreHighRisk(Boolean worksAreHighRisk) {
        this.worksAreHighRisk = worksAreHighRisk;
        return this;
    }

    @JsonProperty("highRiskWorkTypes")
    public String getHighRiskWorkTypes() {
        return highRiskWorkTypes;
    }

    @JsonProperty("highRiskWorkTypes")
    public void setHighRiskWorkTypes(String highRiskWorkTypes) {
        this.highRiskWorkTypes = highRiskWorkTypes;
    }

    public ResourceQuote withHighRiskWorkTypes(String highRiskWorkTypes) {
        this.highRiskWorkTypes = highRiskWorkTypes;
        return this;
    }

    @JsonProperty("quoteLines")
    public List<QuoteLine> getQuoteLines() {
        return quoteLines;
    }

    @JsonProperty("quoteLines")
    public void setQuoteLines(List<QuoteLine> quoteLines) {
        this.quoteLines = quoteLines;
    }

    public ResourceQuote withQuoteLines(List<QuoteLine> quoteLines) {
        this.quoteLines = quoteLines;
        return this;
    }

    @JsonProperty("initialApproverRecommendation")
    public Object getInitialApproverRecommendation() {
        return initialApproverRecommendation;
    }

    @JsonProperty("initialApproverRecommendation")
    public void setInitialApproverRecommendation(Object initialApproverRecommendation) {
        this.initialApproverRecommendation = initialApproverRecommendation;
    }

    public ResourceQuote withInitialApproverRecommendation(Object initialApproverRecommendation) {
        this.initialApproverRecommendation = initialApproverRecommendation;
        return this;
    }

    @JsonProperty("seniorManagerRecommendation")
    public Object getSeniorManagerRecommendation() {
        return seniorManagerRecommendation;
    }

    @JsonProperty("seniorManagerRecommendation")
    public void setSeniorManagerRecommendation(Object seniorManagerRecommendation) {
        this.seniorManagerRecommendation = seniorManagerRecommendation;
    }

    public ResourceQuote withSeniorManagerRecommendation(Object seniorManagerRecommendation) {
        this.seniorManagerRecommendation = seniorManagerRecommendation;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("quoteRef", quoteRef).append("quoteDate", quoteDate).append("quoteValue", quoteValue).append("resource", resource).append("resourceId", resourceId).append("portalResourceId", portalResourceId).append("itqAwaitingAcceptance", itqAwaitingAcceptance).append("hasQuoteSubmitted", hasQuoteSubmitted).append("hasQueryPending", hasQueryPending).append("isQueried", isQueried).append("quoteDocumentLink", quoteDocumentLink).append("supportingDocumentsLink", supportingDocumentsLink).append("descriptionOfWorks", descriptionOfWorks).append("proposedWorkingTimes", proposedWorkingTimes).append("worksAreHighRisk", worksAreHighRisk).append("highRiskWorkTypes", highRiskWorkTypes).append("quoteLines", quoteLines).append("initialApproverRecommendation", initialApproverRecommendation).append("seniorManagerRecommendation", seniorManagerRecommendation).toString();
    }

}
