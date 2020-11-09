package com.freemind.timesheet.web.rest;

import com.freemind.timesheet.domain.ToolUser;
import com.freemind.timesheet.repository.ToolUserRepository;
import com.freemind.timesheet.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link com.freemind.timesheet.domain.ToolUser}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ToolUserResource {
    private final Logger log = LoggerFactory.getLogger(ToolUserResource.class);

    private static final String ENTITY_NAME = "toolUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ToolUserRepository toolUserRepository;

    public ToolUserResource(ToolUserRepository toolUserRepository) {
        this.toolUserRepository = toolUserRepository;
    }

    /**
     * {@code POST  /tool-users} : Create a new toolUser.
     *
     * @param toolUser the toolUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new toolUser, or with status {@code 400 (Bad Request)} if the toolUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tool-users")
    public ResponseEntity<ToolUser> createToolUser(@Valid @RequestBody ToolUser toolUser) throws URISyntaxException {
        log.debug("REST request to save ToolUser : {}", toolUser);
        if (toolUser.getId() != null) {
            throw new BadRequestAlertException("A new toolUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ToolUser result = toolUserRepository.save(toolUser);
        return ResponseEntity
            .created(new URI("/api/tool-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tool-users} : Updates an existing toolUser.
     *
     * @param toolUser the toolUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated toolUser,
     * or with status {@code 400 (Bad Request)} if the toolUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the toolUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tool-users")
    public ResponseEntity<ToolUser> updateToolUser(@Valid @RequestBody ToolUser toolUser) throws URISyntaxException {
        log.debug("REST request to update ToolUser : {}", toolUser);
        if (toolUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ToolUser result = toolUserRepository.save(toolUser);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, toolUser.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /tool-users} : get all the toolUsers.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of toolUsers in body.
     */
    @GetMapping("/tool-users")
    public List<ToolUser> getAllToolUsers(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all ToolUsers");
        return toolUserRepository.findAllWithEagerRelationships();
    }

    /**
     * {@code GET  /tool-users/:id} : get the "id" toolUser.
     *
     * @param id the id of the toolUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the toolUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tool-users/{id}")
    public ResponseEntity<ToolUser> getToolUser(@PathVariable Long id) {
        log.debug("REST request to get ToolUser : {}", id);
        Optional<ToolUser> toolUser = toolUserRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(toolUser);
    }

    /**
     * {@code DELETE  /tool-users/:id} : delete the "id" toolUser.
     *
     * @param id the id of the toolUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tool-users/{id}")
    public ResponseEntity<Void> deleteToolUser(@PathVariable Long id) {
        log.debug("REST request to delete ToolUser : {}", id);
        toolUserRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
