package com.freemind.timesheet.service.dto;

import com.freemind.timesheet.domain.AppUser;
import com.freemind.timesheet.domain.enumeration.Status;
import com.freemind.timesheet.web.rest.vm.ManagedUserVM;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.freemind.timesheet.domain.Job} entity.
 */
public class JobDTO implements Serializable {
    private Long id;

    @NotNull
    @Size(min = 3)
    private String name;

    private Set<AppUserDTO> appUsers = new HashSet<>();

    private Set<PerformanceDTO> performances = new HashSet<>();

    @Size(min = 20)
    private String description;

    private Status status;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean enable;

    private Long projectId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean isEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Set<PerformanceDTO> getPerformances() {
        return performances;
    }

    public void setPerformances(Set<PerformanceDTO> performances) {
        this.performances = performances;
    }

    public Set<AppUserDTO> getAppUsers() {
        return this.appUsers;
    }

    public void setAppUsers(Set<AppUserDTO> appUsers) {
        this.appUsers = appUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JobDTO)) {
            return false;
        }

        return id != null && id.equals(((JobDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JobDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", enable='" + isEnable() + "'" +
            ", projectId= " + getProjectId() +
            ", appUsers= "+getAppUsers()+
            ", performances= "+ getPerformances()+
            "}";
    }
}
