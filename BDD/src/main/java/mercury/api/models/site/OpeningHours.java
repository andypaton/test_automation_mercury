package mercury.api.models.site;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday", "holidays", "openingHour_Monday", "openingHour_Tuesday", "openingHour_Wednesday",
        "openingHour_Thursday", "openingHour_Friday", "openingHour_Saturday", "openingHour_Sunday", "openingHour_Holidays" })
public class OpeningHours {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("monday")
    private Integer monday;
    @JsonProperty("tuesday")
    private Integer tuesday;
    @JsonProperty("wednesday")
    private Integer wednesday;
    @JsonProperty("thursday")
    private Integer thursday;
    @JsonProperty("friday")
    private Integer friday;
    @JsonProperty("saturday")
    private Integer saturday;
    @JsonProperty("sunday")
    private Integer sunday;
    @JsonProperty("holidays")
    private Integer holidays;
    @JsonProperty("openingHour_Monday")
    private OpeningHour openingHourMonday;
    @JsonProperty("openingHour_Tuesday")
    private OpeningHour openingHourTuesday;
    @JsonProperty("openingHour_Wednesday")
    private OpeningHour openingHourWednesday;
    @JsonProperty("openingHour_Thursday")
    private OpeningHour openingHourThursday;
    @JsonProperty("openingHour_Friday")
    private OpeningHour openingHourFriday;
    @JsonProperty("openingHour_Saturday")
    private OpeningHour openingHourSaturday;
    @JsonProperty("openingHour_Sunday")
    private OpeningHour openingHourSunday;
    @JsonProperty("openingHour_Holidays")
    private OpeningHour openingHourHolidays;


    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("monday")
    public Integer getMonday() {
        return monday;
    }

    @JsonProperty("monday")
    public void setMonday(Integer monday) {
        this.monday = monday;
    }

    @JsonProperty("tuesday")
    public Integer getTuesday() {
        return tuesday;
    }

    @JsonProperty("tuesday")
    public void setTuesday(Integer tuesday) {
        this.tuesday = tuesday;
    }

    @JsonProperty("wednesday")
    public Integer getWednesday() {
        return wednesday;
    }

    @JsonProperty("wednesday")
    public void setWednesday(Integer wednesday) {
        this.wednesday = wednesday;
    }

    @JsonProperty("thursday")
    public Integer getThursday() {
        return thursday;
    }

    @JsonProperty("thursday")
    public void setThursday(Integer thursday) {
        this.thursday = thursday;
    }

    @JsonProperty("friday")
    public Integer getFriday() {
        return friday;
    }

    @JsonProperty("friday")
    public void setFriday(Integer friday) {
        this.friday = friday;
    }

    @JsonProperty("saturday")
    public Integer getSaturday() {
        return saturday;
    }

    @JsonProperty("saturday")
    public void setSaturday(Integer saturday) {
        this.saturday = saturday;
    }

    @JsonProperty("sunday")
    public Integer getSunday() {
        return sunday;
    }

    @JsonProperty("sunday")
    public void setSunday(Integer sunday) {
        this.sunday = sunday;
    }

    @JsonProperty("holidays")
    public Integer getHolidays() {
        return holidays;
    }

    @JsonProperty("holidays")
    public void setHolidays(Integer holidays) {
        this.holidays = holidays;
    }

    @JsonProperty("openingHour_Monday")
    public OpeningHour getOpeningHourMonday() {
        return openingHourMonday;
    }

    @JsonProperty("openingHour_Monday")
    public void setOpeningHourMonday(OpeningHour openingHourMonday) {
        this.openingHourMonday = openingHourMonday;
    }

    @JsonProperty("openingHour_Tuesday")
    public OpeningHour getOpeningHourTuesday() {
        return openingHourTuesday;
    }

    @JsonProperty("openingHour_Tuesday")
    public void setOpeningHourTuesday(OpeningHour openingHourTuesday) {
        this.openingHourTuesday = openingHourTuesday;
    }

    @JsonProperty("openingHour_Wednesday")
    public OpeningHour getOpeningHourWednesday() {
        return openingHourWednesday;
    }

    @JsonProperty("openingHour_Wednesday")
    public void setOpeningHourWednesday(OpeningHour openingHourWednesday) {
        this.openingHourWednesday = openingHourWednesday;
    }

    @JsonProperty("openingHour_Thursday")
    public OpeningHour getOpeningHourThursday() {
        return openingHourThursday;
    }

    @JsonProperty("openingHour_Thursday")
    public void setOpeningHourThursday(OpeningHour openingHourThursday) {
        this.openingHourThursday = openingHourThursday;
    }

    @JsonProperty("openingHour_Friday")
    public OpeningHour getOpeningHourFriday() {
        return openingHourFriday;
    }

    @JsonProperty("openingHour_Friday")
    public void setOpeningHourFriday(OpeningHour openingHourFriday) {
        this.openingHourFriday = openingHourFriday;
    }

    @JsonProperty("openingHour_Saturday")
    public OpeningHour getOpeningHourSaturday() {
        return openingHourSaturday;
    }

    @JsonProperty("openingHour_Saturday")
    public void setOpeningHourSaturday(OpeningHour openingHourSaturday) {
        this.openingHourSaturday = openingHourSaturday;
    }

    @JsonProperty("openingHour_Sunday")
    public OpeningHour getOpeningHourSunday() {
        return openingHourSunday;
    }

    @JsonProperty("openingHour_Sunday")
    public void setOpeningHourSunday(OpeningHour openingHourSunday) {
        this.openingHourSunday = openingHourSunday;
    }

    @JsonProperty("openingHour_Holidays")
    public OpeningHour getOpeningHourHolidays() {
        return openingHourHolidays;
    }

    @JsonProperty("openingHour_Holidays")
    public void setOpeningHourHolidays(OpeningHour openingHourHolidays) {
        this.openingHourHolidays = openingHourHolidays;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("monday", monday).append("tuesday", tuesday).append("wednesday", wednesday).append("thursday", thursday).append("friday", friday)
                .append("saturday", saturday).append("sunday", sunday).append("holidays", holidays).append("openingHourMonday", openingHourMonday).append("openingHourTuesday", openingHourTuesday)
                .append("openingHourWednesday", openingHourWednesday).append("openingHourThursday", openingHourThursday).append("openingHourFriday", openingHourFriday)
                .append("openingHourSaturday", openingHourSaturday).append("openingHourSunday", openingHourSunday).append("openingHourHolidays", openingHourHolidays)
                .toString();
    }

}