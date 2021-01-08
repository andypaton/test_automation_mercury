package mercury.api.models.mercuryportal.api.quoteQueriesSubmission;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "jobRef",
    "scopeOfWorksId",
    "queryText",
    "id"
})
public class SubmitResponseToSeniorManagerQuery extends modelBase<SubmitResponseToSeniorManagerQuery>  {

    @JsonProperty("jobRef")
    private Integer jobRef;

    @JsonProperty("scopeOfWorksId")
    private Integer scopeOfWorksId;

    @JsonProperty("queryText")
    private String queryText;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("jobRef")
    public Integer getJobRef() {
        return jobRef;
    }

    @JsonProperty("jobRef")
    public void setJobRef(Integer jobRef) {
        this.jobRef = jobRef;
    }

    public SubmitResponseToSeniorManagerQuery withJobRef(Integer jobRef) {
        this.jobRef = jobRef;
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

    public SubmitResponseToSeniorManagerQuery withScopeOfWorksId(Integer scopeOfWorksId) {
        this.scopeOfWorksId = scopeOfWorksId;
        return this;
    }

    @JsonProperty("queryText")
    public String getQueryText() {
        return queryText;
    }

    @JsonProperty("queryText")
    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public SubmitResponseToSeniorManagerQuery withQueryText(String queryText) {
        this.queryText = queryText;
        return this;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    public SubmitResponseToSeniorManagerQuery withId(Integer id) {
        this.id = id;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("jobRef", jobRef).append("scopeOfWorksId", scopeOfWorksId).append("queryText", queryText).append("id", id).toString();
    }

}