package com.freemind.timesheet.service;

import com.freemind.timesheet.domain.Company;
import com.freemind.timesheet.domain.Customer;
import com.freemind.timesheet.domain.Project;
import com.freemind.timesheet.repository.CompanyRepository;
import com.freemind.timesheet.repository.CustomerRepository;
import com.freemind.timesheet.service.dto.CustomerDTO;
import com.freemind.timesheet.service.mapper.CustomerMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Customer}.
 */
@Service
@Transactional
public class CustomerService {
    private final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final ProjectService projectService;

    private final CompanyRepository companyRepository;

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    public CustomerService(
        CustomerRepository customerRepository,
        CustomerMapper customerMapper,
        CompanyRepository companyRepository,
        ProjectService projectService
    ) {
        this.projectService = projectService;
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.companyRepository = companyRepository;
    }

    /**
     * Save a customer.
     *
     * @param customerDTO the entity to save.
     * @return the persisted entity.
     */
    public CustomerDTO save(CustomerDTO customerDTO) {
        log.debug("Request to save Customer : {}", customerDTO);
        Customer customer = customerMapper.toEntity(customerDTO);
        Company company = companyRepository.getOne(customerDTO.getCompanyId());
        company.addCustomer(customer);
        companyRepository.save(company);
        customer = customerRepository.save(customer);
        return customerMapper.toDto(customer);
    }

    /**
     * Get all the customers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Customers");
        return customerRepository.findAll(pageable).map(customerMapper::toDto);
    }

    /**
     * Get all the customers with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CustomerDTO> findAllWithEagerRelationships(Pageable pageable) {
        //        return customerRepository.findAllWithEagerRelationships(pageable).map(customerMapper::toDto);
        return null;
    }

    /**
     * Get one customer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CustomerDTO> findOne(Long id) {
        log.debug("Request to get Customer : {}", id);
        return customerRepository.findById(id).map(r -> customerMapper.toDto(r));
    }

    /**
     * Delete the customer by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) { //test
        log.debug("Request to delete Customer : {}", id);
        boolean canDelete = true;
        Customer customer = customerRepository.getOne(id);
        Set<Project> projects = new HashSet<Project>(customer.getProjects());
        boolean cannotDeleteBis = customer.getProjects().stream().anyMatch(p -> p.canDelete());
        log.debug("CanDelete : {}", !cannotDeleteBis);

        if (!cannotDeleteBis) {
            for (Project p : projects) {
                canDelete = p.getJobs().stream().anyMatch(j -> j.getPerformances().size() == 0);
                log.debug("Request to delete Project : {}", p);
                projectService.delete(p.getId());
            }
            customer.removeProjects();
            Company company = customer.getCompany();
            company.removeCustomer(customer);
            companyRepository.save(company);

            customerRepository.delete(customer);
        }
    }

    public List<CustomerDTO> findByUserId(Long userId) {
        log.debug("Request to find Customer by user : {}", userId);
        Page<CustomerDTO> tmp = this.customerRepository.findCustomersByUserId(userId, null, null).map(t -> this.customerMapper.toDto(t));
        log.debug("CUSTOMERS : {}", tmp);

        return tmp.getContent();
    }
}
