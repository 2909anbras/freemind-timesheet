package com.freemind.timesheet.service;

import com.freemind.timesheet.domain.Company;
import com.freemind.timesheet.domain.Customer;
import com.freemind.timesheet.repository.CompanyRepository;
import com.freemind.timesheet.service.dto.CompanyDTO;
import com.freemind.timesheet.service.mapper.CompanyMapper;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Company}.
 */
@Service
@Transactional
public class CompanyService {
    private final Logger log = LoggerFactory.getLogger(CompanyRepository.class);

    private final CompanyRepository companyRepository;

    private final CustomerService customerService;

    private final CompanyMapper companyMapper;

    public CompanyService(CompanyRepository companyRepository, CompanyMapper companyMapper, CustomerService customerService) {
        this.companyRepository = companyRepository;
        this.customerService = customerService;
        this.companyMapper = companyMapper;
    }

    /**
     * Save a company.
     *
     * @param companyDTO the entity to save.
     * @return the persisted entity.
     */
    public CompanyDTO save(CompanyDTO companyDTO) {
        log.debug("Request to save Company : {}", companyDTO);
        Company company = companyMapper.toEntity(companyDTO);
        company = companyRepository.save(company);
        return companyMapper.toDto(company);
    }

    /**
     * Get all the companies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CompanyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Companies");
        return companyRepository.findAll(pageable).map(companyMapper::toDto);
    }

    /**
     * Get one company by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CompanyDTO> findOne(Long id) {
        log.debug("Request to get Company : {}", id);
        return companyRepository.findById(id).map(companyMapper::toDto);
    }

    /**
     * Delete the company by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) { //only the admin non?
        log.debug("Request to delete Company : {}", id);
        Company company = this.companyRepository.getOne(id);
        Set<Customer> customers = new HashSet<Customer>(company.getCustomers());
        //delete user
        for (Customer c : customers) this.customerService.delete(c.getId());
        companyRepository.deleteById(id);
    }
}
