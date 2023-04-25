package com.example.demouploadanddowloadfile.service.user_service.impl;

import com.example.demouploadanddowloadfile.dto.BaseResponse;
import com.example.demouploadanddowloadfile.dto.user.UserDTO;
import com.example.demouploadanddowloadfile.dto.user.UserLogin;
import com.example.demouploadanddowloadfile.entity.Role;
import com.example.demouploadanddowloadfile.entity.User;
import com.example.demouploadanddowloadfile.repository.RoleRepository;
import com.example.demouploadanddowloadfile.repository.UserRepository;
import com.example.demouploadanddowloadfile.security.CustomUserDetails;
import com.example.demouploadanddowloadfile.security.jwt.JwtTokenProvider;
import com.example.demouploadanddowloadfile.service.user_service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class UserServiceImpl implements IUserService {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;


    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }


    @Override
    @Transactional
    public BaseResponse<?> createUser(UserDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setAddress(dto.getAddress());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setPhoneNumber(dto.getPhoneNumber());
        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        role.setRoleName("USER");
        roleRepository.save(role);
        roles.add(role);
        user.setRoles(roles);
        repository.save(user);

        return BaseResponse.builder().message("SUCCESS")
//                .data(repository.findByUsername(dto.getUsername()))
                .build();
    }

    @Override
    public BaseResponse<?> login(UserLogin dto) {

        User user = repository.findByUsername(dto.getUsername());
       if(user != null){
           CustomUserDetails customUserDetails = CustomUserDetails.build(user);
           String jwt = jwtTokenProvider.generateToken(customUserDetails);
           return BaseResponse.builder()
                   .data(jwt)
                   .status(200)
                   .build();
       }
       else{
           return BaseResponse.builder()
                   .data(null)
                   .status(200)
                   .build();
       }
    }
}
