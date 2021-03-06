package com.freemind.timesheet.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.freemind.timesheet.domain.enumeration.Status;
import com.freemind.timesheet.service.JobService;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Job.
 */
@Entity
@Table(name = "job", schema = "public")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Job implements Serializable {
    private static Logger log = LoggerFactory.getLogger(Job.class);

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(min = 3)
    @Column(name = "name", nullable = false)
    private String name;

    @Size(min = 20)
    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "enable")
    private Boolean enable;

    //    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.DETACH }, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnoreProperties(value = "jobs", allowSetters = true)
    private Project project;

    @ManyToMany(mappedBy = "jobs", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnore
    private Set<AppUser> appUsers = new HashSet<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.REFRESH)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Performance> performances = new HashSet<>();

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

    public Job name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Job description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public Job status(Status status) {
        this.status = status;
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Job startDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Job endDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean isEnable() {
        return enable;
    }

    public Job enable(Boolean enable) {
        this.enable = enable;
        return this;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Project getProject() {
        return project;
    }

    public Job project(Project project) {
        this.project = project;
        return this;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Set<AppUser> getAppUsers() {
        return appUsers;
    }

    public Job appUsers(Set<AppUser> appUsers) {
        this.appUsers = appUsers;
        return this;
    }

    public Job addAppUser(AppUser appUser) {
        this.appUsers.add(appUser);
        appUser.getJobs().add(this);
        return this;
    }

    public Job removeAppUser(AppUser appUser) {
        this.appUsers.remove(appUser);
        appUser.getJobs().remove(this);
        return this;
    }

    public void setAppUsers(Set<AppUser> appUsers) {
        this.appUsers = appUsers;
    }

    public Set<Performance> getPerformances() {
        return this.performances;
    }

    public Job performances(Set<Performance> performances) {
        this.performances = performances;
        return this;
    }

    public Job addPerformance(Performance performance) {
        this.performances.add(performance);
        performance.setJob(this);
        return this;
    }

    public Job removePerformance(Performance performance) {
        this.performances.remove(performance);
        performance.setJob(null);
        return this;
    }

    public void setPerformances(Set<Performance> performances) {
        this.performances = performances;
    }

    public Performance getPerfByDateAndUserId(LocalDate date, Long userId) {
        List<Performance> perfs = new ArrayList<Performance>();
        if (performances.size() > 0) {
            perfs =
                performances
                    .stream()
                    .filter(p -> p.getDate().compareTo(date) == 0 && p.getAppUser().getId().compareTo(userId) == 0)
                    .collect(Collectors.toList());
        }
        //        log.debug("PERFORMANCE:{}", perfs);
        if (perfs.size() > 1) {
            throw new IllegalStateException();
        }

        if (perfs.size() == 1) {
            return perfs.get(0);
        }
        return null;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    public void removeAppUsers() {
        if (this.appUsers.size() > 0) for (AppUser ap : this.appUsers) {
            log.debug("Request to delete Job from User : {}", ap);
            ap.removeJob(this);
            log.debug("Request to delete Job from User : {}", ap);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Job)) {
            return false;
        }
        return id != null && id.equals(((Job) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Job{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", status='" + getStatus() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", enable='" + isEnable() + "'" +
            ", performances="+getPerformances().size()+"'"+
            ", users="+getAppUsers()+"'"+
            ", project="+getProject()+
            "}";
    }
}
