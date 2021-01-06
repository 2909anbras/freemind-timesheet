package com.freemind.timesheet.service;

import com.freemind.timesheet.domain.Customer;
import com.freemind.timesheet.domain.Job;
import com.freemind.timesheet.domain.Project;
import com.freemind.timesheet.repository.CompanyRepository;
import com.freemind.timesheet.repository.CustomerRepository;
import com.freemind.timesheet.repository.JobRepository;
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

    private final JobService jobService;

    private final ProjectRepository projectRepository;

    private final CustomerRepository customerRepository;

    private final JobRepository jobRepository;

    private final ProjectMapper projectMapper;

    public ProjectService(
        ProjectRepository projectRepository,
        CustomerRepository customerRepository,
        ProjectMapper projectMapper,
        JobRepository jobRepository,
        JobService jobService
    ) {
        this.jobService = jobService;
        this.projectRepository = projectRepository;
        this.customerRepository = customerRepository;
        this.jobRepository = jobRepository;
        this.projectMapper = projectMapper;
    }

    /**
     * Save a project.
     *
     * @param projectDTO the entity to save.
     * @return the persisted entity.
     */
    public ProjectDTO save(ProjectDTO projectDTO) {
        log.debug("Request to save ProjectDTO : {}", projectDTO);
        Project project = projectMapper.toEntity(projectDTO);
        log.debug("Request to save Project : {}", project);
        Customer c = customerRepository.findById(projectDTO.getCustomerId()).get();
        log.debug("Request to save Customer : {}", c);
        c.addProject(project);
        customerRepository.save(c);

        project = projectRepository.save(project);

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
        boolean canDelete = true;
        log.debug("Request to delete Project : {}", id);
        List<Job> jobs = jobRepository.getJobByProject(id);
        for (Job j : jobs) {
            if (j.getPerformances().size() > 0 && canDelete) canDelete = false;
            this.jobService.delete(j.getId());
            //            if (j.getPerformances().size() > 0) {
            //                j.removeAppUsers();
            //                jobRepository.save(j);
            //            }
        }
        Project p = projectRepository.getOne(id);
        log.debug("Request to find Project : {}", p);
        if (canDelete) projectRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsByUserId(Long userId) {
        log.debug("Request to delete Project : {}", userId);

        Page<ProjectDTO> tmp =
            ((Page<Project>) this.projectRepository.findProjectsByUserId(userId, null)).map(t -> this.projectMapper.toDto((Project) t));

        return tmp.getContent();
    }
}
