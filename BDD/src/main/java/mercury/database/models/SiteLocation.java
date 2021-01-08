package mercury.database.models;

import javax.persistence.Column;
import javax.persistence.Id;

public class SiteLocation {

	
	@Id
	@Column(name = "Id")	
	private Integer id;
	
	@Column(name = "SiteCode")
	private String siteCode;
	
	@Column(name = "LocationId")
	private Integer locationId;
	
	@Column(name = "LocationName")
	private String locationName;
	
}
