package com.freemind.timesheet.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Customer.class)
public abstract class Customer_ {
    public static volatile SetAttribute<Customer, Company> companies;
    public static volatile SetAttribute<Customer, Project> projects;
    public static volatile SingularAttribute<Customer, Boolean> enable;
    public static volatile SingularAttribute<Customer, String> name;
    public static volatile SingularAttribute<Customer, Long> id;

    public static final String COMPANIES = "companies";
    public static final String PROJECTS = "projects";
    public static final String ENABLE = "enable";
    public static final String NAME = "name";
    public static final String ID = "id";
}
