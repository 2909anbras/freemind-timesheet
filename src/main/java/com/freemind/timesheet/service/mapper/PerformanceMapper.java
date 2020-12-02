package com.freemind.timesheet.service.mapper;

import com.freemind.timesheet.domain.*;
import com.freemind.timesheet.service.dto.PerformanceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Performance} and its DTO {@link PerformanceDTO}.
 */
@Mapper(componentModel = "spring", uses = { AppUserMapper.class, JobMapper.class })
public interface PerformanceMapper extends EntityMapper<PerformanceDTO, Performance> {
    @Mapping(source = "appUser.id", target = "appUserId")
    @Mapping(source = "job.id", target = "jobId")
    PerformanceDTO toDto(Performance performance);

    @Mapping(source = "appUserId", target = "appUser")
    @Mapping(source = "jobId", target = "job")
    Performance toEntity(PerformanceDTO performanceDTO);

    default Performance fromId(Long id) {
        if (id == null) {
            return null;
        }
        Performance performance = new Performance();
        performance.setId(id);
        return performance;
    }
}
