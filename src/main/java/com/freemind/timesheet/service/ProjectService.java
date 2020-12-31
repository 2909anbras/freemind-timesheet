package com.freemind.timesheet.service;

import com.freemind.timesheet.domain.Customer;
import com.freemind.timesheet.domain.Project;
import com.freemind.timesheet.repository.CustomerRepository;
import com.freemind.timesheet.repository.ProjectRepository;
import com.freemind.timesheet.service.dto.ProjectDTO;
import com.freemind.timesheet.service.mapper.ProjectMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Project}.
 */
@Service
@Transactional
public class ProjectService {
    private final Logger log = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository projectRepository;

    private final CustomerRepository customerRepository;

    private final ProjectMapper projectMapper;

    public ProjectService(ProjectRepository projectRepository, CustomerRepository customerRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.customerRepository = customerRepository;
        this.projectMapper = projectMapper;
    }

    /**
     * Save a project.
     *
     * @param projectDTO the entity to save.
     * @return the persisted entity.
     */
    public ProjectDTO save(ProjectDTO projectDTO) {
        log.debug("Request to save Project : {}", projectDTO);
        Project project = projectMapper.toEntity(projectDTO);
        project = projectRepository.save(project);

        Customer c = customerRepository.findById(project.getCustomer().getId()).get();
        c.addProject(project);
        customerRepository.save(c);
        log.debug("Request to save Customer : {}", c);

        return projectMapper.toDto(project);
    }

    /**
     * Get all the projects.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProjectDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Projects");
        return projectRepository.findAll(pageable).map(projectMapper::toDto);
    }

    /**
     * Get one project by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProjectDTO> findOne(Long id) {
        log.debug("Request to get Project : {}", id);
        return projectRepository.findById(id).map(projectMapper::toDto);
    }

    /**
     * Delete the project by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Project : {}", id);
        projectRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsByUserId(Long userId) {
        log.debug("Request to delete Project : {}", userId);

        Page<ProjectDTO> tmp =
            ((Page<Project>) this.projectRepository.findProjectsByUserId(userId, null)).map(t -> this.projectMapper.toDto((Project) t));

        return tmp.getContent();
    }
}
