package com.freemind.timesheet.repository;

import com.freemind.timesheet.domain.Company;
import com.freemind.timesheet.domain.Project;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Project entity.
 */
//@SuppressWarnings("unused")
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    ////	@Query("select project from Job job where project.jobs.id in ?1")
    //	Page<Project> findByJobsId(List<Long> jobsId, Specification specification,Pageable pageable);

    @Query("select distinct project from Project project left join project.jobs j where j.id =?1")
    Optional<Project> findProjectByJob(Long id);

    @Query("select distinct project from Project project left join project.jobs")
    Page<Project> findAllWithEagerRelationships(Specification specification, Pageable pageable);

    @Query("select project from Project project where project.id in ?1")
    Page<Project> findByIds(List<Long> projectsId, Specification<Project> specification, Pageable pageable);

    @Query("select project from Project project where project.customer.id in ?1")
    Page<Project> findByCustomersId(List<Long> customersId, Specification<Project> specification, Pageable pageable);

    @Query("select distinct job.project from Job job left join job.appUsers ap where ap.id IN ?1 ")
    List<Project> findProjectsByUserId(Long appUsersId, Pageable pageable);

    //to test
    @Query(
        "SELECT DISTINCT p FROM Project p " +
        "LEFT JOIN p.customer customer " +
        "LEFT JOIN customer.company company " +
        "LEFT JOIN company.appUsers ap " +
        "WHERE ap.id=?1 and customer.id=?2"
    )
    List<Project> findProjectsByCustomerAndUserId(Long userId, Long customerId);

    @Query(
        "SELECT DISTINCT p FROM Project p " +
        "LEFT JOIN p.customer customer " +
        "LEFT JOIN customer.company company " +
        "LEFT JOIN company.appUsers ap " +
        "LEFT JOIN customer.projects pro " +
        "WHERE ap.id=?1 and customer.id=?2 and pro.id in ?3"
    )
    List<Project> findProjectsByCustomerAndUserIdAndProjectIds(Long id, Long id2, Set<Long> projectsId);
}
