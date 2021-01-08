package mercury.api.models.portal.quoteCreateEdit;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;
import mercury.api.models.job.Job;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "jobId",
    "jobReference",
    "quoteRequestApproverId",
    "quoteApproverId",
    "fundingRouteId",
    "jobProcessTypeId",
    "isApproved",
    "job",
    "quoteRequestApprover",
    "quoteApprover",
    "quoteAnswers"
})
public class QuoteRequestResponse extends modelBase<QuoteRequestResponse>{

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("jobId")
    private Integer jobId;

    @JsonProperty("jobReference")
    private Integer jobReference;

    @JsonProperty("quoteRequestApproverId")
    private Integer quoteRequestApproverId;

    @JsonProperty("quoteApproverId")
    private Object quoteApproverId;

    @JsonProperty("fundingRouteId")
    private Integer fundingRouteId;

    @JsonProperty("jobProcessTypeId")
    private Integer jobProcessTypeId;

    @JsonProperty("isApproved")
    private Boolean isApproved;

    @JsonProperty("job")
    private Job job;

    @JsonProperty("quoteRequestApprover")
    private QuoteRequestApprover quoteRequestApprover;

    @JsonProperty("quoteApprover")
    private Object quoteApprover;

    @JsonProperty("quoteAnswers")
    private List<Object> quoteAnswers = null;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("jobId")
    public Integer getJobId() {
        return jobId;
    }

    @JsonProperty("jobId")
    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    @JsonProperty("jobReference")
    public Integer getJobReference() {
        return jobReference;
    }

    @JsonProperty("jobReference")
    public void setJobReference(Integer jobReference) {
        this.jobReference = jobReference;
    }

    @JsonProperty("quoteRequestApproverId")
    public Integer getQuoteRequestApproverId() {
        return quoteRequestApproverId;
    }

    @JsonProperty("quoteRequestApproverId")
    public void setQuoteRequestApproverId(Integer quoteRequestApproverId) {
        this.quoteRequestApproverId = quoteRequestApproverId;
    }

    @JsonProperty("quoteApproverId")
    public Object getQuoteApproverId() {
        return quoteApproverId;
    }

    @JsonProperty("quoteApproverId")
    public void setQuoteApproverId(Object quoteApproverId) {
        this.quoteApproverId = quoteApproverId;
    }

    @JsonProperty("fundingRouteId")
    public Integer getFundingRouteId() {
        return fundingRouteId;
    }

    @JsonProperty("fundingRouteId")
    public void setFundingRouteId(Integer fundingRouteId) {
        this.fundingRouteId = fundingRouteId;
    }

    @JsonProperty("jobProcessTypeId")
    public Integer getJobProcessTypeId() {
        return jobProcessTypeId;
    }

    @JsonProperty("jobProcessTypeId")
    public void setJobProcessTypeId(Integer jobProcessTypeId) {
        this.jobProcessTypeId = jobProcessTypeId;
    }

    @JsonProperty("isApproved")
    public Boolean getIsApproved() {
        return isApproved;
    }

    @JsonProperty("isApproved")
    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    @JsonProperty("job")
    public Job getJob() {
        return job;
    }

    @JsonProperty("job")
    public void setJob(Job job) {
        this.job = job;
    }

    @JsonProperty("quoteRequestApprover")
    public QuoteRequestApprover getQuoteRequestApprover() {
        return quoteRequestApprover;
    }

    @JsonProperty("quoteRequestApprover")
    public void setQuoteRequestApprover(QuoteRequestApprover quoteRequestApprover) {
        this.quoteRequestApprover = quoteRequestApprover;
    }

    @JsonProperty("quoteApprover")
    public Object getQuoteApprover() {
        return quoteApprover;
    }

    @JsonProperty("quoteApprover")
    public void setQuoteApprover(Object quoteApprover) {
        this.quoteApprover = quoteApprover;
    }

    @JsonProperty("quoteAnswers")
    public List<Object> getQuoteAnswers() {
        return quoteAnswers;
    }

    @JsonProperty("quoteAnswers")
    public void setQuoteAnswers(List<Object> quoteAnswers) {
        this.quoteAnswers = quoteAnswers;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("jobId", jobId).append("jobReference", jobReference).append("quoteRequestApproverId", quoteRequestApproverId).append("quoteApproverId", quoteApproverId).append("fundingRouteId", fundingRouteId).append("jobProcessTypeId", jobProcessTypeId).append("isApproved", isApproved).append("job", job).append("quoteRequestApprover", quoteRequestApprover).append("quoteApprover", quoteApprover).append("quoteAnswers", quoteAnswers).toString();
    }
}