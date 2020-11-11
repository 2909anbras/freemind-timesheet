package com.freemind.timesheet.service.mapper;


import com.freemind.timesheet.domain.*;
import com.freemind.timesheet.service.dto.JobDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Job} and its DTO {@link JobDTO}.
 */
@Mapper(componentModel = "spring", uses = {ProjectMapper.class})
public interface JobMapper extends EntityMapper<JobDTO, Job> {

    @Mapping(source = "project.id", target = "projectId")
    JobDTO toDto(Job job);

    @Mapping(source = "projectId", target = "project")
    @Mapping(target = "appUsers", ignore = true)
    @Mapping(target = "removeAppUser", ignore = true)
    Job toEntity(JobDTO jobDTO);

    default Job fromId(Long id) {
        if (id == null) {
            return null;
        }
        Job job = new Job();
        job.setId(id);
        return job;
    }
}
