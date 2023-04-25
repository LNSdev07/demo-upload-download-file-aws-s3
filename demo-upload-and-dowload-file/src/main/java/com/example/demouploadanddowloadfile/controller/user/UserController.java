package com.example.demouploadanddowloadfile.controller.user;


import com.example.demouploadanddowloadfile.dto.BaseResponse;
import com.example.demouploadanddowloadfile.dto.user.UserDTO;
import com.example.demouploadanddowloadfile.service.user_service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private IUserService service;


    @PostMapping("/api/auth/register")
    public BaseResponse<?> createUser(@RequestBody UserDTO dto){
        return service.createUser(dto);
    }
}
