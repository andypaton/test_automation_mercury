package mercury.api.models.ppm;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import mercury.api.models.modelBase;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ppmId",
    "jobReference",
    "dateTime",
    "resourceId",
    "hoursString",
    "hours",
    "startDateTime",
    "affirmationId",
    "subAffirmationId",
    "note",
    "overTimeHoursString",
    "overtimeHours",
    "travelTimeHoursString",
    "travelTimeHours",
    "assisted",
    "signalReceived",
    "siteId",
    "ppmJobId",
    "quoteRequired",
    "remedialJobRequired",
    "etaDateTime",
    "gasSafetyAdviceNotices",
    "asbestosRegisterChecked"
})
@Component
public class PPMJob extends modelBase<PPMJob>{

    @JsonProperty("ppmId")
    private String ppmId;

    @JsonProperty("jobReference")
    private String jobReference;

    @JsonProperty("dateTime")
    private String dateTime;

    @JsonProperty("resourceId")
    private Integer resourceId;

    @JsonProperty("hoursString")
    private String hoursString;

    @JsonProperty("hours")
    private Integer hours;

    @JsonProperty("startDateTime")
    private String startDateTime;

    @JsonProperty("affirmationId")
    private String affirmationId;

    @JsonProperty("subAffirmationId")
    private String subAffirmationId;

    @JsonProperty("note")
    private String note;

    @JsonProperty("overTimeHoursString")
    private String overTimeHoursString;

    @JsonProperty("overtimeHours")
    private Integer overtimeHours;

    @JsonProperty("travelTimeHoursString")
    private String travelTimeHoursString;

    @JsonProperty("travelTimeHours")
    private double travelTimeHours;

    @JsonProperty("assisted")
    private Boolean assisted;

    @JsonProperty("signalReceived")
    private Boolean signalReceived;

    @JsonProperty("siteId")
    private Integer siteId;

    @JsonProperty("ppmJobId")
    private String ppmJobId;

    @JsonProperty("quoteRequired")
    private Boolean quoteRequired;

    @JsonProperty("remedialJobRequired")
    private Boolean remedialJobRequired;

    @JsonProperty("etaDateTime")
    private String etaDateTime;

    @JsonProperty("gasSafetyAdviceNotices")
    private List<Object> gasSafetyAdviceNotices = null;

    @JsonProperty("asbestosRegisterChecked")
    private Object asbestosRegisterChecked;

    @JsonProperty("ppmTypeName")
    private String ppmTypeName;

    @JsonProperty("PPMScheduleRefs")
    private List<Integer> PPMScheduleRefs;

    @JsonProperty("ppmId")
    public String getPpmId() {
        return ppmId;
    }

    @JsonProperty("ppmId")
    public void setPpmId(String ppmId) {
        this.ppmId = ppmId;
    }

    public PPMJob withPpmId(String ppmId) {
        this.ppmId = ppmId;
        return this;
    }

    @JsonProperty("jobReference")
    public String getJobReference() {
        return jobReference;
    }

    @JsonProperty("jobReference")
    public void setJobReference(String jobReference) {
        this.jobReference = jobReference;
    }

    public PPMJob withJobReference(String jobReference) {
        this.jobReference = jobReference;
        return this;
    }

    @JsonProperty("dateTime")
    public String getDateTime() {
        return dateTime;
    }

