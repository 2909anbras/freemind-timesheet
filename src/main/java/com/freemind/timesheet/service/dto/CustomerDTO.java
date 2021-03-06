package com.freemind.timesheet.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.freemind.timesheet.domain.Customer} entity.
 */
public class CustomerDTO implements Serializable {
    private Long id;

    @NotNull
    @Size(min = 3)
    private String name;

    @NotNull
    private Boolean enable;

    private Set<ProjectDTO> projects = new HashSet<>();

    private String companyName;

    private Long companyId;

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

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Boolean isEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Set<ProjectDTO> getProjects() {
        return this.projects;
    }

    public void setProjects(Set<ProjectDTO> projects) {
        this.projects = projects;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long id) {
        this.companyId = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerDTO)) {
            return false;
        }

        return id != null && id.equals(((CustomerDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", enable='" + isEnable() + "'" +
            ", projects= "+getProjects()+" "+
            ", companyId='" + getCompanyId() + "'" +
            "}";
    }
}
