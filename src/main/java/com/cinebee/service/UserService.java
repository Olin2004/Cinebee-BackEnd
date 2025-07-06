package com.cinebee.service;

import com.cinebee.dto.response.UserResponse;

public interface UserService {
    UserResponse getUserProfile(String username);
}