    @JsonProperty("dateTime")
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public PPMJob withDateTime(String dateTime) {
        this.dateTime = dateTime;
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

    public PPMJob withResourceId(Integer resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    @JsonProperty("hoursString")
    public String getHoursString() {
        return hoursString;
    }

    @JsonProperty("hoursString")
    public void setHoursString(String hoursString) {
        this.hoursString = hoursString;
    }

    public PPMJob withHoursString(String hoursString) {
        this.hoursString = hoursString;
        return this;
    }

    @JsonProperty("hours")
    public Integer getHours() {
        return hours;
    }

    @JsonProperty("hours")
    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public PPMJob withHours(Integer hours) {
        this.hours = hours;
        return this;
    }

    @JsonProperty("startDateTime")
    public String getStartDateTime() {
        return startDateTime;
    }

    @JsonProperty("startDateTime")
    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public PPMJob withStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
        return this;
    }

    @JsonProperty("affirmationId")
    public String getAffirmationId() {
        return affirmationId;
    }

    @JsonProperty("affirmationId")
    public void setAffirmationId(String affirmationId) {
        this.affirmationId = affirmationId;
    }

    public PPMJob withAffirmationId(String affirmationId) {
        this.affirmationId = affirmationId;
        return this;
    }

    @JsonProperty("subAffirmationId")
    public String getSubAffirmationId() {
        return subAffirmationId;
    }

    @JsonProperty("subAffirmationId")
    public void setSubAffirmationId(String subAffirmationId) {
        this.subAffirmationId = subAffirmationId;
    }

    public PPMJob withSubAffirmationId(String subAffirmationId) {
        this.subAffirmationId = subAffirmationId;
        return this;
    }

    @JsonProperty("note")
    public String getNote() {
        return note;
    }

    @JsonProperty("note")
    public void setNote(String note) {
        this.note = note;
    }

    public PPMJob withNote(String note) {
        this.note = note;
        return this;
    }

    @JsonProperty("overTimeHoursString")
    public String getOverTimeHoursString() {
        return overTimeHoursString;
    }

    @JsonProperty("overTimeHoursString")
    public void setOverTimeHoursString(String overTimeHoursString) {
        this.overTimeHoursString = overTimeHoursString;
    }

    public PPMJob withOverTimeHoursString(String overTimeHoursString) {
        this.overTimeHoursString = overTimeHoursString;
        return this;
    }

    @JsonProperty("overtimeHours")
    public Integer getOvertimeHours() {
        return overtimeHours;
    }

    @JsonProperty("overtimeHours")
    public void setOvertimeHours(Integer overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public PPMJob withOvertimeHours(Integer overtimeHours) {
        this.overtimeHours = overtimeHours;
        return this;
    }

    @JsonProperty("travelTimeHoursString")
    public String getTravelTimeHoursString() {
        return travelTimeHoursString;
    }

    @JsonProperty("travelTimeHoursString")
    public void setTravelTimeHoursString(String travelTimeHoursString) {
        this.travelTimeHoursString = travelTimeHoursString;
    }

    public PPMJob withTravelTimeHoursString(String travelTimeHoursString) {
        this.travelTimeHoursString = travelTimeHoursString;
        return this;
    }

    @JsonProperty("travelTimeHours")
    public double getTravelTimeHours() {
        return travelTimeHours;
    }

    @JsonProperty("travelTimeHours")
    public void setTravelTimeHours(double travelTimeHours) {
        this.travelTimeHours = travelTimeHours;
    }

    public PPMJob withTravelTimeHours(double travelTimeHours) {
        this.travelTimeHours = travelTimeHours;
        return this;
    }

    @JsonProperty("assisted")
    public Boolean getAssisted() {
        return assisted;
    }

    @JsonProperty("assisted")
    public void setAssisted(Boolean assisted) {
        this.assisted = assisted;
    }

    public PPMJob withAssisted(Boolean assisted) {
        this.assisted = assisted;
        return this;
    }

    @JsonProperty("signalReceived")
    public Boolean getSignalReceived() {
        return signalReceived;
    }

    @JsonProperty("signalReceived")
    public void setSignalReceived(Boolean signalReceived) {
        this.signalReceived = signalReceived;
    }

    public PPMJob withSignalReceived(Boolean signalReceived) {
        this.signalReceived = signalReceived;
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

    public PPMJob withSiteId(Integer siteId) {
        this.siteId = siteId;
        return this;
    }

    @JsonProperty("ppmJobId")
    public String getPpmJobId() {
        return ppmJobId;
    }

    @JsonProperty("ppmJobId")
    public void setPpmJobId(String ppmJobId) {
        this.ppmJobId = ppmJobId;
    }

    public PPMJob withPpmJobId(String ppmJobId) {
        this.ppmJobId = ppmJobId;
        return this;
    }

    @JsonProperty("quoteRequired")
    public Boolean getQuoteRequired() {
        return quoteRequired;
    }

    @JsonProperty("quoteRequired")
    public void setQuoteRequired(Boolean quoteRequired) {
        this.quoteRequired = quoteRequired;
    }

    public PPMJob withQuoteRequired(Boolean quoteRequired) {
        this.quoteRequired = quoteRequired;
        return this;
    }

    @JsonProperty("remedialJobRequired")
    public Boolean getRemedialJobRequired() {
        return remedialJobRequired;
    }

    @JsonProperty("remedialJobRequired")
    public void setRemedialJobRequired(Boolean remedialJobRequired) {
        this.remedialJobRequired = remedialJobRequired;
    }

    public PPMJob withRemedialJobRequired(Boolean remedialJobRequired) {
        this.remedialJobRequired = remedialJobRequired;
        return this;
    }

    @JsonProperty("etaDateTime")
    public String getEtaDateTime() {
        return etaDateTime;
    }

    @JsonProperty("etaDateTime")
    public void setEtaDateTime(String etaDateTime) {
        this.etaDateTime = etaDateTime;
    }

    public PPMJob withEtaDateTime(String etaDateTime) {
        this.etaDateTime = etaDateTime;
        return this;
    }

    @JsonProperty("gasSafetyAdviceNotices")
    public List<Object> getGasSafetyAdviceNotices() {
        return gasSafetyAdviceNotices;
    }

    @JsonProperty("gasSafetyAdviceNotices")
    public void setGasSafetyAdviceNotices(List<Object> gasSafetyAdviceNotices) {
        this.gasSafetyAdviceNotices = gasSafetyAdviceNotices;
    }

    public PPMJob withGasSafetyAdviceNotices(List<Object> gasSafetyAdviceNotices) {
        this.gasSafetyAdviceNotices = gasSafetyAdviceNotices;
        return this;
    }

    @JsonProperty("asbestosRegisterChecked")
    public Object getAsbestosRegisterChecked() {
        return asbestosRegisterChecked;
    }

    @JsonProperty("asbestosRegisterChecked")
    public void setAsbestosRegisterChecked(Object asbestosRegisterChecked) {
        this.asbestosRegisterChecked = asbestosRegisterChecked;
    }

    public PPMJob withAsbestosRegisterChecked(Object asbestosRegisterChecked) {
        this.asbestosRegisterChecked = asbestosRegisterChecked;
        return this;
    }

    @JsonProperty("ppmTypeName")
    public String getPPMTypeName() {
        return ppmTypeName;
    }

    @JsonProperty("ppmTypeName")
    public void setPPMTypeName(String ppmTypeName) {
        this.ppmTypeName = ppmTypeName;
    }

    @JsonProperty("PPMScheduleRefs")
    public List<Integer> getPPMScheduleRefs() {
        return PPMScheduleRefs;
    }

    @JsonProperty("PPMScheduleRefs")
    public void setPPMScheduleRefs(List<Integer> pPMScheduleRefs) {
        this.PPMScheduleRefs = pPMScheduleRefs;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("ppmId", ppmId)
                .append("jobReference", jobReference)
                .append("dateTime", dateTime)
                .append("resourceId", resourceId)
                .append("hoursString", hoursString)
                .append("hours", hours)
                .append("startDateTime", startDateTime)
                .append("affirmationId", affirmationId)
                .append("subAffirmationId", subAffirmationId)
                .append("note", note)
                .append("overTimeHoursString", overTimeHoursString)
                .append("overtimeHours", overtimeHours)
                .append("travelTimeHoursString", travelTimeHoursString)
                .append("travelTimeHours", travelTimeHours)
                .append("assisted", assisted)
                .append("signalReceived", signalReceived)
                .append("siteId", siteId)
                .append("ppmJobId", ppmJobId)
                .append("quoteRequired", quoteRequired)
                .append("remedialJobRequired", remedialJobRequired)
                .append("etaDateTime", etaDateTime)
                .append("gasSafetyAdviceNotices", gasSafetyAdviceNotices)
                .append("asbestosRegisterChecked", asbestosRegisterChecked)
                .append("ppmTypeName", ppmTypeName)
                .append("PPMScheduleRefs", PPMScheduleRefs)
                .toString();
    }
}