package com.freemind.timesheet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.freemind.timesheet.FreemindTimesheetApp;
import com.freemind.timesheet.domain.Performance;
import com.freemind.timesheet.repository.PerformanceRepository;
import com.freemind.timesheet.service.PerformanceService;
import com.freemind.timesheet.service.dto.PerformanceDTO;
import com.freemind.timesheet.service.mapper.PerformanceMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PerformanceResource} REST controller.
 */
@SpringBootTest(classes = FreemindTimesheetApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class PerformanceResourceIT {
    private static final Integer DEFAULT_HOURS = 0;
    private static final Integer UPDATED_HOURS = 1;

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private PerformanceMapper performanceMapper;

    @Autowired
    private PerformanceService performanceService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPerformanceMockMvc;

    private Performance performance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Performance createEntity(EntityManager em) {
        Performance performance = new Performance().hours(DEFAULT_HOURS).date(DEFAULT_DATE);
        return performance;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Performance createUpdatedEntity(EntityManager em) {
        Performance performance = new Performance().hours(UPDATED_HOURS).date(UPDATED_DATE);
        return performance;
    }

    @BeforeEach
    public void initTest() {
        performance = createEntity(em);
    }

    @Test
    @Transactional
    public void createPerformance() throws Exception {
        int databaseSizeBeforeCreate = performanceRepository.findAll().size();
        // Create the Performance
        PerformanceDTO performanceDTO = performanceMapper.toDto(performance);
        restPerformanceMockMvc
            .perform(
                post("/api/performances").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(performanceDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Performance in the database
        List<Performance> performanceList = performanceRepository.findAll();
        assertThat(performanceList).hasSize(databaseSizeBeforeCreate + 1);
        Performance testPerformance = performanceList.get(performanceList.size() - 1);
        assertThat(testPerformance.getHours()).isEqualTo(DEFAULT_HOURS);
        assertThat(testPerformance.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    public void createPerformanceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = performanceRepository.findAll().size();

        // Create the Performance with an existing ID
        performance.setId(1L);
        PerformanceDTO performanceDTO = performanceMapper.toDto(performance);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPerformanceMockMvc
            .perform(
                post("/api/performances").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(performanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Performance in the database
        List<Performance> performanceList = performanceRepository.findAll();
        assertThat(performanceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkHoursIsRequired() throws Exception {
        int databaseSizeBeforeTest = performanceRepository.findAll().size();
        // set the field null
        performance.setHours(null);

        // Create the Performance, which fails.
        PerformanceDTO performanceDTO = performanceMapper.toDto(performance);

        restPerformanceMockMvc
            .perform(
                post("/api/performances").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(performanceDTO))
            )
            .andExpect(status().isBadRequest());

        List<Performance> performanceList = performanceRepository.findAll();
        assertThat(performanceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = performanceRepository.findAll().size();
        // set the field null
        performance.setDate(null);

        // Create the Performance, which fails.
        PerformanceDTO performanceDTO = performanceMapper.toDto(performance);

        restPerformanceMockMvc
            .perform(
                post("/api/performances").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(performanceDTO))
            )
            .andExpect(status().isBadRequest());

        List<Performance> performanceList = performanceRepository.findAll();
        assertThat(performanceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPerformances() throws Exception {
        // Initialize the database
        performanceRepository.saveAndFlush(performance);

        // Get all the performanceList
        restPerformanceMockMvc
            .perform(get("/api/performances?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(performance.getId().intValue())))
            .andExpect(jsonPath("$.[*].hours").value(hasItem(DEFAULT_HOURS)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    public void getPerformance() throws Exception {
        // Initialize the database
        performanceRepository.saveAndFlush(performance);

        // Get the performance
        restPerformanceMockMvc
            .perform(get("/api/performances/{id}", performance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(performance.getId().intValue()))
            .andExpect(jsonPath("$.hours").value(DEFAULT_HOURS))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPerformance() throws Exception {
        // Get the performance
        restPerformanceMockMvc.perform(get("/api/performances/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePerformance() throws Exception {
        // Initialize the database
        performanceRepository.saveAndFlush(performance);

        int databaseSizeBeforeUpdate = performanceRepository.findAll().size();

        // Update the performance
        Performance updatedPerformance = performanceRepository.findById(performance.getId()).get();
        // Disconnect from session so that the updates on updatedPerformance are not directly saved in db
        em.detach(updatedPerformance);
        updatedPerformance.hours(UPDATED_HOURS).date(UPDATED_DATE);
        PerformanceDTO performanceDTO = performanceMapper.toDto(updatedPerformance);

        restPerformanceMockMvc
            .perform(
                put("/api/performances").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(performanceDTO))
            )
            .andExpect(status().isOk());

        // Validate the Performance in the database
        List<Performance> performanceList = performanceRepository.findAll();
        assertThat(performanceList).hasSize(databaseSizeBeforeUpdate);
        Performance testPerformance = performanceList.get(performanceList.size() - 1);
        assertThat(testPerformance.getHours()).isEqualTo(UPDATED_HOURS);
        assertThat(testPerformance.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingPerformance() throws Exception {
        int databaseSizeBeforeUpdate = performanceRepository.findAll().size();

        // Create the Performance
        PerformanceDTO performanceDTO = performanceMapper.toDto(performance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPerformanceMockMvc
            .perform(
                put("/api/performances").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(performanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Performance in the database
        List<Performance> performanceList = performanceRepository.findAll();
        assertThat(performanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePerformance() throws Exception {
        // Initialize the database
        performanceRepository.saveAndFlush(performance);

        int databaseSizeBeforeDelete = performanceRepository.findAll().size();

        // Delete the performance
        restPerformanceMockMvc
            .perform(delete("/api/performances/{id}", performance.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Performance> performanceList = performanceRepository.findAll();
        assertThat(performanceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
