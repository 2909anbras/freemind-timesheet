package com.freemind.timesheet.service.mapper;

import com.freemind.timesheet.domain.AppUser;
import com.freemind.timesheet.domain.Customer;
import com.freemind.timesheet.domain.Job;
import com.freemind.timesheet.domain.Performance;
import com.freemind.timesheet.domain.Project;
import com.freemind.timesheet.repository.CompanyRepository;
import com.freemind.timesheet.service.JobService;
import com.freemind.timesheet.service.dto.AppUserDTO;
import com.freemind.timesheet.service.dto.JobDTO;
import com.freemind.timesheet.service.dto.PerformanceDTO;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
public class JobMapperImpl implements JobMapper {
    private final Logger log = LoggerFactory.getLogger(JobMapperImpl.class);

    private CompanyRepository companyRepository;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private PerformanceMapper performanceMapper;

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public List<Job> toEntity(List<JobDTO> dtoList) {
        if (dtoList == null) {
            return null;
        }

        List<Job> list = new ArrayList<Job>(dtoList.size());
        for (JobDTO jobDTO : dtoList) {
            list.add(toEntity(jobDTO));
        }

        return list;
    }

    @Override
    public List<JobDTO> toDto(List<Job> entityList) {
        if (entityList == null) {
            return null;
        }

        List<JobDTO> list = new ArrayList<JobDTO>(entityList.size());
        for (Job job : entityList) {
            list.add(toDto(job));
        }

        return list;
    }

    @Override
    public JobDTO toDto(Job job) {
        if (job == null) {
            return null;
        }

        JobDTO jobDTO = new JobDTO();

        jobDTO.setProjectId(jobProjectId(job));
        jobDTO.setId(job.getId());
        jobDTO.setName(job.getName());
        jobDTO.setProjectName(jobProjectName(job));
        jobDTO.setDescription(job.getDescription());
        jobDTO.setStatus(job.getStatus());
        jobDTO.setStartDate(job.getStartDate());
        jobDTO.setEndDate(job.getEndDate());
        jobDTO.setEnable(job.isEnable());
        jobDTO.setAppUsers(this.appUserSetToAppUserDTOSet(job.getAppUsers()));
        jobDTO.setPerformances(performanceSetToPerformanceDTOSet(job.getPerformances()));

        log.debug("JOBDTO : {}", jobDTO);

        return jobDTO;
    }

    @Override
    public Job toEntity(JobDTO jobDTO) {
        if (jobDTO == null) {
            return null;
        }

        Job job = new Job();

        job.setProject(projectMapper.fromId(jobDTO.getProjectId()));
        job.setId(jobDTO.getId());
        job.setName(jobDTO.getName());
        job.setDescription(jobDTO.getDescription());
        job.setStatus(jobDTO.getStatus());
        job.setStartDate(jobDTO.getStartDate());
        job.setEndDate(jobDTO.getEndDate());
        job.setEnable(jobDTO.isEnable());
        job.setAppUsers(this.appUserDTOSetToAppUserSet(jobDTO.getAppUsers()));
        job.setPerformances(performanceDTOSetToPerformanceSet(jobDTO.getPerformances()));
        log.debug("JOBD : {}", job);
        return job;
    }

    private String jobProjectName(Job job) {
        if (job == null) {
            return null;
        }
        Project project = job.getProject();
        if (project == null) {
            return null;
        }
        String name = project.getName();
        if (name == null) {
            return null;
        }
        return name;
    }

    private Long jobProjectId(Job job) {
        if (job == null) {
            return null;
        }
        Project project = job.getProject();
        if (project == null) {
            return null;
        }
        Long id = project.getId();
        if (id == null) {
            return null;
        }
        return id;
    }

    protected Set<AppUserDTO> appUserSetToAppUserDTOSet(Set<AppUser> set) {
        if (set == null) {
            return null;
        }
        Set<AppUserDTO> set1 = new HashSet<AppUserDTO>(Math.max((int) (set.size() / .75f) + 1, 16));
        for (AppUser appUser : set) {
            AppUserDTO tmp = new AppUserDTO();
            tmp.setId(appUser.getId());

            tmp.setCompanyId(appUser.getCompany().getId());
            List list = appUser.getPerformances().stream().map(p -> performanceMapper.toDto(p)).collect(Collectors.toList());
            Set<PerformanceDTO> foo = new HashSet<PerformanceDTO>(list);
            tmp.setPerformances(foo);
            set1.add(tmp);
            //            set1.add( appUserMapper.toDto( appUser ) );
        }
        return set1;
    }

    protected Set<AppUser> appUserDTOSetToAppUserSet(Set<AppUserDTO> set) {
        if (set == null) {
            return null;
        }
        Set<AppUser> set1 = new HashSet<AppUser>(Math.max((int) (set.size() / .75f) + 1, 16));
        for (AppUserDTO appUserDTO : set) {
            AppUser ap = new AppUser();
            ap.setId(appUserDTO.getId());
            ap.setCompany(companyMapper.fromId(appUserDTO.getCompanyId()));
            set1.add(ap);
            log.debug("app : {}", set1);
        }
        return set1;
    }

    private Set<PerformanceDTO> performanceSetToPerformanceDTOSet(Set<Performance> set) {
        if (set == null) {
            return null;
        }
        Set<PerformanceDTO> set1 = new HashSet<PerformanceDTO>(Math.max((int) (set.size() / .75f) + 1, 16));
        for (Performance performance : set) {
            set1.add(performanceMapper.toDto(performance));
        }
        return set1;
    }

    protected Set<Performance> performanceDTOSetToPerformanceSet(Set<PerformanceDTO> set) {
        if (set == null) {
            return null;
        }
        Set<Performance> set1 = new HashSet<Performance>(Math.max((int) (set.size() / .75f) + 1, 16));
        for (PerformanceDTO performanceDTO : set) {
            set1.add(performanceMapper.toEntity(performanceDTO));
        }
        return set1;
    }
}
