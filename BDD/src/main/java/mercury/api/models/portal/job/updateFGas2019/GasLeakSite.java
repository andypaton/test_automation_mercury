package mercury.api.models.portal.job.updateFGas2019;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "precedingGasLeakSiteCheckId",
    "precedingGasLeakSiteCheckUuid",
    "gasLeakLocationId",
    "primaryComponentInformation",
    "gasLeakSubLocationId",
    "gasLeakSiteStatusId",
    "gasLeakInitialTestId",
    "gasLeakFollowUpTestId"
})
public class GasLeakSite  extends modelBase<GasLeakSite>{
    @JsonProperty("precedingGasLeakSiteCheckId")
    private String precedingGasLeakSiteCheckId;

    @JsonProperty("precedingGasLeakSiteCheckUuid")
    private String precedingGasLeakSiteCheckUuid;

    @JsonProperty("gasLeakLocationId")
    private String gasLeakLocationId;

    @JsonProperty("primaryComponentInformation")
    private String primaryComponentInformation;

    @JsonProperty("gasLeakSubLocationId")
    private String gasLeakSubLocationId;

    @JsonProperty("gasLeakSiteStatusId")
    private String gasLeakSiteStatusId;

    @JsonProperty("gasLeakInitialTestId")
    private String gasLeakInitialTestId;

    @JsonProperty("gasLeakFollowUpTestId")
    private String gasLeakFollowUpTestId;

    @JsonProperty("precedingGasLeakSiteCheckId")
    public String getPrecedingGasLeakSiteCheckId() {
        return precedingGasLeakSiteCheckId;
    }

    @JsonProperty("precedingGasLeakSiteCheckId")
    public void setPrecedingGasLeakSiteCheckId(String precedingGasLeakSiteCheckId) {
        this.precedingGasLeakSiteCheckId = precedingGasLeakSiteCheckId;
    }

    @JsonProperty("precedingGasLeakSiteCheckUuid")
    public String getPrecedingGasLeakSiteCheckUuid() {
        return precedingGasLeakSiteCheckUuid;
    }

    @JsonProperty("precedingGasLeakSiteCheckUuid")
    public void setPrecedingGasLeakSiteCheckUuid(String precedingGasLeakSiteCheckUuid) {
        this.precedingGasLeakSiteCheckUuid = precedingGasLeakSiteCheckUuid;
    }

    @JsonProperty("gasLeakLocationId")
    public String getGasLeakLocationId() {
        return gasLeakLocationId;
    }

    @JsonProperty("gasLeakLocationId")
    public void setGasLeakLocationId(String gasLeakLocationId) {
        this.gasLeakLocationId = gasLeakLocationId;
    }

    @JsonProperty("primaryComponentInformation")
    public String getPrimaryComponentInformation() {
        return primaryComponentInformation;
    }

    @JsonProperty("primaryComponentInformation")
    public void setPrimaryComponentInformation(String primaryComponentInformation) {
        this.primaryComponentInformation = primaryComponentInformation;
    }

    @JsonProperty("gasLeakSubLocationId")
    public String getGasLeakSubLocationId() {
        return gasLeakSubLocationId;
    }

    @JsonProperty("gasLeakSubLocationId")
    public void setGasLeakSubLocationId(String gasLeakSubLocationId) {
        this.gasLeakSubLocationId = gasLeakSubLocationId;
    }

    @JsonProperty("gasLeakSiteStatusId")
    public String getGasLeakSiteStatusId() {
        return gasLeakSiteStatusId;
    }

    @JsonProperty("gasLeakSiteStatusId")
    public void setGasLeakSiteStatusId(String gasLeakSiteStatusId) {
        this.gasLeakSiteStatusId = gasLeakSiteStatusId;
    }

    @JsonProperty("gasLeakInitialTestId")
    public String getGasLeakInitialTestId() {
        return gasLeakInitialTestId;
    }

    @JsonProperty("gasLeakInitialTestId")
    public void setGasLeakInitialTestId(String gasLeakInitialTestId) {
        this.gasLeakInitialTestId = gasLeakInitialTestId;
    }

    @JsonProperty("gasLeakFollowUpTestId")
    public String getGasLeakFollowUpTestId() {
        return gasLeakFollowUpTestId;
    }

    @JsonProperty("gasLeakFollowUpTestId")
    public void setGasLeakFollowUpTestId(String gasLeakFollowUpTestId) {
        this.gasLeakFollowUpTestId = gasLeakFollowUpTestId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("precedingGasLeakSiteCheckId", precedingGasLeakSiteCheckId).append("precedingGasLeakSiteCheckUuid", precedingGasLeakSiteCheckUuid).append("gasLeakLocationId", gasLeakLocationId).append("primaryComponentInformation", primaryComponentInformation).append("gasLeakSubLocationId", gasLeakSubLocationId).append("gasLeakSiteStatusId", gasLeakSiteStatusId).append("gasLeakInitialTestId", gasLeakInitialTestId).append("gasLeakFollowUpTestId", gasLeakFollowUpTestId).toString();
    }
}
