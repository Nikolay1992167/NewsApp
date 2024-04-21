package com.solbeg.newsservice.mapper;

import com.solbeg.newsservice.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface UserMapper {

    default UserDetails toUserDetails(UserResponse userResponse) {
        List<SimpleGrantedAuthority> authorities = userResponse.roles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return User.builder()
                .username(userResponse.email())
                .password(userResponse.password())
                .authorities(authorities)
                .build();
    }
}