package com.solbeg.newsservice.service;


import com.solbeg.newsservice.dto.response.UserResponse;

import java.util.UUID;

public interface UserDataService {

    /**
     * This method finds and returns user with his data by his token.
     *
     * @param token a string containing the authentication token in the request header.
     * @return the {@link UserResponse} object containing the user's data.
     */
    UserResponse getUserData(String token);

    /**
     * This method finds and returns user with his data by his id.
     *
     * @param userId id of user.
     * @return the {@link UserResponse} object containing the user's data.
     */
    UserResponse getUserData(UUID userId, String token);
}