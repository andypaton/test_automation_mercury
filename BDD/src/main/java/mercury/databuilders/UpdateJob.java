package mercury.databuilders;


public class UpdateJob {

	private String jobReference;
	private Boolean remoteJob = false;
	private String travelTime;
	private Boolean operationalOnArrival = false;
	private Boolean operationOnDeparture = false;
	private String workStart;
	private String workEnd;
	private String OverTime;
	private Boolean refrigerantGasUsed = false;
	private String statusOnDeparture;
	private String assetCondition;
	private String rootCauseCategory;
	private String rootCause;
	private String notes;
	private Boolean additionalResourceRequired = false;
	private Boolean requestQuote = false;
	
	public UpdateJob() {		
	}
	
	public String getJobReference() {
		return jobReference;
	}
	public void setJobReference(String jobReference) {
		this.jobReference = jobReference;
	}
	public Boolean getRemoteJob() {
		return remoteJob;
	}
	public void setRemoteJob(Boolean remoteJob) {
		this.remoteJob = remoteJob;
	}
	public String getTravelTime() {
		return travelTime;
	}
	public void setTravelTime(String travelTime) {
		this.travelTime = travelTime;
	}
	public Boolean getOperationalOnArrival() {
		return operationalOnArrival;
	}
	public void setOperationalOnArrival(Boolean operationalOnArrival) {
		this.operationalOnArrival = operationalOnArrival;
	}
	public Boolean getOperationOnDeparture() {
		return operationOnDeparture;
	}
	public void setOperationOnDeparture(Boolean operationOnDeparture) {
		this.operationOnDeparture = operationOnDeparture;
	}
	public String getWorkStart() {
		return workStart;
	}
	public void setWorkStart(String workStart) {
		this.workStart = workStart;
	}
	public String getWorkEnd() {
		return workEnd;
	}
	public void setWorkEnd(String workEnd) {
		this.workEnd = workEnd;
	}
	public String getOverTime() {
		return OverTime;
	}
	public void setOverTime(String overTime) {
		OverTime = overTime;
	}
	public Boolean getRefrigerantGasUsed() {
		return refrigerantGasUsed;
	}
	public void setRefrigerantGasUsed(Boolean refrigerantGasUsed) {
		this.refrigerantGasUsed = refrigerantGasUsed;
	}
	public String getStatusOnDeparture() {
		return statusOnDeparture;
	}
	public void setStatusOnDeparture(String statusOnDeparture) {
		this.statusOnDeparture = statusOnDeparture;
	}
	public String getAssetCondition() {
		return assetCondition;
	}
	public void setAssetCondition(String assetCondition) {
		this.assetCondition = assetCondition;
	}
	public String getRootCauseCategory() {
		return rootCauseCategory;
	}
	public void setRootCauseCategory(String rootCauseCategory) {
		this.rootCauseCategory = rootCauseCategory;
	}
	public String getRootCause() {
		return rootCause;
	}
	public void setRootCause(String rootCause) {
		this.rootCause = rootCause;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public Boolean getAdditionalResourceRequired() {
		return additionalResourceRequired;
	}
	public void setAdditionalResourceRequired(Boolean additionalResourceRequired) {
		this.additionalResourceRequired = additionalResourceRequired;
	}
	public Boolean getRequestQuote() {
		return requestQuote;
	}
	public void setRequestQuote(Boolean requestQuote) {
		this.requestQuote = requestQuote;
	}
	
	
	
	
}
