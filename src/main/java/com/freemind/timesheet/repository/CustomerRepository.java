package com.freemind.timesheet.repository;

import com.freemind.timesheet.domain.Company;
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
 * Spring Data  repository for the Customer entity.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    //    @Query(
    ////        value = "select distinct customer from Customer customer left join fetch customer.companies",
    ////        countQuery = "select count(distinct customer) from Customer customer"
    //    )
    //    Page<Customer> findAllWithEagerRelationships(Pageable pageable);
    //
    ////    @Query("select distinct customer from Customer customer left join fetch customer.companies")
    //    List<Customer> findAllWithEagerRelationships();
    //
    ////    @Query("select customer from Customer customer left join fetch customer.companies where customer.id =:id")
    //    Optional<Customer> findOneWithEagerRelationships(@Param("id") Long id);

    @Query("select distinct customer from Customer customer left join customer.projects")
    Page<Customer> findAllWithEagerRelationships(Specification specification, Pageable pageable);

    @Query("select distinct customer.id from Customer customer where customer.company.id= ?1 ")
    List<Long> findCustomersIdByCompany(Long id);

    @Query("select distinct customer from Customer customer where customer.company.id=?1")
    Page findByCompany(Long id, Specification<Customer> specification, Pageable page);
}
