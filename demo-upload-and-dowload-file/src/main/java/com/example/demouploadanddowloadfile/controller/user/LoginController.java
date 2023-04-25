package com.example.demouploadanddowloadfile.controller.user;


import com.example.demouploadanddowloadfile.dto.BaseResponse;
import com.example.demouploadanddowloadfile.dto.user.UserDTO;
import com.example.demouploadanddowloadfile.dto.user.UserLogin;
import com.example.demouploadanddowloadfile.service.user_service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private IUserService service;

    @PostMapping("/api/auth/login")
    public BaseResponse<?> createUser(@RequestBody UserLogin dto){
        return service.login(dto);
    }
}
