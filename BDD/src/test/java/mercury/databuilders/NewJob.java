package mercury.databuilders;

import org.springframework.beans.BeanUtils;

public class NewJob {
	
	private int jobReference;
	private String site;
	private CallerContact caller;
	private String location;
	private String mainType;
	private String subtype;
	private String classification;
	private String assetTag;
	private String fault;
	private String description;
	private int jobTypeId;
	private String priority;
	
	
	public int getJobReference() {
		return jobReference;
	}
	public void setJobReference(int jobReference) {
		this.jobReference = jobReference;
	}
	
	public void setJobReference(String jobReference) {
		this.jobReference = Integer.valueOf(jobReference);
	}
	
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public CallerContact getCaller() {
		return caller;
	}
	public void setCaller(CallerContact caller) {
		if (this.caller == null) {
			this.caller = new CallerContact();
		}
		BeanUtils.copyProperties(caller, this.caller);
	}
	public void setCaller(String name) {
		if (caller == null) {
			caller = new CallerContact();
		}
		caller.setName(name);
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getMaintype() {
		return mainType;
	}
	public void setMaintype(String mainType) {
		this.mainType = mainType;
	}
	public String getClassification() {
		return classification;
	}
	public void setClassification(String classification) {
		this.classification = classification;
	}
	public String getSubtype() {
		return subtype;
	}
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
	public String getAssetTag() {
		return assetTag;
	}
	public void setAssetTag(String assetTag) {
		this.assetTag = assetTag;
	}
	public String getFault() {
		return fault;
	}
	public void setFault(String fault) {
		this.fault = fault;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setJobTypeId(int jobTypeId) {
	    this.jobTypeId = jobTypeId;
    }	

    public boolean isQuoteRequested() {
	    return jobTypeId == 2;
    }
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}


	public static class Builder {
		private int jobReference;
	
		
		public Builder(String userProfile) {
			this.jobReference = 10001032;
		}
		
		public NewJob build() {
			return new NewJob(this);
		}

	}
	
	private NewJob(Builder builder){
		jobReference = builder.jobReference;
	}
	public NewJob() {
		// TODO Auto-generated constructor stub
	}

}