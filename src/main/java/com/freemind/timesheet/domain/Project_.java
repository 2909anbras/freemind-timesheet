package com.freemind.timesheet.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Project.class)
public abstract class Project_ {
    public static volatile SingularAttribute<Project, Boolean> enable;
    public static volatile SetAttribute<Project, Job> jobs;
    public static volatile SingularAttribute<Project, String> name;
    public static volatile SingularAttribute<Project, Long> id;
    public static volatile SingularAttribute<Project, Customer> customer;

    public static final String ENABLE = "enable";
    public static final String JOBS = "jobs";
    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String CUSTOMER = "customer";
}
