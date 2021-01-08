package mercury.database.models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="CallerDetails")
public class CallerDetails {

	@Id
	@Column(name = "Id")
	private Integer id;
	
	@Column(name = "Name")
	private String name;
	
	@Column(name = "Department")
	private String department;
	
	@Column(name = "PhoneNumber")
	private String phoneNumber;
	
	@Column(name = "Extension")
	private String extension;
	
	@Column(name = "CallerType")
	private String callerType;
	
	@Column(name = "SiteName")
	private String siteName;
	
	@Column(name = "JobRole")
	private String jobRole;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getCallerType() {
		return callerType;
	}

	public void setCallerType(String callerType) {
		this.callerType = callerType;
	}

	public String getSiteName() {
		return siteName;
	}
	
	public String getJobRole() {
		return jobRole;
	}

}
