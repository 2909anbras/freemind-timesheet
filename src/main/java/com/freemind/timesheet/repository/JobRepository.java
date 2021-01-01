package com.freemind.timesheet.repository;

import com.freemind.timesheet.domain.AppUser;
import com.freemind.timesheet.domain.Job;
import com.freemind.timesheet.domain.Project;
import com.freemind.timesheet.service.dto.JobCriteria;
import com.freemind.timesheet.service.dto.JobDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Job entity.
 */
//@SuppressWarnings("unused")
@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    @Query("select distinct job.project.id from Job job left join job.appUsers where job.appUsers In ?1 ")
    List<Long> findProjectsByUsersId(List<Long> appUsersId);

    @Query("select  appUser.jobs from AppUser appUser left join appUser.jobs where appUser.company.id = ?1 ")
    Page<Job> findJobsIdByCompany(Long companyId, Specification<Job> specification, Pageable pageable);

    @Query("select distinct j from Job j left join j.project p left join p.customer c where c.company.id=?1")
    Page<Job> findJobsByCompany(Long companyId, Pageable pageable);

    @Query("select distinct job from Job job left join job.appUsers ap where ap.id IN ?1 ")
    Page<Job> findByJobsId(List<Long> appUsersId, Specification specification, Pageable pageable);

    @Query("select distinct job from Job job left join job.appUsers ap where ap.id=?1")
    Page<Job> findByUserId(Long id, Pageable pageable);

    @Query("select distinct job from Job job left join job.performances")
    Page<Job> findAllWithEagearRelationship(Specification specification, Pageable pageable);

    @Query("select distinct job from Job job left join job.project p where p =?1")
    List<Job> getJobByProject(Long id);
}
