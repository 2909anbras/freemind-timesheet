package com.freemind.timesheet.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Company.class)
public abstract class Company_ {
    public static volatile SingularAttribute<Company, String> name;
    public static volatile SetAttribute<Company, AppUser> appUsers;
    public static volatile SingularAttribute<Company, Long> id;
    public static volatile SetAttribute<Company, Customer> customers;

    public static final String NAME = "name";
    public static final String APP_USERS = "appUsers";
    public static final String ID = "id";
    public static final String CUSTOMERS = "customers";
}
