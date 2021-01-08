package mercury.api.models.portal.quoteJobEdit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "InvitationToQuoteFaultTimeId",
    "QuoteApprovalScenarioId",
    "ResourceName",
    "ResourceId",
    "ResourceEmailAddress",
    "IsResource",
    "IsPreferredResource",
    "IsVendor",
    "IsPreferredVendor",
    "Cost"
})
public class InvitationsQuoted {

    @JsonProperty("InvitationToQuoteFaultTimeId")
    private Integer invitationToQuoteFaultTimeId;

    @JsonProperty("QuoteApprovalScenarioId")
    private Integer quoteApprovalScenarioId;

    @JsonProperty("ResourceName")
    private String resourceName;

    @JsonProperty("ResourceId")
    private Integer resourceId;

    @JsonProperty("ResourceEmailAddress")
    private String resourceEmailAddress;

    @JsonProperty("IsResource")
    private String isResource;

    @JsonProperty("IsPreferredResource")
    private String isPreferredResource;

    @JsonProperty("IsVendor")
    private String isVendor;

    @JsonProperty("IsPreferredVendor")
    private String isPreferredVendor;

    @JsonProperty("Cost")
    private BigDecimal cost;

    @JsonProperty("InvitationToQuoteFaultTimeId")
    public Integer getInvitationToQuoteFaultTimeId() {
        return invitationToQuoteFaultTimeId;
    }

    @JsonProperty("InvitationToQuoteFaultTimeId")
    public void setInvitationToQuoteFaultTimeId(Integer invitationToQuoteFaultTimeId) {
        this.invitationToQuoteFaultTimeId = invitationToQuoteFaultTimeId;
    }

    public InvitationsQuoted withInvitationToQuoteFaultTimeId(Integer invitationToQuoteFaultTimeId) {
        this.invitationToQuoteFaultTimeId = invitationToQuoteFaultTimeId;
        return this;
    }

    @JsonProperty("QuoteApprovalScenarioId")
    public Integer getQuoteApprovalScenarioId() {
        return quoteApprovalScenarioId;
    }

    @JsonProperty("QuoteApprovalScenarioId")
    public void setQuoteApprovalScenarioId(Integer quoteApprovalScenarioId) {
        this.quoteApprovalScenarioId = quoteApprovalScenarioId;
    }

    public InvitationsQuoted withQuoteApprovalScenarioId(Integer quoteApprovalScenarioId) {
        this.quoteApprovalScenarioId = quoteApprovalScenarioId;
        return this;
    }

    @JsonProperty("ResourceName")
    public String getResourceName() {
        return resourceName;
    }

    @JsonProperty("ResourceName")
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public InvitationsQuoted withResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    @JsonProperty("ResourceId")
    public Integer getResourceId() {
        return resourceId;
    }

    @JsonProperty("ResourceId")
    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public InvitationsQuoted withResourceId(Integer resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    @JsonProperty("ResourceEmailAddress")
    public String getResourceEmailAddress() {
        return resourceEmailAddress;
    }

    @JsonProperty("ResourceEmailAddress")
    public void setResourceEmailAddress(String resourceEmailAddress) {
        this.resourceEmailAddress = resourceEmailAddress;
    }

    public InvitationsQuoted withResourceEmailAddress(String resourceEmailAddress) {
        this.resourceEmailAddress = resourceEmailAddress;
        return this;
    }

    @JsonProperty("IsResource")
    public String getIsResource() {
        return isResource;
    }

    @JsonProperty("IsResource")
    public void setIsResource(String isResource) {
        this.isResource = isResource;
    }

    public InvitationsQuoted withIsResource(String isResource) {
        this.isResource = isResource;
        return this;
    }

    @JsonProperty("IsPreferredResource")
    public String getIsPreferredResource() {
        return isPreferredResource;
    }

    @JsonProperty("IsPreferredResource")
    public void setIsPreferredResource(String isPreferredResource) {
        this.isPreferredResource = isPreferredResource;
    }

    public InvitationsQuoted withIsPreferredResource(String isPreferredResource) {
        this.isPreferredResource = isPreferredResource;
        return this;
    }

    @JsonProperty("IsVendor")
    public String getIsVendor() {
        return isVendor;
    }

    @JsonProperty("IsVendor")
    public void setIsVendor(String isVendor) {
        this.isVendor = isVendor;
    }

    public InvitationsQuoted withIsVendor(String isVendor) {
        this.isVendor = isVendor;
        return this;
    }

    @JsonProperty("IsPreferredVendor")
    public String getIsPreferredVendor() {
        return isPreferredVendor;
    }

    @JsonProperty("IsPreferredVendor")
    public void setIsPreferredVendor(String isPreferredVendor) {
        this.isPreferredVendor = isPreferredVendor;
    }

    public InvitationsQuoted withIsPreferredVendor(String isPreferredVendor) {
        this.isPreferredVendor = isPreferredVendor;
        return this;
    }

    @JsonProperty("Cost")
    public BigDecimal getCost() {
        return cost;
    }

    @JsonProperty("Cost")
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public InvitationsQuoted withCost(BigDecimal cost) {
        this.cost = cost;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("invitationToQuoteFaultTimeId", invitationToQuoteFaultTimeId).append("quoteApprovalScenarioId", quoteApprovalScenarioId).append("resourceName", resourceName).append("resourceId", resourceId).append("resourceEmailAddress", resourceEmailAddress).append("isResource", isResource).append("isPreferredResource", isPreferredResource).append("isVendor", isVendor).append("isPreferredVendor", isPreferredVendor).append("cost", cost).toString();
    }

}