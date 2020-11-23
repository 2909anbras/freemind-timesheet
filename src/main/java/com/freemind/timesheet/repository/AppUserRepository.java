package com.freemind.timesheet.repository;

import com.freemind.timesheet.domain.AppUser;
import com.freemind.timesheet.domain.Customer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the AppUser entity.
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long>, JpaSpecificationExecutor<AppUser> {
    @Query(
        value = "select distinct appUser from AppUser appUser left join fetch appUser.jobs",
        countQuery = "select count(distinct appUser) from AppUser appUser"
    )
    Page<AppUser> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct appUser from AppUser appUser left join fetch appUser.jobs")
    List<AppUser> findAllWithEagerRelationships();

    @Query("select appUser from AppUser appUser left join fetch appUser.jobs where appUser.id =:id")
    Optional<AppUser> findOneWithEagerRelationships(@Param("id") Long id);

    @Query("select appUser from AppUser appUser where appUser.company.id=?1")
    Page<AppUser> findByCompany(Long id, Specification<AppUser> specification, Pageable pageable);
}
