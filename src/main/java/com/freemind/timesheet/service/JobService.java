package com.freemind.timesheet.service;

import com.freemind.timesheet.domain.AppUser;
import com.freemind.timesheet.domain.Customer;
import com.freemind.timesheet.domain.Job;
import com.freemind.timesheet.domain.Project;
import com.freemind.timesheet.repository.AppUserRepository;
import com.freemind.timesheet.repository.JobRepository;
import com.freemind.timesheet.repository.ProjectRepository;
import com.freemind.timesheet.service.dto.JobDTO;
import com.freemind.timesheet.service.mapper.JobMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Job}.
 */
@Service
@Transactional
public class JobService {
    private final Logger log = LoggerFactory.getLogger(JobService.class);

    private final ProjectRepository projectRepository;

    private final AppUserRepository appUserRepository;

    private final JobRepository jobRepository;

    private final JobMapper jobMapper;

    public JobService(
        JobRepository jobRepository,
        ProjectRepository projectRepository,
        AppUserRepository appUserRepository,
        JobMapper jobMapper
    ) {
        this.appUserRepository = appUserRepository;
        this.projectRepository = projectRepository;
        this.jobRepository = jobRepository;
        this.jobMapper = jobMapper;
    }

    /**
     * Save a job.
     *
     * @param jobDTO the entity to save.
     * @return the persisted entity.
     */
    public JobDTO save(JobDTO jobDTO) {
        log.debug("Request to save Job : {}", jobDTO);
        Job job = jobMapper.toEntity(jobDTO);
        job = jobRepository.save(job);

        final Job jb = job;
        Project p = null;
        if (jobDTO.getProjectId() != null) {
            p = projectRepository.findById(job.getProject().getId()).get();
            p.addJob(job);
            projectRepository.save(p);
        }
        jobRepository.save(job);

        job
            .getAppUsers()
            .forEach(
                ap -> {
                    AppUser tmp = appUserRepository.findById(ap.getId()).get();
                    tmp.addJob(jb);
                    log.debug("AppUser Jobs: {}", tmp.getJobs());
                    appUserRepository.save(tmp);
                }
            );

        return jobMapper.toDto(job);
    }

    /**
     * Get all the jobs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<JobDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Jobs");
        return jobRepository.findAll(pageable).map(jobMapper::toDto);
    }

    /**
     * Get one job by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<JobDTO> findOne(Long id) {
        log.debug("Request to get Job : {}", id);
        return jobRepository.findById(id).map(jobMapper::toDto);
    }

    /**
     * Delete the job by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Job : {}", id);
        jobRepository.deleteById(id);
    }

    public List<Long> findProjectsByAppUsersId(List<Long> appUsersId) {
        // TODO Auto-generated method stub
        log.debug("Request to find projects id : {}", appUsersId);

        return jobRepository.findProjectsByUsersId(appUsersId);
    }

    @Transactional(readOnly = true)
    public List<JobDTO> findByUser(Long userId) {
        log.debug("Request to find jobs by userId : {}", userId);
        Page<JobDTO> tmp = ((Page<Job>) jobRepository.findByUserId(userId, null)).map(t -> this.jobMapper.toDto((Job) t));
        return tmp.getContent();
    }
}
