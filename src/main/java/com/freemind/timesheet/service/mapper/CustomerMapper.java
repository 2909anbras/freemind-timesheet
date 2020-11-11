package com.freemind.timesheet.service.mapper;


import com.freemind.timesheet.domain.*;
import com.freemind.timesheet.service.dto.CustomerDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Customer} and its DTO {@link CustomerDTO}.
 */
@Mapper(componentModel = "spring", uses = {CompanyMapper.class})
public interface CustomerMapper extends EntityMapper<CustomerDTO, Customer> {


    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "removeProject", ignore = true)
    @Mapping(target = "removeCompany", ignore = true)
    Customer toEntity(CustomerDTO customerDTO);

    default Customer fromId(Long id) {
        if (id == null) {
            return null;
        }
        Customer customer = new Customer();
        customer.setId(id);
        return customer;
    }
}
