package com.freemind.timesheet.service.mapper;


import com.freemind.timesheet.domain.*;
import com.freemind.timesheet.service.dto.ProjectDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Project} and its DTO {@link ProjectDTO}.
 */
@Mapper(componentModel = "spring", uses = {CustomerMapper.class})
public interface ProjectMapper extends EntityMapper<ProjectDTO, Project> {

    @Mapping(source = "customer.id", target = "customerId")
    ProjectDTO toDto(Project project);

    @Mapping(target = "jobs", ignore = true)
    @Mapping(target = "removeJob", ignore = true)
    @Mapping(source = "customerId", target = "customer")
    Project toEntity(ProjectDTO projectDTO);

    default Project fromId(Long id) {
        if (id == null) {
            return null;
        }
        Project project = new Project();
        project.setId(id);
        return project;
    }
}
