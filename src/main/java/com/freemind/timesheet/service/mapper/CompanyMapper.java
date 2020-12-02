package com.freemind.timesheet.service.mapper;

import com.freemind.timesheet.domain.*;
import com.freemind.timesheet.service.dto.CompanyDTO;
import com.freemind.timesheet.service.dto.ProjectDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Company} and its DTO {@link CompanyDTO}.
 */
@Mapper(componentModel = "spring", uses = { CustomerMapper.class })
public interface CompanyMapper extends EntityMapper<CompanyDTO, Company> {
    //    @Mapping(source = "customers", target = "customers")
    //    CompanyDTO toDto(Company company);

    @Mapping(target = "appUsers", ignore = true)
    @Mapping(target = "removeAppUser", ignore = true)
    @Mapping(target = "customers", ignore = true)
    @Mapping(target = "removeCustomer", ignore = true)
    Company toEntity(CompanyDTO companyDTO);

    default Company fromId(Long id) {
        if (id == null) {
            return null;
        }
        Company company = new Company();
        company.setId(id);
        return company;
    }
}
