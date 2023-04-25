package com.example.demouploadanddowloadfile.service.user_service;

import com.example.demouploadanddowloadfile.dto.BaseResponse;
import com.example.demouploadanddowloadfile.dto.user.UserDTO;
import com.example.demouploadanddowloadfile.dto.user.UserLogin;

public interface IUserService {
    BaseResponse<?> createUser(UserDTO dto);

    BaseResponse<?> login(UserLogin dto);
}
