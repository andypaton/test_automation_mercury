package mercury.databuilders;

public class NewIncident {

	private String incidentType;
	private String siteClosed;
	private String siteReopened;
	private String departmentclosed;
	private String departmentReopened;
	private String description;
	private String caller;

	public String getCaller() {
		return caller;
	}
	public void setCaller(String caller) {
		this.caller = caller;
	}
	public String getIncidentType() {
		return incidentType;
	}
	public void setIncidentType(String incidentType) {
		this.incidentType = incidentType;
	}
	public String getSiteClosed() {
		return siteClosed;
	}
	public void setSiteClosed(String siteClosed) {
		this.siteClosed = siteClosed;
	}
	public String getSiteReopened() {
		return siteReopened;
	}
	public void setSiteReopened(String siteReopened) {
		this.siteReopened = siteReopened;
	}
	public String getDepartmentclosed() {
		return departmentclosed;
	}
	public void setDepartmentclosed(String departmentclosed) {
		this.departmentclosed = departmentclosed;
	}
	public String getDepartmentReopened() {
		return departmentReopened;
	}
	public void setDepartmentReopened(String departmentReopened) {
		this.departmentReopened = departmentReopened;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public NewIncident() {

	}


}
