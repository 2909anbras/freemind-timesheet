package com.freemind.timesheet.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * A Customer.
 */
@Entity
@Table(name = "customer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Customer implements Serializable {
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

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Project> projects = new HashSet<>();

    @ManyToMany(cascade = CascadeType.REFRESH) //Inverser avec company
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(
        name = "customer_company",
        joinColumns = @JoinColumn(name = "customer_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "company_id", referencedColumnName = "id")
    )
    private Set<Company> companies = new HashSet<>();

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

    public Customer name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isEnable() {
        return enable;
    }

    public Customer enable(Boolean enable) {
        this.enable = enable;
        return this;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public Customer projects(Set<Project> projects) {
        this.projects = projects;
        return this;
    }

    public void removeProjects() {
        for (Project p : this.projects) {
            //    		p.removeCustomer(this.id);
        }
    }

    public Customer addProject(Project project) {
        this.projects.add(project);
        project.setCustomer(this);
        return this;
    }

    public Customer removeProject(Project project) {
        this.projects.remove(project);
        project.setCustomer(null);
        return this;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public Set<Company> getCompanies() {
        return companies;
    }

    public Customer companies(Set<Company> companies) {
        this.companies = companies;
        return this;
    }

    public Customer addCompany(Company company) {
        this.companies.add(company);
        company.getCustomers().add(this);
        return this;
    }

    public Customer removeCompany(Company company) {
        this.companies.remove(company);
        company.getCustomers().remove(this);
        return this;
    }

    public void setCompanies(Set<Company> companies) {
        this.companies = companies;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        return id != null && id.equals(((Customer) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Customer{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", enable='" + isEnable() + "'" +
            "}";
    }
}
//@ManyToMany(mappedBy = "customers",cascade = CascadeType.REFRESH)//vérifier si le nom donné doit être dans liquibase
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@JsonIgnore //from company
