package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Incident {
		
	@Id
	@Column(name = "Id")	
	Integer id;
	
	@Column(name = "SiteId")	
	Integer siteId;

	@Column(name = "IncidentStatusId")	
	Integer incidentStatusId;

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSiteId() {
		return siteId;
	}
	
	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public Integer getIncidentStatusId() {
		return incidentStatusId;
	}

	public void setIncidentStatusId(Integer incidentStatusId) {
		this.incidentStatusId = incidentStatusId;
	}
}
