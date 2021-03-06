package com.freemind.timesheet.service;

import com.freemind.timesheet.config.Constants;
import com.freemind.timesheet.domain.AppUser;
import com.freemind.timesheet.domain.Authority;
import com.freemind.timesheet.domain.Company;
import com.freemind.timesheet.domain.Job;
import com.freemind.timesheet.domain.Performance;
import com.freemind.timesheet.domain.User;
import com.freemind.timesheet.repository.AppUserRepository;
import com.freemind.timesheet.repository.AuthorityRepository;
import com.freemind.timesheet.repository.CompanyRepository;
import com.freemind.timesheet.repository.JobRepository;
import com.freemind.timesheet.repository.UserRepository;
import com.freemind.timesheet.security.AuthoritiesConstants;
import com.freemind.timesheet.security.SecurityUtils;
import com.freemind.timesheet.service.dto.JobDTO;
import com.freemind.timesheet.service.dto.PerformanceDTO;
import com.freemind.timesheet.service.dto.UserDTO;
import com.freemind.timesheet.service.mapper.JobMapper;
import com.freemind.timesheet.service.mapper.PerformanceMapper;
import com.freemind.timesheet.web.rest.vm.ManagedUserVM;
import io.github.jhipster.security.RandomUtil;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final AppUserRepository appUserRepository;

    private final UserRepository userRepository;

    private final JobMapper jobMapper;

    private final JobRepository jobRepository;

    private final PerformanceMapper performanceMapper;

    private final CompanyRepository companyRepository;

    private final CompanyRepository companyService;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    private final CacheManager cacheManager;

    public UserService(
        JobRepository jobRepository,
        UserRepository userRepository,
        AppUserRepository appUserRepository,
        PasswordEncoder passwordEncoder,
        AuthorityRepository authorityRepository,
        CacheManager cacheManager,
        CompanyRepository companyService,
        CompanyRepository companyRepository,
        JobMapper jobMapper,
        PerformanceMapper performanceMapper
    ) {
        this.jobRepository = jobRepository;
        this.appUserRepository = appUserRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;
        this.companyService = companyService;
        this.companyRepository = companyRepository;
        this.jobMapper = jobMapper;
        this.performanceMapper = performanceMapper;
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository
            .findOneByActivationKey(key)
            .map(
                user -> {
                    // activate given user for the registration key.
                    user.setActivated(true);
                    user.setActivationKey(null);
                    this.clearUserCaches(user);
                    log.debug("Activated user: {}", user);
                    return user;
                }
            );
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository
            .findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400)))
            .map(
                user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetKey(null);
                    user.setResetDate(null);
                    this.clearUserCaches(user);
                    return user;
                }
            );
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository
            .findOneByEmailIgnoreCase(mail)
            .filter(User::getActivated)
            .map(
                user -> {
                    user.setResetKey(RandomUtil.generateResetKey());
                    user.setResetDate(Instant.now());
                    this.clearUserCaches(user);
                    return user;
                }
            );
    }

    public User registerUser(UserDTO userDTO, String password, String phone) {
        userRepository
            .findOneByLogin(userDTO.getLogin().toLowerCase())
            .ifPresent(
                existingUser -> {
                    boolean removed = removeNonActivatedUser(existingUser);
                    if (!removed) {
                        throw new UsernameAlreadyUsedException();
                    }
                }
            );
        userRepository
            .findOneByEmailIgnoreCase(userDTO.getEmail())
            .ifPresent(
                existingUser -> {
                    boolean removed = removeNonActivatedUser(existingUser);
                    if (!removed) {
                        throw new EmailAlreadyUsedException();
                    }
                }
            );
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getLogin().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey(userDTO.getLangKey());
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        this.clearUserCaches(newUser);
        log.debug("Created Information for User: {}", newUser);

        //create newAppUser
        AppUser newUserExtra = new AppUser();
        newUserExtra.setInternalUser(newUser);
        newUserExtra.setPhone(phone);
        //        newUserExtra.setId(newUser.getId());
        log.debug("Created Information for UserExtra: {}", newUser);

        appUserRepository.save(newUserExtra);
        return newUser;
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.getActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        this.clearUserCaches(existingUser);
        return true;
    }

    public User createUser(UserDTO userDTO, String phone, Long companyId, Set<JobDTO> jobs) {
        User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        Authority authUser = new Authority();
        authUser.setName("ROLE_USER");
        Authority authCust = new Authority();
        authCust.setName("ROLE_CUSTOMER_ADMIN");
        Authority authInspec = new Authority();
        authInspec.setName("ROLE_INSPECTOR");
        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO
                .getAuthorities()
                .stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

            log.debug("AUTHORITIES:{}", authorities);
            if (authorities.contains("ROLE_ADMIN")) {
                authorities.add(authUser);
                authorities.add(authInspec);
                authorities.add(authCust);
            } else if (authorities.contains("ROLE_CUSTOMER_ADMIN")) {
                authorities.add(authUser);
                authorities.add(authInspec);
            } else if (authorities.contains("ROLE_INSPECTOR")) {
                authorities.add(authUser);
            }
            user.setAuthorities(authorities);
        }
        userRepository.save(user);
        this.clearUserCaches(user);
        log.debug("Created Information for User: {}", user);

        AppUser newUserExtra = new AppUser();
        newUserExtra.setInternalUser(user);
        newUserExtra.setPhone(phone);
        newUserExtra.setJobs(jobs.stream().map(e -> jobMapper.toEntity(e)).collect(Collectors.toSet()));

        jobs
            .stream()
            .map(e -> jobMapper.toEntity(e))
            .collect(Collectors.toSet())
            .forEach(
                j -> {
                    Job tmp = jobRepository.findById(j.getId()).get();
                    tmp.addAppUser(newUserExtra);
                    jobRepository.save(tmp);
                }
            );
        Company c = companyRepository.getOne(companyId);
        newUserExtra.setCompany(c);
        c.addAppUser(newUserExtra);
        companyRepository.save(c);

        newUserExtra.setId(user.getId());
        log.debug("Created Information for UserExtra: {}", newUserExtra);

        appUserRepository.save(newUserExtra);

        return user;
    }

    private void setAuthorities() {}

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<UserDTO> updateUser(ManagedUserVM userDTO) {
        return Optional
            .of(userRepository.findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(
                user -> {
                    this.clearUserCaches(user);
                    user.setLogin(userDTO.getLogin().toLowerCase());
                    user.setFirstName(userDTO.getFirstName());
                    user.setLastName(userDTO.getLastName());
                    if (userDTO.getEmail() != null) {
                        user.setEmail(userDTO.getEmail().toLowerCase());
                    }
                    user.setImageUrl(userDTO.getImageUrl());
                    user.setActivated(userDTO.isActivated());
                    user.setLangKey(userDTO.getLangKey());
                    Set<Authority> managedAuthorities = user.getAuthorities();
                    managedAuthorities.clear();
                    userDTO
                        .getAuthorities()
                        .stream()
                        .map(authorityRepository::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(managedAuthorities::add);
                    this.clearUserCaches(user);
                    log.debug("Changed Information for User: {}", user);
                    //test
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    //                    if (!auth.getAuthorities().stream().anyMatch((r -> r.getAuthority().equals("ROLE_ADMIN")))) {    //cé fo
                    AppUser newUserExtra = appUserRepository.getOne(userDTO.getId());
                    newUserExtra.setInternalUser(user);
                    newUserExtra.setPhone(userDTO.getPhone());
                    Company c = companyRepository.getOne(userDTO.getCompanyId());
                    newUserExtra.setCompany(c);
                    c.addAppUser(newUserExtra);
                    companyRepository.save(c);

                    newUserExtra.setJobs(userDTO.getJobs().stream().map(jobMapper::toEntity).collect(Collectors.toSet()));

                    userDTO
                        .getJobs()
                        .stream()
                        .map(e -> jobMapper.toEntity(e))
                        .collect(Collectors.toSet())
                        .forEach(
                            j -> {
                                Job tmp = jobRepository.findById(j.getId()).get();
                                tmp.addAppUser(newUserExtra);
                                jobRepository.save(tmp);
                            }
                        );

                    //                    newUserExtra.setJobs(userDTO.getJobs().stream().map(e -> jobMapper.toEntity(e)).collect(Collectors.toSet()));
                    log.debug("#############################################################################", userDTO.getJobs());

                    log.debug("Created Information for UserExtra: {}", newUserExtra);
                    appUserRepository.save(newUserExtra);
                    //                    }

                    return user;
                }
            )
            .map(UserDTO::new);
    }

    public void deleteUser(String login) {
        userRepository
            .findOneByLogin(login)
            .ifPresent(
                user -> {
                    boolean canDelete = true;
                    AppUser appUser = appUserRepository.findById(user.getId()).get();
                    log.debug("AppUser to delete:{}", appUser);
                    if (appUser != null) {
                        canDelete = appUser.getPerformances().size() == 0;
                        log.debug("Can DELETE:{}", appUser);

                        if (canDelete) {
                            Company c = appUser.getCompany();
                            c.removeAppUser(appUser);
                            companyRepository.save(c);
                            for (Job j : appUser.getJobs()) {
                                j.removeAppUser(appUser);
                                jobRepository.save(j);
                            }
                            appUserRepository.delete(appUser);
                            log.debug("Deleted AppUser: {}", appUser);
                        }
                    }
                    if (canDelete) {
                        userRepository.delete(user);
                        this.clearUserCaches(user);
                        log.debug("Deleted User: {}", user);
                    }
                }
            );
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param langKey   language key.
     * @param imageUrl  image URL of user.
     */
    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(
                user -> {
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    if (email != null) {
                        user.setEmail(email.toLowerCase());
                    }
                    user.setLangKey(langKey);
                    user.setImageUrl(imageUrl);
                    this.clearUserCaches(user);
                    log.debug("Changed Information for User: {}", user);
                }
            );
    }

    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(
                user -> {
                    String currentEncryptedPassword = user.getPassword();
                    if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                        throw new InvalidPasswordException();
                    }
                    String encryptedPassword = passwordEncoder.encode(newPassword);
                    user.setPassword(encryptedPassword);
                    this.clearUserCaches(user);
                    log.debug("Changed password for User: {}", user);
                }
            );
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAllByLoginNot(pageable, Constants.ANONYMOUS_USER).map(UserDTO::new);
    }

    @Transactional
    public Page<UserDTO> getUsersByIds(List<Long> ids, Pageable pageable) { //ManagedUser
        return userRepository.findAllByIds(ids, pageable).map(UserDTO::new);
        //        return null;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public ManagedUserVM getUserWithAuthorities() { // {Optional<User>
        //    	UserDTO userDto= SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin).map(UserDTO::new).get();
        Optional<User> opt = SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
        UserDTO userDto = null;
        log.debug("optUser: {}", opt.get());

        if (opt.isPresent()) {
            userDto = new UserDTO(opt.get());
        }
        if (userDto != null) {
            ManagedUserVM user = new ManagedUserVM(userDto);
            Optional<AppUser> tmpU = this.appUserRepository.findById(user.getId());
            if (tmpU.isPresent()) user.setCompanyId(tmpU.get().getId());
            return user;
        } else return null;
        //        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
            .forEach(
                user -> {
                    boolean canDelete = true;
                    AppUser appUser = appUserRepository.findById(user.getId()).get();
                    if (appUser != null) {
                        canDelete = appUser.getPerformances().size() == 0;
                        if (canDelete) {
                            Company c = appUser.getCompany();
                            c.removeAppUser(appUser);
                            companyRepository.save(c);
                            for (Job j : appUser.getJobs()) {
                                j.removeAppUser(appUser);
                                jobRepository.save(j);
                            }
                            appUserRepository.delete(appUser);
                            log.debug("Deleted AppUser: {}", appUser);
                        }
                    }
                    if (canDelete) {
                        log.debug("Deleting not activated user {}", user.getLogin());
                        userRepository.delete(user);
                        this.clearUserCaches(user);
                    }
                }
            );
    }

    /**
     * Gets a list of all the authorities.
     * @return a list of all the authorities.
     */
    @Transactional(readOnly = true)
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }

    private void clearUserCaches(User user) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getLogin());
        if (user.getEmail() != null) {
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
        }
    }

    public Optional<ManagedUserVM> addPerformance(Long id, @Valid PerformanceDTO performanceDTO) {
        User user = userRepository.getOne(id);
        AppUser appUser = appUserRepository.findOneWithEagerRelationships(id).get(); //rajouter les performances dans query
        log.debug("add Performance: {}", appUser.getPerformances());
        appUser.addPerformance(performanceMapper.toEntity(performanceDTO));
        log.debug("Performance added: {}", appUser.getPerformances());
        appUserRepository.save(appUser);
        Set<PerformanceDTO> tmpList = new HashSet();
        ManagedUserVM userVM = new ManagedUserVM();
        userVM.setCompanyId(user.getId());
        userVM.setCompanyId(appUser.getCompany().getId());
        userVM.setLogin(user.getLogin());
        userVM.setFirstName(user.getFirstName());
        for (Performance p : appUser.getPerformances()) tmpList.add(performanceMapper.toDto(p));
        userVM.setPerformances(tmpList);
        Optional<ManagedUserVM> opt = Optional.empty();
        opt.of(userVM);
        return opt;
    }

    public List<ManagedUserVM> getAllByCompany(Long companyId) { //noms,performances,company,jobs,id
        log.debug("companyid : {}", companyId);

        List<AppUser> appUsers =
            this.appUserRepository.findByCompany(companyId, null, null)
                .getContent()
                .stream()
                .sorted((u1, u2) -> u1.getId().compareTo(u2.getId()))
                .collect(Collectors.toList());
        log.debug("APPUSERS BY COMPANY : {}", appUsers);
        List<User> users =
            this.userRepository.findAllByIds(appUsers.stream().map(t -> t.getId()).collect(Collectors.toList()), null)
                .getContent()
                .stream()
                .sorted((u1, u2) -> u1.getId().compareTo(u2.getId()))
                .collect(Collectors.toList());
        log.debug("USERS BY COMPANY : {}", users);

        List<ManagedUserVM> managedUsers = new ArrayList<ManagedUserVM>();
        for (int i = 0; i < appUsers.size(); i++) {
            User u = users.get(i);
            AppUser apu = appUsers.get(i);
            ManagedUserVM tmp = new ManagedUserVM();
            tmp.setId(apu.getId());
            tmp.setCompanyId(u.getId());
            tmp.setLogin(u.getLogin());
            tmp.setCompanyId(apu.getCompany().getId());
            tmp.setJobs(apu.getJobs().stream().map(jobMapper::toDto).collect(Collectors.toSet()));
            tmp.setPerformances(apu.getPerformances().stream().map(performanceMapper::toDto).collect(Collectors.toSet()));
            managedUsers.add(tmp);
        }

        log.debug("managedUsers :{}", managedUsers);

        return managedUsers;
    }
}
