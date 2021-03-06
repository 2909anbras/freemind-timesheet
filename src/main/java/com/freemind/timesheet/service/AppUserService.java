package com.freemind.timesheet.service;

import com.freemind.timesheet.domain.AppUser;
import com.freemind.timesheet.domain.Authority;
import com.freemind.timesheet.domain.User;
import com.freemind.timesheet.repository.AppUserRepository;
import com.freemind.timesheet.repository.CompanyRepository;
import com.freemind.timesheet.security.AuthoritiesConstants;
import com.freemind.timesheet.service.dto.AppUserDTO;
import com.freemind.timesheet.service.mapper.AppUserMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link AppUser}.
 */
@Service
@Transactional
public class AppUserService {
    private final Logger log = LoggerFactory.getLogger(AppUserService.class);

    private final AppUserRepository appUserRepository;
    private final CompanyRepository companyRepository;
    private final AppUserMapper appUserMapper;

    public AppUserService(AppUserRepository appUserRepository, CompanyRepository companyRepository, AppUserMapper appUserMapper) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
        this.companyRepository = companyRepository;
    }

    /**
     * Save a appUser.
     *
     * @param appUserDTO the entity to save.
     * @return the persisted entity.
     */
    @Transactional
    public AppUserDTO save(AppUserDTO appUserDTO) {
        log.debug("Request to save AppUser : {}", appUserDTO);
        AppUser appUser = appUserMapper.toEntity(appUserDTO);
        appUser = appUserRepository.save(appUser);
        return appUserMapper.toDto(appUser);
    }

    @Transactional
    public AppUserDTO update(AppUserDTO appUserDTO) {
        AppUser newUserExtra = appUserRepository.getOne(appUserDTO.getId());
        newUserExtra.setPhone(appUserDTO.getPhone());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        newUserExtra = appUserMapper.toEntity(appUserDTO);
        //        if (auth.getAuthorities().stream().anyMatch((r -> r.getAuthority().equals("ROLE_ADMIN"))))
        log.debug("Created Information for UserExtra: {}", newUserExtra);
        appUserRepository.save(newUserExtra);
        return appUserMapper.toDto(newUserExtra);
    }

    /**
     * Get all the appUsers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AppUserDTO> findAll(Pageable pageable) {
        log.debug("Request to get all AppUsers");
        return appUserRepository.findAll(pageable).map(appUserMapper::toDto);
    }

    ////    @Transactional(readOnly= true)
    //    public Page<AppUserDTO> findByCompany(Long id,Pageable pageable){//disparition
    //        log.debug("Request to get all AppUsers by company id", id);
    //        return  appUserRepository.findByCompany(id,pageable).map(appUserMapper::toDto);
    //    }
    //
    //  @Transactional(readOnly= true)
    //  public Page<AppUserDTO> findByCompanyBis(Long id,Pageable pageable){//disparition
    //      log.debug("Request to get all AppUsers by company id", id);
    //      return  appUserRepository.findByCompany(id,pageable).map(appUserMapper::toDto);
    //  }
    /**
     * Get all the appUsers with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<AppUserDTO> findAllWithEagerRelationships(Pageable pageable) {
        return appUserRepository.findAllWithEagerRelationships(pageable).map(appUserMapper::toDto);
    }

    /**
     * Get one appUser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AppUserDTO> findOne(Long id) {
        log.debug("Request to get AppUser : {}", id);
        return appUserRepository.findOneWithEagerRelationships(id).map(appUserMapper::toDto);
    }

    /**
     * Delete the appUser by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete AppUser : {}", id);
        appUserRepository.deleteById(id);
    }

    public List<Long> findIdsByCompany(Long companyId) {
        // TODO Auto-generated method stub
        return appUserRepository.findAllIdsByCompany(companyId);
    }
}
