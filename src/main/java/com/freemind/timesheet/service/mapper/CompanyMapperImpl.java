package com.freemind.timesheet.service.mapper;

import com.freemind.timesheet.domain.Company;
import com.freemind.timesheet.domain.Customer;
import com.freemind.timesheet.service.dto.CompanyDTO;
import com.freemind.timesheet.service.dto.CustomerDTO;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-11-17T13:12:19+0100",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.8 (AdoptOpenJDK)"
)
@Component
public class CompanyMapperImpl implements CompanyMapper {
    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public CompanyDTO toDto(Company entity) {
        if (entity == null) {
            return null;
        }

        CompanyDTO companyDTO = new CompanyDTO();

        companyDTO.setId(entity.getId());
        companyDTO.setName(entity.getName());
        companyDTO.setCustomers(this.customerSetToCustomerDTOSet(entity.getCustomers()));

        return companyDTO;
    }

    @Override
    public Company toEntity(CompanyDTO companyDTO) {
        if (companyDTO == null) {
            return null;
        }

        Company company = new Company();

        company.setId(companyDTO.getId());
        company.setName(companyDTO.getName());
        company.setCustomer(this.customerDTOSetToCustomerSet(companyDTO.getCustomers()));

        return company;
    }

    @Override
    public List<Company> toEntity(List<CompanyDTO> dtoList) {
        if (dtoList == null) {
            return null;
        }

        List<Company> list = new ArrayList<Company>(dtoList.size());
        for (CompanyDTO companyDTO : dtoList) {
            list.add(toEntity(companyDTO));
        }

        return list;
    }

    @Override
    public List<CompanyDTO> toDto(List<Company> entityList) {
        if (entityList == null) {
            return null;
        }

        List<CompanyDTO> list = new ArrayList<CompanyDTO>(entityList.size());
        for (Company company : entityList) {
            list.add(toDto(company));
        }

        return list;
    }

    protected Set<CustomerDTO> customerSetToCustomerDTOSet(Set<Customer> set) {
        if (set == null) {
            return null;
        }

        Set<CustomerDTO> set1 = new HashSet<CustomerDTO>(Math.max((int) (set.size() / .75f) + 1, 16));
        for (Customer customer : set) {
            set1.add(customerMapper.toDto(customer));
        }

        return set1;
    }

    protected Set<Customer> customerDTOSetToCustomerSet(Set<CustomerDTO> set) {
        if (set == null) {
            return null;
        }

        Set<Customer> set1 = new HashSet<Customer>(Math.max((int) (set.size() / .75f) + 1, 16));
        for (CustomerDTO customerDTO : set) {
            set1.add(customerMapper.toEntity(customerDTO));
        }

        return set1;
    }
}
