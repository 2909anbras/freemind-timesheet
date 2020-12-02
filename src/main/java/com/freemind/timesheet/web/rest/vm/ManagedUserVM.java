package com.freemind.timesheet.web.rest.vm;

import com.freemind.timesheet.service.dto.JobDTO;
import com.freemind.timesheet.service.dto.PerformanceDTO;
import com.freemind.timesheet.service.dto.UserDTO;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.Size;

/**
 * View Model extending the UserDTO, which is meant to be used in the user management UI.
 */
public class ManagedUserVM extends UserDTO {
    public static final int PASSWORD_MIN_LENGTH = 4;

    public static final int PASSWORD_MAX_LENGTH = 100;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    private Set<JobDTO> jobs = new HashSet<>();

    private Set<PerformanceDTO> performances = new HashSet<>();

    private String phone;

    private Long companyId;

    public ManagedUserVM() {
        // Empty constructor needed for Jackson.
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<JobDTO> getJobs() {
        return jobs;
    }

    public void setJobs(Set<JobDTO> jobs) {
        this.jobs = jobs;
    }

    public Set<PerformanceDTO> getPerformances() {
        return performances;
    }

    public void setPerformances(Set<PerformanceDTO> performances) {
        this.performances = performances;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ManagedUserVM{" + super.toString() + 
        		"phone:"+this.phone+", "
        		+ "companyId:"+this.companyId+", "
				+"jobs:"+this.jobs+ "} ";
    }
}
