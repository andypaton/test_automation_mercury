package mercury.api.models.portal.job.updateFGas2019;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "JobId",
    "JobReference",
    "RootCauseCategoryId",
    "RootCauseId",
    "AdditionalNotes"
})
public class JobDetails extends modelBase<JobDetails>{
    @JsonProperty("JobId")
    private String jobId;
    @JsonProperty("JobReference")
    private String jobReference;

    @JsonProperty("RootCauseCategoryId")
    private String rootCauseCategoryId;

    @JsonProperty("RootCauseId")
    private String rootCauseId;

    @JsonProperty("AdditionalNotes")
    private String additionalNotes;

    public JobDetails() {
    }

    /**
     *
     * @param jobReference
     * @param jobId
     * @param rootCauseCategoryId
     * @param rootCauseId
     * @param additionalNotes
     */
    public JobDetails(String jobId, String jobReference, String rootCauseCategoryId, String rootCauseId, String additionalNotes) {
        super();
        this.jobId = jobId;
        this.jobReference = jobReference;
        this.rootCauseCategoryId = rootCauseCategoryId;
        this.rootCauseId = rootCauseId;
        this.additionalNotes = additionalNotes;
    }

    @JsonProperty("JobId")
    public String getJobId() {
        return jobId;
    }

    @JsonProperty("JobId")
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @JsonProperty("JobReference")
    public String getJobReference() {
        return jobReference;
    }

    @JsonProperty("JobReference")
    public void setJobReference(String jobReference) {
        this.jobReference = jobReference;
    }


    @JsonProperty("RootCauseCategoryId")
    public String getRootCauseCategoryId() {
        return rootCauseCategoryId;
    }

    @JsonProperty("RootCauseCategoryId")
    public void setRootCauseCategoryId(String rootCauseCategoryId) {
        this.rootCauseCategoryId = rootCauseCategoryId;
    }

    @JsonProperty("RootCauseId")
    public String getRootCauseId() {
        return rootCauseId;
    }

    @JsonProperty("RootCauseId")
    public void setRootCauseId(String rootCauseId) {
        this.rootCauseId = rootCauseId;
    }

    @JsonProperty("AdditionalNotes")
    public String getAdditionalNotes() {
        return additionalNotes;
    }

    @JsonProperty("AdditionalNotes")
    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this).append("jobId", jobId).append("jobReference", jobReference).append("rootCauseCategoryId", rootCauseCategoryId).append("rootCauseId", rootCauseId).append("additionalNotes", additionalNotes).toString();
    }
}
