package com.freemind.timesheet.repository;

import com.freemind.timesheet.domain.Company;
import com.freemind.timesheet.domain.Customer;
import com.freemind.timesheet.domain.Project;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Customer entity.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    @Query(
        "SELECT DISTINCT c FROM Customer c " +
        "LEFT JOIN c.projects projects " +
        "LEFT JOIN projects.jobs jobs " +
        "LEFT JOIN jobs.appUsers ap " +
        "WHERE ap.id=?1 and ap.company.id=c.company.id"
    )
    Page<Customer> findCustomersByUserId(Long appUsersId, Long currentCompanyId, Pageable pageable);

    @Query(
        "SELECT DISTINCT c FROM Customer c " +
        "LEFT JOIN c.projects projects " +
        "LEFT JOIN projects.jobs jobs " +
        "LEFT JOIN jobs.appUsers ap " +
        "WHERE ap.id=?1 and ap.company.id=c.company.id"
    )
    List<Customer> findCustomersByUserId(Long appUsersId, Long currentCompanyId);

    //    @Query("select distinct job.project.customer from Job job left join job.appUsers ap where ap=?1 ")
    //    List<Customer> findCustomersByUserId(Long appUsersId);

    @Query("select distinct customer from Customer customer left join customer.projects")
    Page<Customer> findAllWithEagerRelationships(Specification specification, Pageable pageable);

    @Query("select distinct customer.id from Customer customer where customer.company.id= ?1 ")
    List<Long> findCustomersIdByCompany(Long id);

    @Query("select distinct customer from Customer customer where customer.company.id=?1")
    Page findByCompany(Long id, Specification<Customer> specification, Pageable page);

    @Query("select customer from Customer customer where customer.id in ?1")
    Page<Customer> findByIds(List<Long> customersId, Specification<Project> specification, Pageable pageable);
}
