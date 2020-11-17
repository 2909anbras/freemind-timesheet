package com.freemind.timesheet.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A DTO for the {@link com.freemind.timesheet.domain.AppUser} entity.
 */
public class AppUserDTO implements Serializable {
    
    private Long id;

    private String phone;


    private Long internalUserId;
    private Set<JobDTO> jobs = new HashSet<>();

    private Long companyId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getInternalUserId() {
        return internalUserId;
    }

    public void setInternalUserId(Long userId) {
        this.internalUserId = userId;
    }

    public Set<JobDTO> getJobs() {
        return jobs;
    }

    public void setJobs(Set<JobDTO> jobs) {
        this.jobs = jobs;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppUserDTO)) {
            return false;
        }

        return id != null && id.equals(((AppUserDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppUserDTO{" +
            "id=" + getId() +
            ", phone='" + getPhone() + "'" +
            ", internalUserId=" + getInternalUserId() +
            ", jobs='" + getJobs() + "'" +
            ", companyId=" + getCompanyId() +
            "}";
    }
}
