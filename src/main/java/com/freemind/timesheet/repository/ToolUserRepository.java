package com.freemind.timesheet.repository;

import com.freemind.timesheet.domain.ToolUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the ToolUser entity.
 */
@Repository
public interface ToolUserRepository extends JpaRepository<ToolUser, Long> {
    @Query(
        value = "select distinct toolUser from ToolUser toolUser left join fetch toolUser.jobs",
        countQuery = "select count(distinct toolUser) from ToolUser toolUser"
    )
    Page<ToolUser> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct toolUser from ToolUser toolUser left join fetch toolUser.jobs")
    List<ToolUser> findAllWithEagerRelationships();

    @Query("select toolUser from ToolUser toolUser left join fetch toolUser.jobs where toolUser.id =:id")
    Optional<ToolUser> findOneWithEagerRelationships(@Param("id") Long id);
}
