package com.freemind.timesheet.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.freemind.timesheet.domain.Performance} entity.
 */
public class PerformanceDTO implements Serializable {
    private Long id;

    @NotNull
    @Min(value = 0)
    @Max(value = 16)
    private Integer hours;

    @NotNull
    private LocalDate date;

    private Long appUserId;

    private Long jobId;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(Long appUserId) {
        this.appUserId = appUserId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PerformanceDTO)) {
            return false;
        }

        return id != null && id.equals(((PerformanceDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PerformanceDTO{" +
            "id=" + getId() +
            ", hours=" + getHours() +
            ", date='" + getDate() + "'" +
            ", appUserId=" + getAppUserId() +
            ", jobId=" + getJobId() +
            ", description="+getDescription()+
            "}";
    }
}
