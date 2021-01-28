package com.freemind.timesheet.service.mapper;

import com.freemind.timesheet.domain.AppUser;
import com.freemind.timesheet.domain.Company;
import com.freemind.timesheet.domain.Customer;
import com.freemind.timesheet.domain.Project;
import com.freemind.timesheet.service.dto.CompanyDTO;
import com.freemind.timesheet.service.dto.CustomerDTO;
import com.freemind.timesheet.service.dto.ProjectDTO;
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
public class CustomerMapperImpl implements CustomerMapper {
    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public CustomerDTO toDto(Customer entity) {
        if (entity == null) {
            return null;
        }
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(entity.getId());
        customerDTO.setName(entity.getName());
        customerDTO.setCompanyName(customerCompanyName(entity));
        customerDTO.setEnable(entity.isEnable());
        customerDTO.setCompanyId(this.customerCompanyId(entity));
        customerDTO.setProjects(this.projectSetToProjectDTOSet(entity.getProjects()));
        return customerDTO;
    }

    @Override
    public List<Customer> toEntity(List<CustomerDTO> dtoList) {
        if (dtoList == null) {
            return null;
        }

        List<Customer> list = new ArrayList<Customer>(dtoList.size());
        for (CustomerDTO customerDTO : dtoList) {
            list.add(toEntity(customerDTO));
        }

        return list;
    }

    @Override
    public List<CustomerDTO> toDto(List<Customer> entityList) {
        if (entityList == null) {
            return null;
        }

        List<CustomerDTO> list = new ArrayList<CustomerDTO>(entityList.size());
        for (Customer customer : entityList) {
            list.add(toDto(customer));
        }

        return list;
    }

    @Override
    public Customer toEntity(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            return null;
        }

        Customer customer = new Customer();

        customer.setId(customerDTO.getId());
        customer.setName(customerDTO.getName());
        customer.setEnable(customerDTO.isEnable());
        customer.setCompany(companyMapper.fromId(customerDTO.getCompanyId()));
        customer.setProjects(this.projectDTOSetToProjectSet(customerDTO.getProjects()));
        return customer;
    }

    protected CompanyDTO companySetToCompanyDTOSet(Company set) {
        if (set == null) {
            return null;
        }
        return companyMapper.toDto(set);
    }

    protected Long companyDTOSetToCompanySet(Long set) {
        if (set == null) {
            return null;
        }

        return set;
    }

    private Long customerCompanyId(Customer customer) {
        if (customer == null) {
            return null;
        }
        Company company = customer.getCompany();
        if (company == null) {
            return null;
        }
        Long id = company.getId();
        if (id == null) {
            return null;
        }
        return id;
    }

    private String customerCompanyName(Customer customer) {
        if (customer == null) {
            return null;
        }
        Company company = customer.getCompany();
        if (company == null) {
            return null;
        }
        String name = company.getName();
        if (name == null) {
            return null;
        }
        return name;
    }

    protected Set<ProjectDTO> projectSetToProjectDTOSet(Set<Project> set) {
        if (set == null) {
            return null;
        }

        Set<ProjectDTO> set1 = new HashSet<ProjectDTO>(Math.max((int) (set.size() / .75f) + 1, 16));
        for (Project project : set) {
            set1.add(projectMapper.toDto(project));
        }

        return set1;
    }

    protected Set<Project> projectDTOSetToProjectSet(Set<ProjectDTO> set) {
        if (set == null) {
            return null;
        }

        Set<Project> set1 = new HashSet<Project>(Math.max((int) (set.size() / .75f) + 1, 16));
        for (ProjectDTO projectDTO : set) {
            set1.add(projectMapper.toEntity(projectDTO));
        }

        return set1;
    }
}
