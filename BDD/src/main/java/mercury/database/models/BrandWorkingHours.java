package mercury.database.models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.beans.BeanUtils;



@Entity
@Table(name="BrandWorkingHours")
public class BrandWorkingHours {

	@Id
	@Column(name = "Id")
	private Integer id;
	
	@Column(name = "DayOfTheWeek")
	private Integer dayOfTheWeek;
	
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

	public Integer getDayOfTheWeek() {
		return dayOfTheWeek;
	}

	public void setDayOfTheWeek(Integer dayOfTheWeek) {
		this.dayOfTheWeek = dayOfTheWeek;
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
		dayOfTheWeek = null;
		startAt = null;
		endAt = null;
	}
	
	public void copy(BrandWorkingHours brandWorkingHours) {
		BeanUtils.copyProperties(brandWorkingHours, this);
	}
	
	@Override
	public String toString() {
		return "BrandWorkingHours [id=" + id + ", dayOfTheWeek=" + dayOfTheWeek + ", startAt=" + startAt + ", endAt=" + endAt + "]";
	}

}
