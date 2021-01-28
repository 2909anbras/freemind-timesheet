package com.freemind.timesheet.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AppUser.class)
public abstract class AppUser_ {
    public static volatile SingularAttribute<AppUser, User> internalUser;
    public static volatile SingularAttribute<AppUser, String> phone;
    public static volatile SetAttribute<AppUser, Job> jobs;
    public static volatile SingularAttribute<AppUser, Company> company;
    public static volatile SingularAttribute<AppUser, Long> id;

    public static final String INTERNAL_USER = "internalUser";
    public static final String PHONE = "phone";
    public static final String JOBS = "jobs";
    public static final String COMPANY = "company";
    public static final String ID = "id";
}
