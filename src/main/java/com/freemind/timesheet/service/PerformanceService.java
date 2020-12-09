package com.freemind.timesheet.service;

import com.freemind.timesheet.domain.AppUser;
import com.freemind.timesheet.domain.Customer;
import com.freemind.timesheet.domain.Job;
import com.freemind.timesheet.domain.Performance;
import com.freemind.timesheet.domain.Project;
import com.freemind.timesheet.repository.AppUserRepository;
import com.freemind.timesheet.repository.JobRepository;
import com.freemind.timesheet.repository.PerformanceRepository;
import com.freemind.timesheet.service.dto.PerformanceDTO;
import com.freemind.timesheet.service.mapper.PerformanceMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Performance}.
 */
@Service
@Transactional
public class PerformanceService {
    private final Logger log = LoggerFactory.getLogger(PerformanceService.class);

    private final PerformanceRepository performanceRepository;

    private final AppUserRepository appUserRepository;

    private final JobRepository jobRepository;

    private final PerformanceMapper performanceMapper;

    public PerformanceService(
        PerformanceRepository performanceRepository,
        AppUserRepository appUserRepository,
        JobRepository jobRepository,
        PerformanceMapper performanceMapper
    ) {
        this.performanceRepository = performanceRepository;
        this.jobRepository = jobRepository;
        this.appUserRepository = appUserRepository;
        this.performanceMapper = performanceMapper;
    }

    /**
     * Save a performance.
     *
     * @param performanceDTO the entity to save.
     * @return the persisted entity.
     */
    public PerformanceDTO save(PerformanceDTO performanceDTO) {
        log.debug("Request to save Performance : {}", performanceDTO);
        Performance performance = performanceMapper.toEntity(performanceDTO);
        performance = performanceRepository.save(performance);

        AppUser ap = appUserRepository.findById(performance.getAppUser().getId()).get();
        ap.addPerformance(performance);
        appUserRepository.save(ap);

        Job j = jobRepository.findById(performance.getJob().getId()).get();
        j.addPerformance(performance);
        jobRepository.save(j);

        return performanceMapper.toDto(performance);
    }

    /**
     * Get all the performances.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PerformanceDTO> findAll() {
        log.debug("Request to get all Performances");
        return performanceRepository.findAll().stream().map(performanceMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one performance by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PerformanceDTO> findOne(Long id) {
        log.debug("Request to get Performance : {}", id);
        return performanceRepository.findById(id).map(performanceMapper::toDto);
    }

    /**
     * Delete the performance by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Performance : {}", id);
        performanceRepository.deleteById(id);
    }
}
