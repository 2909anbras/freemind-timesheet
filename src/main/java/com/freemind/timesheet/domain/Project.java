package com.freemind.timesheet.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.freemind.timesheet.web.rest.ReportRessource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Project.
 */
@Entity
@Table(name = "project", schema = "public")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Project implements Serializable {
    private static Logger log = LoggerFactory.getLogger(Project.class);

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(min = 3)
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "enable", nullable = false)
    private Boolean enable;

    @OneToMany(mappedBy = "project", orphanRemoval = true, fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Job> jobs = new HashSet<>();

    @ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
    //    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnoreProperties(value = "projects", allowSetters = true)
    private Customer customer;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Project name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isEnable() {
        return enable;
    }

    public Project enable(Boolean enable) {
        this.enable = enable;
        return this;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Set<Job> getJobs() {
        return jobs;
    }

    public Project jobs(Set<Job> jobs) {
        this.jobs = jobs;
        return this;
    }

    public Project addJob(Job job) {
        this.jobs.add(job);
        job.setProject(this);
        return this;
    }

    public Project removeJob(Job job) {
        this.jobs.remove(job);
        job.setProject(null);
        return this;
    }

    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Project customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Job> getJobsByUser(AppUser user) {
        List<Job> jobs = new ArrayList<Job>();
        jobs = this.jobs.stream().filter(j -> j.getAppUsers().contains(user)).collect(Collectors.toList());
        return jobs;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Project)) {
            return false;
        }
        return id != null && id.equals(((Project) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public boolean canDelete() {
        return this.jobs.stream().anyMatch(j -> j.getPerformances().size() == 0);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Project{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", enable='" + isEnable() + "'" +
            "}";
    }
}
