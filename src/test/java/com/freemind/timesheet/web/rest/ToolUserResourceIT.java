package com.freemind.timesheet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.freemind.timesheet.FreemindTimesheetApp;
import com.freemind.timesheet.domain.ToolUser;
import com.freemind.timesheet.domain.enumeration.Language;
import com.freemind.timesheet.repository.ToolUserRepository;
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
 * Integration tests for the {@link ToolUserResource} REST controller.
 */
@SpringBootTest(classes = FreemindTimesheetApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class ToolUserResourceIT {
    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "w\\[&?\\\\i+|\\1&`|w`\\a%}|\\o/w?w@x\\=QB\\^YcX";
    private static final String UPDATED_EMAIL = "$#{|\\}\\0-+\\w#`\\>|\\q$\\4`-\\q!=&@K\\[BdcYT";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ENABLE = false;
    private static final Boolean UPDATED_ENABLE = true;

    private static final Language DEFAULT_LANGUAGE = Language.FRENCH;
    private static final Language UPDATED_LANGUAGE = Language.ENGLISH;

    @Autowired
    private ToolUserRepository toolUserRepository;

    @Mock
    private ToolUserRepository toolUserRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restToolUserMockMvc;

    private ToolUser toolUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ToolUser createEntity(EntityManager em) {
        ToolUser toolUser = new ToolUser()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .password(DEFAULT_PASSWORD)
            .enable(DEFAULT_ENABLE)
            .language(DEFAULT_LANGUAGE);
        return toolUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ToolUser createUpdatedEntity(EntityManager em) {
        ToolUser toolUser = new ToolUser()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD)
            .enable(UPDATED_ENABLE)
            .language(UPDATED_LANGUAGE);
        return toolUser;
    }

    @BeforeEach
    public void initTest() {
        toolUser = createEntity(em);
    }

    @Test
    @Transactional
    public void createToolUser() throws Exception {
        int databaseSizeBeforeCreate = toolUserRepository.findAll().size();
        // Create the ToolUser
        restToolUserMockMvc
            .perform(post("/api/tool-users").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(toolUser)))
            .andExpect(status().isCreated());

        // Validate the ToolUser in the database
        List<ToolUser> toolUserList = toolUserRepository.findAll();
        assertThat(toolUserList).hasSize(databaseSizeBeforeCreate + 1);
        ToolUser testToolUser = toolUserList.get(toolUserList.size() - 1);
        assertThat(testToolUser.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testToolUser.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testToolUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testToolUser.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testToolUser.isEnable()).isEqualTo(DEFAULT_ENABLE);
        assertThat(testToolUser.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
    }

    @Test
    @Transactional
    public void createToolUserWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = toolUserRepository.findAll().size();

        // Create the ToolUser with an existing ID
        toolUser.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restToolUserMockMvc
            .perform(post("/api/tool-users").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(toolUser)))
            .andExpect(status().isBadRequest());

        // Validate the ToolUser in the database
        List<ToolUser> toolUserList = toolUserRepository.findAll();
        assertThat(toolUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = toolUserRepository.findAll().size();
        // set the field null
        toolUser.setFirstName(null);

        // Create the ToolUser, which fails.

        restToolUserMockMvc
            .perform(post("/api/tool-users").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(toolUser)))
            .andExpect(status().isBadRequest());

        List<ToolUser> toolUserList = toolUserRepository.findAll();
        assertThat(toolUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = toolUserRepository.findAll().size();
        // set the field null
        toolUser.setLastName(null);

        // Create the ToolUser, which fails.

        restToolUserMockMvc
            .perform(post("/api/tool-users").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(toolUser)))
            .andExpect(status().isBadRequest());

        List<ToolUser> toolUserList = toolUserRepository.findAll();
        assertThat(toolUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = toolUserRepository.findAll().size();
        // set the field null
        toolUser.setEmail(null);

        // Create the ToolUser, which fails.

        restToolUserMockMvc
            .perform(post("/api/tool-users").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(toolUser)))
            .andExpect(status().isBadRequest());

        List<ToolUser> toolUserList = toolUserRepository.findAll();
        assertThat(toolUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPasswordIsRequired() throws Exception {
        int databaseSizeBeforeTest = toolUserRepository.findAll().size();
        // set the field null
        toolUser.setPassword(null);

        // Create the ToolUser, which fails.

        restToolUserMockMvc
            .perform(post("/api/tool-users").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(toolUser)))
            .andExpect(status().isBadRequest());

        List<ToolUser> toolUserList = toolUserRepository.findAll();
        assertThat(toolUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEnableIsRequired() throws Exception {
        int databaseSizeBeforeTest = toolUserRepository.findAll().size();
        // set the field null
        toolUser.setEnable(null);

        // Create the ToolUser, which fails.

        restToolUserMockMvc
            .perform(post("/api/tool-users").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(toolUser)))
            .andExpect(status().isBadRequest());

        List<ToolUser> toolUserList = toolUserRepository.findAll();
        assertThat(toolUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllToolUsers() throws Exception {
        // Initialize the database
        toolUserRepository.saveAndFlush(toolUser);

        // Get all the toolUserList
        restToolUserMockMvc
            .perform(get("/api/tool-users?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(toolUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].enable").value(hasItem(DEFAULT_ENABLE.booleanValue())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    public void getAllToolUsersWithEagerRelationshipsIsEnabled() throws Exception {
        when(toolUserRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restToolUserMockMvc.perform(get("/api/tool-users?eagerload=true")).andExpect(status().isOk());

        verify(toolUserRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    public void getAllToolUsersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(toolUserRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restToolUserMockMvc.perform(get("/api/tool-users?eagerload=true")).andExpect(status().isOk());

        verify(toolUserRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getToolUser() throws Exception {
        // Initialize the database
        toolUserRepository.saveAndFlush(toolUser);

        // Get the toolUser
        restToolUserMockMvc
            .perform(get("/api/tool-users/{id}", toolUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(toolUser.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD))
            .andExpect(jsonPath("$.enable").value(DEFAULT_ENABLE.booleanValue()))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingToolUser() throws Exception {
        // Get the toolUser
        restToolUserMockMvc.perform(get("/api/tool-users/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateToolUser() throws Exception {
        // Initialize the database
        toolUserRepository.saveAndFlush(toolUser);

        int databaseSizeBeforeUpdate = toolUserRepository.findAll().size();

        // Update the toolUser
        ToolUser updatedToolUser = toolUserRepository.findById(toolUser.getId()).get();
        // Disconnect from session so that the updates on updatedToolUser are not directly saved in db
        em.detach(updatedToolUser);
        updatedToolUser
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD)
            .enable(UPDATED_ENABLE)
            .language(UPDATED_LANGUAGE);

        restToolUserMockMvc
            .perform(
                put("/api/tool-users").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(updatedToolUser))
            )
            .andExpect(status().isOk());

        // Validate the ToolUser in the database
        List<ToolUser> toolUserList = toolUserRepository.findAll();
        assertThat(toolUserList).hasSize(databaseSizeBeforeUpdate);
        ToolUser testToolUser = toolUserList.get(toolUserList.size() - 1);
        assertThat(testToolUser.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testToolUser.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testToolUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testToolUser.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testToolUser.isEnable()).isEqualTo(UPDATED_ENABLE);
        assertThat(testToolUser.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    public void updateNonExistingToolUser() throws Exception {
        int databaseSizeBeforeUpdate = toolUserRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restToolUserMockMvc
            .perform(put("/api/tool-users").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(toolUser)))
            .andExpect(status().isBadRequest());

        // Validate the ToolUser in the database
        List<ToolUser> toolUserList = toolUserRepository.findAll();
        assertThat(toolUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteToolUser() throws Exception {
        // Initialize the database
        toolUserRepository.saveAndFlush(toolUser);

        int databaseSizeBeforeDelete = toolUserRepository.findAll().size();

        // Delete the toolUser
        restToolUserMockMvc
            .perform(delete("/api/tool-users/{id}", toolUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ToolUser> toolUserList = toolUserRepository.findAll();
        assertThat(toolUserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
