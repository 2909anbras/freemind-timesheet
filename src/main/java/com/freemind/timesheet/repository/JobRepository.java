package com.freemind.timesheet.repository;

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
    //lets try
    @Query("select distinct job.project.id from Job job left join job.appUsers where job.appUsers In ?1 ")
    List<Long> findProjectsByUsersId(List<Long> appUsersId);

    @Query("select distinct job from Job job left join job.appUsers where job.appUsers.companyId = ?1 ")
    Page<JobDTO> findByCompany(Long companyId, Specification specification, Pageable pageable);
}
