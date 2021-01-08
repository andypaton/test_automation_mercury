package mercury.databuilders;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class User {

	private String username;
	private String password;
	private Integer resourceId; 
	private String profileName;
	private String jobStatus;

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getResourceId() {
		return resourceId;
	}
	public void setResourceId(Integer resourceId) {
		this.resourceId = resourceId;
	}
	
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	
	public String getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}
	
	public static class Builder {
		private String username;
		private String password;
		private Integer resourceId;
		private String profileName;
		private String jobStatus;

		public Builder(String userProfile) {

			switch(userProfile) {
			case "Portal":
				this.username = "C6012";
				this.password = "Password1";
				this.resourceId = 3042;
				break;
			case "Helpdesk":
				this.username = "Andrew.Paton";
				this.password = "Password1";
				this.resourceId = 3160;
			}
		}
		
		public Builder withProfile(String profileName) {
			this.profileName = profileName;
			return this;
		}
		
		public Builder withJobStatus(String jobStatus) {
			this.jobStatus = jobStatus;
			return this;
		}
		
		public Builder withUserName(String userName) {
			this.username = userName;
			return this;
		}
		
		public Builder withPassword(String password) {
			this.password = password;
			return this;
		}
		
		public Builder withResourceId(Integer resourceId) {
			this.resourceId = resourceId;
			return this;
		}
		
		public User build() {
			return new User(this);
		}

	}	

	private User(Builder builder){
		username = builder.username;
		password = builder.password;
		resourceId = builder.resourceId;
		profileName = builder.profileName;
		jobStatus = builder.jobStatus;
	}

	public User(String username, String password) {
		this.username = username;
		this.password = password;	
	}
	
	public User() {
	}
	
	public void copy(User user) {		
		BeanUtils.copyProperties(user, this);		
	}
}
