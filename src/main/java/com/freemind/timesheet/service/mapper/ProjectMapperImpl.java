package com.freemind.timesheet.service.mapper;

import com.freemind.timesheet.domain.Customer;
import com.freemind.timesheet.domain.Job;
import com.freemind.timesheet.domain.Project;
import com.freemind.timesheet.service.dto.JobDTO;
import com.freemind.timesheet.service.dto.ProjectDTO;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-11-17T13:12:20+0100",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.8 (AdoptOpenJDK)"
)
@Component
public class ProjectMapperImpl implements ProjectMapper {
    private final Logger log = LoggerFactory.getLogger(ProjectMapperImpl.class);

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private JobMapper jobMapper;

    @Override
    public List<Project> toEntity(List<ProjectDTO> dtoList) {
        if (dtoList == null) {
            return null;
        }

        List<Project> list = new ArrayList<Project>(dtoList.size());
        for (ProjectDTO projectDTO : dtoList) {
            list.add(toEntity(projectDTO));
        }

        return list;
    }

    @Override
    public List<ProjectDTO> toDto(List<Project> entityList) {
        if (entityList == null) {
            return null;
        }

        List<ProjectDTO> list = new ArrayList<ProjectDTO>(entityList.size());
        for (Project project : entityList) {
            list.add(toDto(project));
        }

        return list;
    }

    @Override
    public ProjectDTO toDto(Project project) {
        if (project == null) {
            return null;
        }

        ProjectDTO projectDTO = new ProjectDTO();

        projectDTO.setCustomerId(projectCustomerId(project));
        projectDTO.setCustomerName(projectCustomerName(project));
        projectDTO.setId(project.getId());
        projectDTO.setName(project.getName());
        projectDTO.setEnable(project.isEnable());
        projectDTO.setJobs(jobSetToJobDTOSet(project.getJobs()));

        return projectDTO;
    }

    @Override
    public Project toEntity(ProjectDTO projectDTO) {
        if (projectDTO == null) {
            return null;
        }

        Project project = new Project();

        project.setCustomer(customerMapper.fromId(projectDTO.getCustomerId()));
        project.setId(projectDTO.getId());
        project.setName(projectDTO.getName());

        project.setEnable(projectDTO.isEnable());
        project.setJobs(jobDTOSetToJobSet(projectDTO.getJobs()));
        log.debug("PROJECT : {}", project);
        return project;
    }

    private Long projectCustomerId(Project project) {
        if (project == null) {
            return null;
        }
        Customer customer = project.getCustomer();
        if (customer == null) {
            return null;
        }
        Long id = customer.getId();
        if (id == null) {
            return null;
        }
        return id;
    }

    private String projectCustomerName(Project project) {
        if (project == null) {
            return null;
        }
        Customer customer = project.getCustomer();
        if (customer == null) {
            return null;
        }
        String name = customer.getName();
        if (name == null) {
            return null;
        }
        return name;
    }

    protected Set<JobDTO> jobSetToJobDTOSet(Set<Job> set) {
        if (set == null) {
            return null;
        }

        Set<JobDTO> set1 = new HashSet<JobDTO>(Math.max((int) (set.size() / .75f) + 1, 16));
        for (Job job : set) {
            set1.add(jobMapper.toDto(job));
        }

        return set1;
    }

    protected Set<Job> jobDTOSetToJobSet(Set<JobDTO> set) {
        if (set == null) {
            return null;
        }

        Set<Job> set1 = new HashSet<Job>(Math.max((int) (set.size() / .75f) + 1, 16));
        for (JobDTO jobDTO : set) {
            set1.add(jobMapper.toEntity(jobDTO));
        }

        return set1;
    }
}
