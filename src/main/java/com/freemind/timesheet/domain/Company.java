package com.freemind.timesheet.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Company.
 */
@Entity
@Table(name = "company", schema = "public")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Company implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<AppUser> appUsers = new HashSet<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL) //mappedBy = "company_customer",
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    //    @JsonIgnore
    private Set<Customer> customers;

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

    public Company name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<AppUser> getAppUsers() {
        return appUsers;
    }

    public Company appUsers(Set<AppUser> appUsers) {
        this.appUsers = appUsers;
        return this;
    }

    public Company addAppUser(AppUser appUser) {
        this.appUsers.add(appUser);
        appUser.setCompany(this);
        return this;
    }

    public Company removeAppUser(AppUser appUser) {
        this.appUsers.remove(appUser);
        appUser.setCompany(null);
        return this;
    }

    public void setAppUsers(Set<AppUser> appUsers) {
        this.appUsers = appUsers;
    }

    public Set<Customer> getCustomers() {
        return customers;
    }

    public Company customers(Set<Customer> customer) {
        this.customers = customer;
        return this;
    }

    public Company addCustomer(Customer customer) {
        this.customers.add(customer);
        customer.setCompany(this);
        return this;
    }

    public Company removeCustomer(Customer customer) {
        this.customers.remove(customer);
        customer.setCompany(null);
        return this;
    }

    public void setCustomer(Set<Customer> customer) {
        this.customers = customer;
    }

    @PreRemove
    public void removeCustomer() {
        this.customers = null;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Company)) {
            return false;
        }
        return id != null && id.equals(((Company) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Company{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", customers= "+ getCustomers().size()+ "'"+
            "}";
    }
}
