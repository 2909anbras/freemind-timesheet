package com.freemind.timesheet.service.mapper;

import com.freemind.timesheet.domain.Performance;
import com.freemind.timesheet.service.dto.PerformanceDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-11-17T13:12:19+0100",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.8 (AdoptOpenJDK)"
)
@Component
public class PerformanceMapperImpl implements PerformanceMapper {
    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private JobMapper jobMapper;

    @Override
    public PerformanceDTO toDto(Performance entity) {
        if (entity == null) {
            return null;
        }
        PerformanceDTO performanceDTO = new PerformanceDTO();
        performanceDTO.setId(entity.getId());
        performanceDTO.setAppUserId(entity.getAppUser().getId());
        performanceDTO.setJobId(entity.getJob().getId());
        performanceDTO.setDate(entity.getDate());
        performanceDTO.setDescription(entity.getDescription());
        performanceDTO.setHours(entity.getHours());
        return performanceDTO;
    }

    @Override
    public Performance toEntity(PerformanceDTO performanceDTO) {
        if (performanceDTO == null) {
            return null;
        }
        Performance performance = new Performance();
        performance.setId(performanceDTO.getId());
        performance.setAppUser(appUserMapper.fromId(performanceDTO.getAppUserId()));
        performance.setJob(jobMapper.fromId(performanceDTO.getJobId()));
        performance.setDate(performanceDTO.getDate());
        performance.setHours(performanceDTO.getHours());
        performance.setDescription(performanceDTO.getDescription());
        return performance;
    }

    @Override
    public List<Performance> toEntity(List<PerformanceDTO> dtoList) {
        if (dtoList == null) {
            return null;
        }

        List<Performance> list = new ArrayList<Performance>(dtoList.size());
        for (PerformanceDTO performanceDTO : dtoList) {
            list.add(toEntity(performanceDTO));
        }

        return list;
    }

    @Override
    public List<PerformanceDTO> toDto(List<Performance> entityList) {
        if (entityList == null) {
            return null;
        }

        List<PerformanceDTO> list = new ArrayList<PerformanceDTO>(entityList.size());
        for (Performance performance : entityList) {
            list.add(toDto(performance));
        }

        return list;
    }
}
