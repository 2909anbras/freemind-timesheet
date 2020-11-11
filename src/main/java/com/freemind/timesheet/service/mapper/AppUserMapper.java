package com.freemind.timesheet.service.mapper;


import com.freemind.timesheet.domain.*;
import com.freemind.timesheet.service.dto.AppUserDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link AppUser} and its DTO {@link AppUserDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, JobMapper.class, CompanyMapper.class})
public interface AppUserMapper extends EntityMapper<AppUserDTO, AppUser> {

    @Mapping(source = "internalUser.id", target = "internalUserId")
    @Mapping(source = "company.id", target = "companyId")
    AppUserDTO toDto(AppUser appUser);

    @Mapping(source = "internalUserId", target = "internalUser")
    @Mapping(target = "removeJob", ignore = true)
    @Mapping(source = "companyId", target = "company")
    AppUser toEntity(AppUserDTO appUserDTO);

    default AppUser fromId(Long id) {
        if (id == null) {
            return null;
        }
        AppUser appUser = new AppUser();
        appUser.setId(id);
        return appUser;
    }
}
