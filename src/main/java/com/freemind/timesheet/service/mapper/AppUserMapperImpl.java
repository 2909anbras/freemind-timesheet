package com.freemind.timesheet.service.mapper;

import com.freemind.timesheet.domain.AppUser;
import com.freemind.timesheet.domain.Company;
import com.freemind.timesheet.domain.Job;
import com.freemind.timesheet.domain.Performance;
import com.freemind.timesheet.domain.User;
import com.freemind.timesheet.service.dto.AppUserDTO;
import com.freemind.timesheet.service.dto.JobDTO;
import com.freemind.timesheet.service.dto.PerformanceDTO;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-11-17T13:12:20+0100",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.8 (AdoptOpenJDK)"
)
@Component
public class AppUserMapperImpl implements AppUserMapper {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private PerformanceMapper performanceMapper;

    @Override
    public List<AppUser> toEntity(List<AppUserDTO> dtoList) {
        if (dtoList == null) {
            return null;
        }

        List<AppUser> list = new ArrayList<AppUser>(dtoList.size());
        for (AppUserDTO appUserDTO : dtoList) {
            list.add(toEntity(appUserDTO));
        }

        return list;
    }

    @Override
    public List<AppUserDTO> toDto(List<AppUser> entityList) {
        if (entityList == null) {
            return null;
        }

        List<AppUserDTO> list = new ArrayList<AppUserDTO>(entityList.size());
        for (AppUser appUser : entityList) {
            list.add(toDto(appUser));
        }

        return list;
    }

    @Override
    public AppUserDTO toDto(AppUser appUser) {
        if (appUser == null) {
            return null;
        }

        AppUserDTO appUserDTO = new AppUserDTO();

        appUserDTO.setCompanyId(appUserCompanyId(appUser));
        appUserDTO.setInternalUserId(appUserInternalUserId(appUser));
        appUserDTO.setId(appUser.getId());
        appUserDTO.setPhone(appUser.getPhone());
        appUserDTO.setJobs(jobSetToJobDTOSet(appUser.getJobs()));
        appUserDTO.setPerformances(performanceSetToPerformanceDTOSet(appUser.getPerformances()));

        return appUserDTO;
    }

    @Override
    public AppUser toEntity(AppUserDTO appUserDTO) {
        if (appUserDTO == null) {
            return null;
        }

        AppUser appUser = new AppUser();

        appUser.setCompany(companyMapper.fromId(appUserDTO.getCompanyId()));
        appUser.setInternalUser(userMapper.userFromId(appUserDTO.getInternalUserId()));
        appUser.setId(appUserDTO.getId());
        appUser.setPhone(appUserDTO.getPhone());
        appUser.setJobs(jobDTOSetToJobSet(appUserDTO.getJobs()));
        appUser.setPerformances(performanceDTOSetToPerformanceSet(appUserDTO.getPerformances()));

        return appUser;
    }

    private Long appUserCompanyId(AppUser appUser) {
        if (appUser == null) {
            return null;
        }
        Company company = appUser.getCompany();
        if (company == null) {
            return null;
        }
        Long id = company.getId();
        if (id == null) {
            return null;
        }
        return id;
    }

    private Long appUserInternalUserId(AppUser appUser) {
        if (appUser == null) {
            return null;
        }
        User internalUser = appUser.getInternalUser();
        if (internalUser == null) {
            return null;
        }
        Long id = internalUser.getId();
        if (id == null) {
            return null;
        }
        return id;
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

    protected Set<PerformanceDTO> performanceSetToPerformanceDTOSet(Set<Performance> set) {
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
