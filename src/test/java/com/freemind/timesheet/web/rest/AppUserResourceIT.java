package com.freemind.timesheet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.freemind.timesheet.FreemindTimesheetApp;
import com.freemind.timesheet.domain.AppUser;
import com.freemind.timesheet.domain.Company;
import com.freemind.timesheet.domain.Job;
import com.freemind.timesheet.domain.User;
import com.freemind.timesheet.repository.AppUserRepository;
import com.freemind.timesheet.service.AppUserQueryService;
import com.freemind.timesheet.service.AppUserService;
import com.freemind.timesheet.service.dto.AppUserCriteria;
import com.freemind.timesheet.service.dto.AppUserDTO;
import com.freemind.timesheet.service.mapper.AppUserMapper;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AppUserResource} REST controller.
 */
@SpringBootTest(classes = FreemindTimesheetApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class AppUserResourceIT {
    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    //    private static final String SMALLER_PHONE = 1 - 1;

    @Autowired
    private AppUserRepository appUserRepository;

    @Mock
    private AppUserRepository appUserRepositoryMock;

    @Autowired
    private AppUserMapper appUserMapper;

    @Mock
    private AppUserService appUserServiceMock;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserQueryService appUserQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAppUserMockMvc;

    private AppUser appUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createEntity(EntityManager em) {
        AppUser appUser = new AppUser().phone(DEFAULT_PHONE);
        return appUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createUpdatedEntity(EntityManager em) {
        AppUser appUser = new AppUser().phone(UPDATED_PHONE);
        return appUser;
    }

    @BeforeEach
    public void initTest() {
        appUser = createEntity(em);
    }

    @Test
    @Transactional
    public void createAppUser() throws Exception {
        int databaseSizeBeforeCreate = appUserRepository.findAll().size();
        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);
        restAppUserMockMvc
            .perform(post("/api/app-users").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appUserDTO)))
            .andExpect(status().isCreated());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeCreate + 1);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getPhone()).isEqualTo(DEFAULT_PHONE);
    }

    @Test
    @Transactional
    public void createAppUserWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = appUserRepository.findAll().size();

        // Create the AppUser with an existing ID
        appUser.setId(1L);
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppUserMockMvc
            .perform(post("/api/app-users").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appUserDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllAppUsers() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList
        restAppUserMockMvc
            .perform(get("/api/app-users?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)));
    }

    @SuppressWarnings({ "unchecked" })
    public void getAllAppUsersWithEagerRelationshipsIsEnabled() throws Exception {
        when(appUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAppUserMockMvc.perform(get("/api/app-users?eagerload=true")).andExpect(status().isOk());

        verify(appUserServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    public void getAllAppUsersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(appUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAppUserMockMvc.perform(get("/api/app-users?eagerload=true")).andExpect(status().isOk());

        verify(appUserServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getAppUser() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get the appUser
        restAppUserMockMvc
            .perform(get("/api/app-users/{id}", appUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appUser.getId().intValue()))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE));
    }

    @Test
    @Transactional
    public void getAppUsersByIdFiltering() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        Long id = appUser.getId();

        defaultAppUserShouldBeFound("id.equals=" + id);
        defaultAppUserShouldNotBeFound("id.notEquals=" + id);

        defaultAppUserShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAppUserShouldNotBeFound("id.greaterThan=" + id);

        defaultAppUserShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAppUserShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    public void getAllAppUsersByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phone equals to DEFAULT_PHONE
        defaultAppUserShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the appUserList where phone equals to UPDATED_PHONE
        defaultAppUserShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void getAllAppUsersByPhoneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phone not equals to DEFAULT_PHONE
        defaultAppUserShouldNotBeFound("phone.notEquals=" + DEFAULT_PHONE);

        // Get all the appUserList where phone not equals to UPDATED_PHONE
        defaultAppUserShouldBeFound("phone.notEquals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void getAllAppUsersByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultAppUserShouldBeFound("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE);

        // Get all the appUserList where phone equals to UPDATED_PHONE
        defaultAppUserShouldNotBeFound("phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void getAllAppUsersByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phone is not null
        defaultAppUserShouldBeFound("phone.specified=true");

        // Get all the appUserList where phone is null
        defaultAppUserShouldNotBeFound("phone.specified=false");
    }

    @Test
    @Transactional
    public void getAllAppUsersByPhoneIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phone is greater than or equal to DEFAULT_PHONE
        defaultAppUserShouldBeFound("phone.greaterThanOrEqual=" + DEFAULT_PHONE);

        // Get all the appUserList where phone is greater than or equal to UPDATED_PHONE
        defaultAppUserShouldNotBeFound("phone.greaterThanOrEqual=" + UPDATED_PHONE);
    }

    //    @Test
    //    @Transactional
    //    public void getAllAppUsersByPhoneIsLessThanOrEqualToSomething() throws Exception {
    //        // Initialize the database
    //        appUserRepository.saveAndFlush(appUser);
    //
    //        // Get all the appUserList where phone is less than or equal to DEFAULT_PHONE
    //        defaultAppUserShouldBeFound("phone.lessThanOrEqual=" + DEFAULT_PHONE);
    //
    //        // Get all the appUserList where phone is less than or equal to SMALLER_PHONE
    //        defaultAppUserShouldNotBeFound("phone.lessThanOrEqual=" + SMALLER_PHONE);
    //    }

    @Test
    @Transactional
    public void getAllAppUsersByPhoneIsLessThanSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phone is less than DEFAULT_PHONE
        defaultAppUserShouldNotBeFound("phone.lessThan=" + DEFAULT_PHONE);

        // Get all the appUserList where phone is less than UPDATED_PHONE
        defaultAppUserShouldBeFound("phone.lessThan=" + UPDATED_PHONE);
    }

    //    @Test
    //    @Transactional
    //    public void getAllAppUsersByPhoneIsGreaterThanSomething() throws Exception {
    //        // Initialize the database
    //        appUserRepository.saveAndFlush(appUser);
    //
    //        // Get all the appUserList where phone is greater than DEFAULT_PHONE
    //        defaultAppUserShouldNotBeFound("phone.greaterThan=" + DEFAULT_PHONE);
    //
    //        // Get all the appUserList where phone is greater than SMALLER_PHONE
    //        defaultAppUserShouldBeFound("phone.greaterThan=" + SMALLER_PHONE);
    //    }

    @Test
    @Transactional
    public void getAllAppUsersByInternalUserIsEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);
        User internalUser = UserResourceIT.createEntity(em);
        em.persist(internalUser);
        em.flush();
        appUser.setInternalUser(internalUser);
        appUserRepository.saveAndFlush(appUser);
        Long internalUserId = internalUser.getId();

        // Get all the appUserList where internalUser equals to internalUserId
        defaultAppUserShouldBeFound("internalUserId.equals=" + internalUserId);

        // Get all the appUserList where internalUser equals to internalUserId + 1
        defaultAppUserShouldNotBeFound("internalUserId.equals=" + (internalUserId + 1));
    }

    @Test
    @Transactional
    public void getAllAppUsersByJobIsEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);
        Job job = JobResourceIT.createEntity(em);
        em.persist(job);
        em.flush();
        appUser.addJob(job);
        appUserRepository.saveAndFlush(appUser);
        Long jobId = job.getId();

        // Get all the appUserList where job equals to jobId
        defaultAppUserShouldBeFound("jobId.equals=" + jobId);

        // Get all the appUserList where job equals to jobId + 1
        defaultAppUserShouldNotBeFound("jobId.equals=" + (jobId + 1));
    }

    @Test
    @Transactional
    public void getAllAppUsersByCompanyIsEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);
        Company company = CompanyResourceIT.createEntity(em);
        em.persist(company);
        em.flush();
        appUser.setCompany(company);
        appUserRepository.saveAndFlush(appUser);
        Long companyId = company.getId();

        // Get all the appUserList where company equals to companyId
        defaultAppUserShouldBeFound("companyId.equals=" + companyId);

        // Get all the appUserList where company equals to companyId + 1
        defaultAppUserShouldNotBeFound("companyId.equals=" + (companyId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAppUserShouldBeFound(String filter) throws Exception {
        restAppUserMockMvc
            .perform(get("/api/app-users?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)));

        // Check, that the count call also returns 1
        restAppUserMockMvc
            .perform(get("/api/app-users/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAppUserShouldNotBeFound(String filter) throws Exception {
        restAppUserMockMvc
            .perform(get("/api/app-users?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAppUserMockMvc
            .perform(get("/api/app-users/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingAppUser() throws Exception {
        // Get the appUser
        restAppUserMockMvc.perform(get("/api/app-users/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAppUser() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();

        // Update the appUser
        AppUser updatedAppUser = appUserRepository.findById(appUser.getId()).get();
        // Disconnect from session so that the updates on updatedAppUser are not directly saved in db
        em.detach(updatedAppUser);
        updatedAppUser.phone(UPDATED_PHONE);
        AppUserDTO appUserDTO = appUserMapper.toDto(updatedAppUser);

        restAppUserMockMvc
            .perform(put("/api/app-users").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appUserDTO)))
            .andExpect(status().isOk());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getPhone()).isEqualTo(UPDATED_PHONE);
    }

    @Test
    @Transactional
    public void updateNonExistingAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(put("/api/app-users").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appUserDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteAppUser() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        int databaseSizeBeforeDelete = appUserRepository.findAll().size();

        // Delete the appUser
        restAppUserMockMvc
            .perform(delete("/api/app-users/{id}", appUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
