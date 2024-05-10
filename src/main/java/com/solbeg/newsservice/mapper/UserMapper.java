package com.solbeg.newsservice.mapper;


import com.solbeg.newsservice.dto.request.JwtUser;
import com.solbeg.newsservice.dto.response.RoleResponse;
import com.solbeg.newsservice.dto.response.UserResponse;
import com.solbeg.newsservice.enams.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts {@link UserResponse} to {@link JwtUser}.
     * @param userResponse received from a third-party service.
     * @return {@link JwtUser}.
     */
    @Mapping(target = "authorities", source = "userResponse.roles", qualifiedByName = "mapToAuthorities")
    @Mapping(target = "enabled", source = "userResponse.status", qualifiedByName = "mapToEnabledStatus")
    JwtUser toUserDetails(UserResponse userResponse);

    @Named("mapToAuthorities")
    default List<SimpleGrantedAuthority> mapToAuthorities(List<RoleResponse> roles) {
        return roles.stream()
                .map(roleResponse -> new SimpleGrantedAuthority(roleResponse.getName()))
                .collect(Collectors.toList());
    }

    @Named("mapToEnabledStatus")
    default Boolean mapToEnabledStatus(Status status) {
        return Status.ACTIVE.equals(status);
    }
}