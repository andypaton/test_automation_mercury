package mercury.database.models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.beans.BeanUtils;


@Entity
@Table(name="Site")
public class SiteView {

	@Id
	@Column(name = "Id")	
	private Integer id;
	
	@Column(name = "Name")
	private String name;
		
	@Column(name = "SiteCode")
	private String siteCode;
	
	@Column(name = "Address1")
	private String address1;
	
	@Column(name = "Address2")
	private String address2;
	
	@Column(name = "Address3")
	private String address3;
	
	@Column(name = "Town")
	private String town;
	
	@Column(name = "County")
	private String county;
	
	@Column(name = "Postcode")
	private String postcode;
	
	@Column(name = "TelNo")
	private String telNo;
	
	@Column(name = "SiteStatusId")
	private Integer siteStatusId;
	
	@Column(name = "SiteTypeId")
	private Integer siteTypeId;
	
	@Column(name = "SiteType")
	private String siteType;
	
	@Column(name = "IsVIPStore")
	private int isVIPStore;

	@Column(name = "SiteStatus")
	private String siteStatus;

	@Column(name = "PhoneNumber")
	private String phoneNumber;
	
	@Column(name = "BrandId")
	private Integer brandId;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getTelNo() {
		return telNo;
	}

	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}	
	
	public Integer getSiteStatusId() {
		return siteStatusId;
	}

	public void setSiteStatusId(Integer siteStatusId) {
		this.siteStatusId = siteStatusId;
	}
	
	public Integer getSiteTypeId() {
		return siteTypeId;
	}
	
	public void setSiteTypeId(Integer siteTypeId) {
		this.siteTypeId = siteTypeId;
	}
	
	public String getSiteType() {
		return siteType;
	}
	
	public void setSiteType(String siteType) {
		this.siteType = siteType;
	}
	
	public Boolean isVIPStore() {
		return isVIPStore == 1;
	}

	public int getIsVIPStore() {
		return isVIPStore;
	}
	
	public void setIsVIPStore(int isVIPStore) {
		this.isVIPStore = isVIPStore;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getSiteStatus() {
		return siteStatus;
	}
	
	public void setSiteStatus(String siteStatus) {
		this.siteStatus = siteStatus;
	}
	
	public void copy(SiteView site) {
		BeanUtils.copyProperties(site, this);
	}
}
