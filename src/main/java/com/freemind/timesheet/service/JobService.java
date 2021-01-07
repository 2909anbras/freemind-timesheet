package com.freemind.timesheet.service;

import com.freemind.timesheet.domain.AppUser;
import com.freemind.timesheet.domain.Customer;
import com.freemind.timesheet.domain.Job;
import com.freemind.timesheet.domain.Project;
import com.freemind.timesheet.repository.AppUserRepository;
import com.freemind.timesheet.repository.CompanyRepository;
import com.freemind.timesheet.repository.JobRepository;
import com.freemind.timesheet.repository.ProjectRepository;
import com.freemind.timesheet.service.dto.AppUserDTO;
import com.freemind.timesheet.service.dto.JobDTO;
import com.freemind.timesheet.service.mapper.AppUserMapperImpl;
import com.freemind.timesheet.service.mapper.JobMapper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
    private final CompanyRepository companyRepository;

    private final CompanyRepository companyService;

    private final Logger log = LoggerFactory.getLogger(JobService.class);

    private final AppUserMapperImpl appUserMapper;

    private final ProjectRepository projectRepository;

    private final AppUserRepository appUserRepository;

    private final JobRepository jobRepository;

    private final JobMapper jobMapper;

    public JobService(
        CompanyRepository companyService,
        JobRepository jobRepository,
        ProjectRepository projectRepository,
        AppUserRepository appUserRepository,
        JobMapper jobMapper,
        CompanyRepository companyRepository,
        AppUserMapperImpl appUserMapper
    ) {
        this.companyRepository = companyRepository;
        this.companyService = companyService;
        this.appUserMapper = appUserMapper;
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
    public JobDTO update(JobDTO jobDTO) {
        //job
        Job job = jobMapper.toEntity(jobDTO);
        log.debug("Request to update Job : {}", jobDTO);
        //project refresh
        Optional<Project> projectToRemove;
        projectToRemove = projectRepository.findProjectByJob(jobDTO.getId());
        Project p;

        if (jobDTO.getProjectId() != null) {
            if (projectToRemove.isPresent() && jobDTO.getProjectId() != projectToRemove.get().getId()) {
                projectToRemove.get().removeJob(job);
                projectRepository.save(projectToRemove.get());
            }
            p = projectRepository.findById(jobDTO.getProjectId()).get();
            p.addJob(job);
            projectRepository.save(p);
            job.setProject(p);
        }

        //user
        //si jobDTO moins de appusers=>remove job de appUser
        List<AppUser> oldUsers = appUserRepository.findUsersByJob(job.getId());
        log.debug("OLD APPUSERS: {}", oldUsers);
        List<AppUser> oldUsersBis = new ArrayList<AppUser>();
        if (oldUsers.size() >= jobDTO.getAppUsers().size()) {
            log.debug("INSIDE: {}");
            if (jobDTO.getAppUsers().size() > 0) {
                for (AppUser user : oldUsers) {
                    for (AppUserDTO userDto : jobDTO.getAppUsers()) {
                        if (user.getId() != userDto.getId()) oldUsersBis.add(user);
                    }
                }
            } else oldUsersBis = oldUsers;
            log.debug("INSIDE OLDUSERS: {}", oldUsersBis);
            for (AppUser ap : oldUsersBis) {
                log.debug("Old User removed job: {}", ap);
                ap.removeJob(job);
                appUserRepository.save(ap);
            }
        }
        List<AppUserDTO> arr = new ArrayList<>(jobDTO.getAppUsers());
        job.appUsers(new HashSet<AppUser>(appUserMapper.toEntity(arr)));

        job
            .getAppUsers()
            .forEach(
                ap -> {
                    AppUser tmp = appUserRepository.findById(ap.getId()).get();
                    tmp.addJob(job);
                    log.debug("AppUser : {}", ap);
                    appUserRepository.save(tmp);
                }
            );
        log.debug("Job Saved : {}", job);
        jobRepository.save(job);
        return jobMapper.toDto(job);
    }

    public JobDTO save(JobDTO jobDTO) {
        log.debug("Request to save Job : {}", jobDTO);
        Job job = jobMapper.toEntity(jobDTO);
        jobRepository.save(job);
        Project p;
        if (jobDTO.getProjectId() != null) {
            p = projectRepository.findById(jobDTO.getProjectId()).get();
            p.addJob(job);
            projectRepository.save(p);
        }
        log.debug("APPUSERS : {}", job.getAppUsers());

        job
            .getAppUsers()
            .forEach(
                ap -> {
                    AppUser tmp = appUserRepository.findById(ap.getId()).get();
                    tmp.addJob(job);
                    log.debug("AppUser : {}", ap);
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
        Job j = jobRepository.getOne(id);
        log.debug("Request to delete Job : {}", j);
        log.debug("Request to delete Job : {}", j.getPerformances().size() == 0);

        if (j.getPerformances().size() == 0 && j.getAppUsers() != null) {
            Set<AppUser> tmp = new HashSet(j.getAppUsers());
            for (AppUser ap : tmp) {
                ap.removeJob(j);
                this.appUserRepository.save(ap);
                log.debug("Request to delete Job from User : {}", ap);
                log.debug("Request to delete Job : {}", j);
            }
            jobRepository.save(j);
            jobRepository.deleteById(id);
        } //else error:> can't delete job with perf
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
