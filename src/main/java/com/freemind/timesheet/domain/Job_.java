package com.freemind.timesheet.domain;

import com.freemind.timesheet.domain.enumeration.Status;
import java.time.LocalDate;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Job.class)
public abstract class Job_ {
    public static volatile SingularAttribute<Job, LocalDate> endDate;
    public static volatile SingularAttribute<Job, Boolean> enable;
    public static volatile SingularAttribute<Job, String> name;
    public static volatile SingularAttribute<Job, String> description;
    public static volatile SingularAttribute<Job, Project> project;
    public static volatile SetAttribute<Job, AppUser> appUsers;
    public static volatile SingularAttribute<Job, Long> id;
    public static volatile SingularAttribute<Job, LocalDate> startDate;
    public static volatile SingularAttribute<Job, Status> status;

    public static final String END_DATE = "endDate";
    public static final String ENABLE = "enable";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String PROJECT = "project";
    public static final String APP_USERS = "appUsers";
    public static final String ID = "id";
    public static final String START_DATE = "startDate";
    public static final String STATUS = "status";
}
