package mercury.api.models.portal.quoteCreateEdit;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "__RequestVerificationToken",
    "Id",
    "JobRef",
    "QuoteType",
    "DescriptionOfWorks",
    "WorksAreHighRisk",
    "WorksAreHighRiskrd",
    "ProposedWorkingTimes"
})
public class QuoteHeader extends modelBase<QuoteHeader>{

    @JsonProperty("__RequestVerificationToken")
    private String requestVerificationToken;

    @JsonProperty("Id")
    private String id;

    @JsonProperty("JobRef")
    private String jobRef;

    @JsonProperty("QuoteType")
    private String quoteType;

    @JsonProperty("DescriptionOfWorks")
    private String descriptionOfWorks;

    @JsonProperty("WorksAreHighRisk")
    private String worksAreHighRisk;

    @JsonProperty("WorksAreHighRiskrd")
    private String worksAreHighRiskrd;

    @JsonProperty("ProposedWorkingTimes")
    private String proposedWorkingTimes;

    @JsonProperty("__RequestVerificationToken")
    public String getRequestVerificationToken() {
        return requestVerificationToken;
    }

    @JsonProperty("__RequestVerificationToken")
    public void setRequestVerificationToken(String requestVerificationToken) {
        this.requestVerificationToken = requestVerificationToken;
    }

    public QuoteHeader withRequestVerificationToken(String requestVerificationToken) {
        this.requestVerificationToken = requestVerificationToken;
        return this;
    }

    @JsonProperty("Id")
    public String getId() {
        return id;
    }

    @JsonProperty("Id")
    public void setId(String id) {
        this.id = id;
    }

    public QuoteHeader withId(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("JobRef")
    public String getJobRef() {
        return jobRef;
    }

    @JsonProperty("JobRef")
    public void setJobRef(String jobRef) {
        this.jobRef = jobRef;
    }

    public QuoteHeader withJobRef(String jobRef) {
        this.jobRef = jobRef;
        return this;
    }

    @JsonProperty("QuoteType")
    public String getQuoteType() {
        return quoteType;
    }

    @JsonProperty("QuoteType")
    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    public QuoteHeader withQuoteType(String quoteType) {
        this.quoteType = quoteType;
        return this;
    }

    @JsonProperty("DescriptionOfWorks")
    public String getDescriptionOfWorks() {
        return descriptionOfWorks;
    }

    @JsonProperty("DescriptionOfWorks")
    public void setDescriptionOfWorks(String descriptionOfWorks) {
        this.descriptionOfWorks = descriptionOfWorks;
    }

    public QuoteHeader withDescriptionOfWorks(String descriptionOfWorks) {
        this.descriptionOfWorks = descriptionOfWorks;
        return this;
    }

    @JsonProperty("WorksAreHighRisk")
    public String getWorksAreHighRisk() {
        return worksAreHighRisk;
    }

    @JsonProperty("WorksAreHighRisk")
    public void setWorksAreHighRisk(String worksAreHighRisk) {
        this.worksAreHighRisk = worksAreHighRisk;
    }

    public QuoteHeader withWorksAreHighRisk(String worksAreHighRisk) {
        this.worksAreHighRisk = worksAreHighRisk;
        return this;
    }

    @JsonProperty("WorksAreHighRiskrd")
    public String getWorksAreHighRiskrd() {
        return worksAreHighRiskrd;
    }

    @JsonProperty("WorksAreHighRiskrd")
    public void setWorksAreHighRiskrd(String worksAreHighRiskrd) {
        this.worksAreHighRiskrd = worksAreHighRiskrd;
    }

    public QuoteHeader withWorksAreHighRiskrd(String worksAreHighRiskrd) {
        this.worksAreHighRiskrd = worksAreHighRiskrd;
        return this;
    }

    @JsonProperty("ProposedWorkingTimes")
    public String getProposedWorkingTimes() {
        return proposedWorkingTimes;
    }

    @JsonProperty("ProposedWorkingTimes")
    public void setProposedWorkingTimes(String proposedWorkingTimes) {
        this.proposedWorkingTimes = proposedWorkingTimes;
    }

    public QuoteHeader withProposedWorkingTimes(String proposedWorkingTimes) {
        this.proposedWorkingTimes = proposedWorkingTimes;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("requestVerificationToken", requestVerificationToken).append("id", id).append("jobRef", jobRef).append("quoteType", quoteType).append("descriptionOfWorks", descriptionOfWorks).append("worksAreHighRisk", worksAreHighRisk).append("worksAreHighRiskrd", worksAreHighRiskrd).append("proposedWorkingTimes", proposedWorkingTimes).toString();
    }
}

