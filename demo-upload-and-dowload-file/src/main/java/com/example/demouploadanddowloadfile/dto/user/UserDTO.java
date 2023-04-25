package com.example.demouploadanddowloadfile.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String username;

    private String password;

    private String fullName;

    private String address;

    private String email;

    private String phoneNumber;

}
