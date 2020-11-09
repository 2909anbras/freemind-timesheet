package com.freemind.timesheet.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.freemind.timesheet.domain.enumeration.Language;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ToolUser.
 */
@Entity
@Table(name = "tool_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ToolUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(min = 3, max = 10)
    @Column(name = "first_name", length = 10, nullable = false)
    private String firstName;

    @NotNull
    @Size(min = 3, max = 10)
    @Column(name = "last_name", length = 10, nullable = false)
    private String lastName;

    @NotNull
    @Pattern(regexp = "^[\\\\w!#$%&’*+/=?`{|}~^-]+(?:\\\\.[\\\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\\\.)+[a-zA-Z]{2,6}$")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull
    @Size(min = 3, max = 20)
    @Column(name = "password", length = 20, nullable = false)
    private String password;

    @NotNull
    @Column(name = "enable", nullable = false)
    private Boolean enable;

    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private Language language;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(
        name = "tool_user_job",
        joinColumns = @JoinColumn(name = "tool_user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "job_id", referencedColumnName = "id")
    )
    private Set<Job> jobs = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = "toolUsers", allowSetters = true)
    private Company company;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public ToolUser firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public ToolUser lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public ToolUser email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public ToolUser password(String password) {
        this.password = password;
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isEnable() {
        return enable;
    }

    public ToolUser enable(Boolean enable) {
        this.enable = enable;
        return this;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Language getLanguage() {
        return language;
    }

    public ToolUser language(Language language) {
        this.language = language;
        return this;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Set<Job> getJobs() {
        return jobs;
    }

    public ToolUser jobs(Set<Job> jobs) {
        this.jobs = jobs;
        return this;
    }

    public ToolUser addJob(Job job) {
        this.jobs.add(job);
        job.getToolUsers().add(this);
        return this;
    }

    public ToolUser removeJob(Job job) {
        this.jobs.remove(job);
        job.getToolUsers().remove(this);
        return this;
    }

    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }

    public Company getCompany() {
        return company;
    }

    public ToolUser company(Company company) {
        this.company = company;
        return this;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ToolUser)) {
            return false;
        }
        return id != null && id.equals(((ToolUser) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ToolUser{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", password='" + getPassword() + "'" +
            ", enable='" + isEnable() + "'" +
            ", language='" + getLanguage() + "'" +
            "}";
    }
}
