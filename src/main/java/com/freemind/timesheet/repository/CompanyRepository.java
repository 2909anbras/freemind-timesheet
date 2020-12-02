package com.freemind.timesheet.repository;

import com.freemind.timesheet.domain.Company;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Company entity.
 */
//@SuppressWarnings("unused")
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
    @Query("select distinct company from Company company left join company.customers")
    Page<Company> findAllWithEagerRelationships(Specification specification, Pageable pageable);
}
