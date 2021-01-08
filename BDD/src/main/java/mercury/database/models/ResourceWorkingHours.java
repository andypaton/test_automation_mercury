package mercury.database.models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.beans.BeanUtils;



@Entity
@Table(name="ResourceWorkingHours")
public class ResourceWorkingHours {

	@Id
	@Column(name = "Id")
	private Integer id;

	@Column(name = "ResourceId")
	private Integer resourceId;
	
	@Column(name = "StartDayOfTheWeek")
	private Integer startDayOfTheWeek;
	
	@Column(name = "EndDayOfTheWeek")
	private Integer endDayOfTheWeek;
	
	@Column(name = "StartAt")
	private String startAt;
	
	@Column(name = "EndAt")
	private String endAt;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getResourceId() {
		return resourceId;
	}

	public void setResourceId(Integer resourceId) {
		this.resourceId = resourceId;
	}

	public Integer getStartDayOfTheWeek() {
		return startDayOfTheWeek;
	}

	public void setStartDayOfTheWeek(Integer startDayOfTheWeek) {
		this.startDayOfTheWeek = startDayOfTheWeek;
	}
	
	public Integer getEndDayOfTheWeek() {
		return endDayOfTheWeek;
	}

	public void setEndDayOfTheWeek(Integer endDayOfTheWeek) {
		this.endDayOfTheWeek = endDayOfTheWeek;
	}

	public String getStartAt() {
		return startAt;
	}

	public void setStartAt(String startAt) {
		this.startAt = startAt;
	}

	public String getEndAt() {
		return endAt;
	}

	public void setEndAt(String endAt) {
		this.endAt = endAt;
	}

	public void reset() {
		id = null;
		resourceId = null;
		startDayOfTheWeek = null;
		endDayOfTheWeek = null;
		startAt = null;
		endAt = null;
	}
	
	public void copy(ResourceWorkingHours resourceWorkingHours) {
		BeanUtils.copyProperties(resourceWorkingHours, this);
	}

	@Override
	public String toString() {
		return "ResourceWorkingHours [id=" + id + ", resourceId=" + resourceId + ", startDayOfTheWeek="
				+ startDayOfTheWeek + ", endDayOfTheWeek=" + endDayOfTheWeek + ", startAt=" + startAt + ", endAt="
				+ endAt + "]";
	}

}
