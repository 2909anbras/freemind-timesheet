package com.freemind.timesheet.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Performance.
 */
@Entity
@Table(name = "performance", schema = "public")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Performance implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Min(value = 0)
    @Max(value = 16)
    @Column(name = "hours", nullable = false)
    private Integer hours;

    @Column(name = "description")
    private String description = "";

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JsonIgnoreProperties(value = "performances_user", allowSetters = true)
    @JoinColumn(name = "user_id")
    private AppUser appUser;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JsonIgnoreProperties(value = "performances_job", allowSetters = true)
    private Job job;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getHours() {
        return hours;
    }

    public Performance hours(Integer hours) {
        this.hours = hours;
        return this;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public LocalDate getDate() {
        return date;
    }

    public Performance date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public Performance appUser(AppUser appUser) {
        this.appUser = appUser;
        return this;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public Job getJob() {
        return job;
    }

    public Performance job(Job job) {
        this.job = job;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Performance description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Performance)) {
            return false;
        }
        return id != null && id.equals(((Performance) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Performance{" +
            "id=" + getId() +
            ", hours=" + getHours() +
            ", date='" + getDate() + "'" +
            ", description"+getDescription()+" "+
            ", userID="+getAppUser().getId()+
            "; jobId="+getJob().getId()+
            "}";
    }
}
