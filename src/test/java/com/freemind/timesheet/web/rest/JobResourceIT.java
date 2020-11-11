package com.freemind.timesheet.web.rest;

import com.freemind.timesheet.FreemindTimesheetApp;
import com.freemind.timesheet.domain.Job;
import com.freemind.timesheet.domain.Project;
import com.freemind.timesheet.domain.AppUser;
import com.freemind.timesheet.repository.JobRepository;
import com.freemind.timesheet.service.JobService;
import com.freemind.timesheet.service.dto.JobDTO;
import com.freemind.timesheet.service.mapper.JobMapper;
import com.freemind.timesheet.service.dto.JobCriteria;
import com.freemind.timesheet.service.JobQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.freemind.timesheet.domain.enumeration.Status;
/**
 * Integration tests for the {@link JobResource} REST controller.
 */
@SpringBootTest(classes = FreemindTimesheetApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class JobResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBBBBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.ACTIVE;
    private static final Status UPDATED_STATUS = Status.NOT_ACTIVE;

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_END_DATE = LocalDate.ofEpochDay(-1L);

    private static final Boolean DEFAULT_ENABLE = false;
    private static final Boolean UPDATED_ENABLE = true;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private JobService jobService;

    @Autowired
    private JobQueryService jobQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restJobMockMvc;

    private Job job;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Job createEntity(EntityManager em) {
        Job job = new Job()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .enable(DEFAULT_ENABLE);
        return job;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Job createUpdatedEntity(EntityManager em) {
        Job job = new Job()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .enable(UPDATED_ENABLE);
        return job;
    }

    @BeforeEach
    public void initTest() {
        job = createEntity(em);
    }

    @Test
    @Transactional
    public void createJob() throws Exception {
        int databaseSizeBeforeCreate = jobRepository.findAll().size();
        // Create the Job
        JobDTO jobDTO = jobMapper.toDto(job);
        restJobMockMvc.perform(post("/api/jobs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(jobDTO)))
            .andExpect(status().isCreated());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeCreate + 1);
        Job testJob = jobList.get(jobList.size() - 1);
        assertThat(testJob.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testJob.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testJob.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testJob.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testJob.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testJob.isEnable()).isEqualTo(DEFAULT_ENABLE);
    }

    @Test
    @Transactional
    public void createJobWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = jobRepository.findAll().size();

        // Create the Job with an existing ID
        job.setId(1L);
        JobDTO jobDTO = jobMapper.toDto(job);

        // An entity with an existing ID cannot be created, so this API call must fail
        restJobMockMvc.perform(post("/api/jobs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(jobDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobRepository.findAll().size();
        // set the field null
        job.setName(null);

        // Create the Job, which fails.
        JobDTO jobDTO = jobMapper.toDto(job);


        restJobMockMvc.perform(post("/api/jobs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(jobDTO)))
            .andExpect(status().isBadRequest());

        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllJobs() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList
        restJobMockMvc.perform(get("/api/jobs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(job.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getJob() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get the job
        restJobMockMvc.perform(get("/api/jobs/{id}", job.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(job.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.enable").value(DEFAULT_ENABLE.booleanValue()));
    }


    @Test
    @Transactional
    public void getJobsByIdFiltering() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        Long id = job.getId();

        defaultJobShouldBeFound("id.equals=" + id);
        defaultJobShouldNotBeFound("id.notEquals=" + id);

        defaultJobShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultJobShouldNotBeFound("id.greaterThan=" + id);

        defaultJobShouldBeFound("id.lessThanOrEqual=" + id);
        defaultJobShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllJobsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where name equals to DEFAULT_NAME
        defaultJobShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the jobList where name equals to UPDATED_NAME
        defaultJobShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllJobsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where name not equals to DEFAULT_NAME
        defaultJobShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the jobList where name not equals to UPDATED_NAME
        defaultJobShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllJobsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where name in DEFAULT_NAME or UPDATED_NAME
        defaultJobShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the jobList where name equals to UPDATED_NAME
        defaultJobShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllJobsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where name is not null
        defaultJobShouldBeFound("name.specified=true");

        // Get all the jobList where name is null
        defaultJobShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllJobsByNameContainsSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where name contains DEFAULT_NAME
        defaultJobShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the jobList where name contains UPDATED_NAME
        defaultJobShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllJobsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where name does not contain DEFAULT_NAME
        defaultJobShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the jobList where name does not contain UPDATED_NAME
        defaultJobShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllJobsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where description equals to DEFAULT_DESCRIPTION
        defaultJobShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the jobList where description equals to UPDATED_DESCRIPTION
        defaultJobShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllJobsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where description not equals to DEFAULT_DESCRIPTION
        defaultJobShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the jobList where description not equals to UPDATED_DESCRIPTION
        defaultJobShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllJobsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultJobShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the jobList where description equals to UPDATED_DESCRIPTION
        defaultJobShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllJobsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where description is not null
        defaultJobShouldBeFound("description.specified=true");

        // Get all the jobList where description is null
        defaultJobShouldNotBeFound("description.specified=false");
    }
                @Test
    @Transactional
    public void getAllJobsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where description contains DEFAULT_DESCRIPTION
        defaultJobShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the jobList where description contains UPDATED_DESCRIPTION
        defaultJobShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllJobsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where description does not contain DEFAULT_DESCRIPTION
        defaultJobShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the jobList where description does not contain UPDATED_DESCRIPTION
        defaultJobShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }


    @Test
    @Transactional
    public void getAllJobsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where status equals to DEFAULT_STATUS
        defaultJobShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the jobList where status equals to UPDATED_STATUS
        defaultJobShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllJobsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where status not equals to DEFAULT_STATUS
        defaultJobShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the jobList where status not equals to UPDATED_STATUS
        defaultJobShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllJobsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultJobShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the jobList where status equals to UPDATED_STATUS
        defaultJobShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllJobsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where status is not null
        defaultJobShouldBeFound("status.specified=true");

        // Get all the jobList where status is null
        defaultJobShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllJobsByStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where startDate equals to DEFAULT_START_DATE
        defaultJobShouldBeFound("startDate.equals=" + DEFAULT_START_DATE);

        // Get all the jobList where startDate equals to UPDATED_START_DATE
        defaultJobShouldNotBeFound("startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllJobsByStartDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where startDate not equals to DEFAULT_START_DATE
        defaultJobShouldNotBeFound("startDate.notEquals=" + DEFAULT_START_DATE);

        // Get all the jobList where startDate not equals to UPDATED_START_DATE
        defaultJobShouldBeFound("startDate.notEquals=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllJobsByStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where startDate in DEFAULT_START_DATE or UPDATED_START_DATE
        defaultJobShouldBeFound("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE);

        // Get all the jobList where startDate equals to UPDATED_START_DATE
        defaultJobShouldNotBeFound("startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllJobsByStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where startDate is not null
        defaultJobShouldBeFound("startDate.specified=true");

        // Get all the jobList where startDate is null
        defaultJobShouldNotBeFound("startDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllJobsByStartDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where startDate is greater than or equal to DEFAULT_START_DATE
        defaultJobShouldBeFound("startDate.greaterThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the jobList where startDate is greater than or equal to UPDATED_START_DATE
        defaultJobShouldNotBeFound("startDate.greaterThanOrEqual=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllJobsByStartDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where startDate is less than or equal to DEFAULT_START_DATE
        defaultJobShouldBeFound("startDate.lessThanOrEqual=" + DEFAULT_START_DATE);

        // Get all the jobList where startDate is less than or equal to SMALLER_START_DATE
        defaultJobShouldNotBeFound("startDate.lessThanOrEqual=" + SMALLER_START_DATE);
    }

    @Test
    @Transactional
    public void getAllJobsByStartDateIsLessThanSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where startDate is less than DEFAULT_START_DATE
        defaultJobShouldNotBeFound("startDate.lessThan=" + DEFAULT_START_DATE);

        // Get all the jobList where startDate is less than UPDATED_START_DATE
        defaultJobShouldBeFound("startDate.lessThan=" + UPDATED_START_DATE);
    }

    @Test
    @Transactional
    public void getAllJobsByStartDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where startDate is greater than DEFAULT_START_DATE
        defaultJobShouldNotBeFound("startDate.greaterThan=" + DEFAULT_START_DATE);

        // Get all the jobList where startDate is greater than SMALLER_START_DATE
        defaultJobShouldBeFound("startDate.greaterThan=" + SMALLER_START_DATE);
    }


    @Test
    @Transactional
    public void getAllJobsByEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where endDate equals to DEFAULT_END_DATE
        defaultJobShouldBeFound("endDate.equals=" + DEFAULT_END_DATE);

        // Get all the jobList where endDate equals to UPDATED_END_DATE
        defaultJobShouldNotBeFound("endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllJobsByEndDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where endDate not equals to DEFAULT_END_DATE
        defaultJobShouldNotBeFound("endDate.notEquals=" + DEFAULT_END_DATE);

        // Get all the jobList where endDate not equals to UPDATED_END_DATE
        defaultJobShouldBeFound("endDate.notEquals=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllJobsByEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where endDate in DEFAULT_END_DATE or UPDATED_END_DATE
        defaultJobShouldBeFound("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE);

        // Get all the jobList where endDate equals to UPDATED_END_DATE
        defaultJobShouldNotBeFound("endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllJobsByEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where endDate is not null
        defaultJobShouldBeFound("endDate.specified=true");

        // Get all the jobList where endDate is null
        defaultJobShouldNotBeFound("endDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllJobsByEndDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where endDate is greater than or equal to DEFAULT_END_DATE
        defaultJobShouldBeFound("endDate.greaterThanOrEqual=" + DEFAULT_END_DATE);

        // Get all the jobList where endDate is greater than or equal to UPDATED_END_DATE
        defaultJobShouldNotBeFound("endDate.greaterThanOrEqual=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllJobsByEndDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where endDate is less than or equal to DEFAULT_END_DATE
        defaultJobShouldBeFound("endDate.lessThanOrEqual=" + DEFAULT_END_DATE);

        // Get all the jobList where endDate is less than or equal to SMALLER_END_DATE
        defaultJobShouldNotBeFound("endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    @Transactional
    public void getAllJobsByEndDateIsLessThanSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where endDate is less than DEFAULT_END_DATE
        defaultJobShouldNotBeFound("endDate.lessThan=" + DEFAULT_END_DATE);

        // Get all the jobList where endDate is less than UPDATED_END_DATE
        defaultJobShouldBeFound("endDate.lessThan=" + UPDATED_END_DATE);
    }

    @Test
    @Transactional
    public void getAllJobsByEndDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where endDate is greater than DEFAULT_END_DATE
        defaultJobShouldNotBeFound("endDate.greaterThan=" + DEFAULT_END_DATE);

        // Get all the jobList where endDate is greater than SMALLER_END_DATE
        defaultJobShouldBeFound("endDate.greaterThan=" + SMALLER_END_DATE);
    }


    @Test
    @Transactional
    public void getAllJobsByEnableIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where enable equals to DEFAULT_ENABLE
        defaultJobShouldBeFound("enable.equals=" + DEFAULT_ENABLE);

        // Get all the jobList where enable equals to UPDATED_ENABLE
        defaultJobShouldNotBeFound("enable.equals=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    public void getAllJobsByEnableIsNotEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where enable not equals to DEFAULT_ENABLE
        defaultJobShouldNotBeFound("enable.notEquals=" + DEFAULT_ENABLE);

        // Get all the jobList where enable not equals to UPDATED_ENABLE
        defaultJobShouldBeFound("enable.notEquals=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    public void getAllJobsByEnableIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where enable in DEFAULT_ENABLE or UPDATED_ENABLE
        defaultJobShouldBeFound("enable.in=" + DEFAULT_ENABLE + "," + UPDATED_ENABLE);

        // Get all the jobList where enable equals to UPDATED_ENABLE
        defaultJobShouldNotBeFound("enable.in=" + UPDATED_ENABLE);
    }

    @Test
    @Transactional
    public void getAllJobsByEnableIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where enable is not null
        defaultJobShouldBeFound("enable.specified=true");

        // Get all the jobList where enable is null
        defaultJobShouldNotBeFound("enable.specified=false");
    }

    @Test
    @Transactional
    public void getAllJobsByProjectIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);
        Project project = ProjectResourceIT.createEntity(em);
        em.persist(project);
        em.flush();
        job.setProject(project);
        jobRepository.saveAndFlush(job);
        Long projectId = project.getId();

        // Get all the jobList where project equals to projectId
        defaultJobShouldBeFound("projectId.equals=" + projectId);

        // Get all the jobList where project equals to projectId + 1
        defaultJobShouldNotBeFound("projectId.equals=" + (projectId + 1));
    }


    @Test
    @Transactional
    public void getAllJobsByAppUserIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);
        AppUser appUser = AppUserResourceIT.createEntity(em);
        em.persist(appUser);
        em.flush();
        job.addAppUser(appUser);
        jobRepository.saveAndFlush(job);
        Long appUserId = appUser.getId();

        // Get all the jobList where appUser equals to appUserId
        defaultJobShouldBeFound("appUserId.equals=" + appUserId);

        // Get all the jobList where appUser equals to appUserId + 1
        defaultJobShouldNotBeFound("appUserId.equals=" + (appUserId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultJobShouldBeFound(String filter) throws Exception {
        restJobMockMvc.perform(get("/api/jobs?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(job.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE.booleanValue())));

        // Check, that the count call also returns 1
        restJobMockMvc.perform(get("/api/jobs/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultJobShouldNotBeFound(String filter) throws Exception {
        restJobMockMvc.perform(get("/api/jobs?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restJobMockMvc.perform(get("/api/jobs/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingJob() throws Exception {
        // Get the job
        restJobMockMvc.perform(get("/api/jobs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJob() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        int databaseSizeBeforeUpdate = jobRepository.findAll().size();

        // Update the job
        Job updatedJob = jobRepository.findById(job.getId()).get();
        // Disconnect from session so that the updates on updatedJob are not directly saved in db
        em.detach(updatedJob);
        updatedJob
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .enable(UPDATED_ENABLE);
        JobDTO jobDTO = jobMapper.toDto(updatedJob);

        restJobMockMvc.perform(put("/api/jobs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(jobDTO)))
            .andExpect(status().isOk());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeUpdate);
        Job testJob = jobList.get(jobList.size() - 1);
        assertThat(testJob.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testJob.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testJob.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testJob.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testJob.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testJob.isEnable()).isEqualTo(UPDATED_ENABLE);
    }

    @Test
    @Transactional
    public void updateNonExistingJob() throws Exception {
        int databaseSizeBeforeUpdate = jobRepository.findAll().size();

        // Create the Job
        JobDTO jobDTO = jobMapper.toDto(job);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJobMockMvc.perform(put("/api/jobs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(jobDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteJob() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        int databaseSizeBeforeDelete = jobRepository.findAll().size();

        // Delete the job
        restJobMockMvc.perform(delete("/api/jobs/{id}", job.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
