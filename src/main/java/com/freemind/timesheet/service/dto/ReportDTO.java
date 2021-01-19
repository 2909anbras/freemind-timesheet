package com.freemind.timesheet.service.dto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class ReportDTO {
    private Set<Long> companiesId = new HashSet<>();
    private Set<Long> jobsId = new HashSet<>();
    private Set<Long> customersId = new HashSet<>();
    private Set<Long> usersId = new HashSet<>();
    private Set<LocalDate> dates = new HashSet<>();
    private Set<Long> projectsId = new HashSet<>();

    public ReportDTO() {
        // Empty constructor needed for Jackson.
    }

    public Set<Long> getJobsId() {
        return jobsId;
    }

    public void setJobsId(Set<Long> jobsId) {
        this.jobsId = jobsId;
    }

    public Set<Long> getProjectsId() {
        return projectsId;
    }

    public void setProjectsId(Set<Long> projectsId) {
        this.projectsId = projectsId;
    }

    public Set<Long> getCustomersId() {
        return customersId;
    }

    public void setCustomersId(Set<Long> customersId) {
        this.customersId = customersId;
    }

    public Set<Long> getUsersId() {
        return usersId;
    }

    public void setUsersId(Set<Long> usersId) {
        this.usersId = usersId;
    }

    public Set<Long> getCompaniesId() {
        return companiesId;
    }

    public void setCompaniesId(Set<Long> companiesId) {
        this.companiesId = companiesId;
    }

    public Set<LocalDate> getDates() {
        return dates;
    }

    public void setDate(Set<LocalDate> dates) {
        this.dates = dates;
    }

    @Override
    public String toString() {
        return (
            "ReportDTO{" +
            "usersId=" +
            getUsersId() +
            ", companiesId='" +
            getCompaniesId() +
            "'" +
            ", customersId='" +
            getCustomersId() +
            "'" +
            ", projectsId='" +
            getProjectsId() +
            "'" +
            ", jobsId=" +
            getJobsId() +
            ", dates=" +
            getDates() +
            "}"
        );
    }
}
