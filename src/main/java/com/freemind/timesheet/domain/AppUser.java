package com.freemind.timesheet.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AppUser.
 */
@Entity
@Table(name = "app_user", schema = "public")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AppUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "internal_user_id", unique = true, nullable = false)
    private Long id;

    @Column(name = "phone")
    private String phone;

    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    private User internalUser;

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(
        name = "app_user_job",
        joinColumns = @JoinColumn(name = "internal_user_id"),
        inverseJoinColumns = @JoinColumn(name = "job_id", referencedColumnName = "id")
    )
    private Set<Job> jobs = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = "appUsers", allowSetters = true)
    private Company company;

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Performance> performances = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public AppUser phone(String phone) {
        this.phone = phone;
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public User getInternalUser() {
        return internalUser;
    }

    public AppUser internalUser(User user) {
        this.internalUser = user;
        return this;
    }

    public void setInternalUser(User user) {
        this.internalUser = user;
    }

    public Set<Job> getJobs() {
        return jobs;
    }

    public AppUser jobs(Set<Job> jobs) {
        this.jobs = jobs;
        return this;
    }

    public AppUser addJob(Job job) {
        this.jobs.add(job);
        job.getAppUsers().add(this);
        return this;
    }

    public AppUser removeJob(Job job) {
        this.jobs.remove(job);
        job.getAppUsers().remove(this);
        return this;
    }

    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }

    public Company getCompany() {
        return company;
    }

    public AppUser company(Company company) {
        this.company = company;
        return this;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Set<Performance> getPerformances() {
        return this.performances;
    }

    public AppUser performances(Set<Performance> performances) {
        this.performances = performances;
        return this;
    }

    public AppUser addPerformance(Performance performance) {
        this.performances.add(performance);
        performance.setAppUser(this);
        return this;
    }

    public AppUser removePerformance(Performance performance) {
        this.performances.remove(performance);
        performance.setAppUser(null);
        return this;
    }

    public void setPerformances(Set<Performance> performances) {
        this.performances = performances;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppUser)) {
            return false;
        }
        return id != null && id.equals(((AppUser) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return (
            "AppUser{" +
            "id=" +
            getId() +
            ", phone=" +
            getPhone() +
            ", customers= " +
            getPerformances() +
            "'" +
            "Company=" +
            getCompany() +
            "" +
            "Jobs= " +
            getJobs().size() +
            "}"
        );
    }
}
