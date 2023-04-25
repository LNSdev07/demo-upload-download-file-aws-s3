package com.example.demouploadanddowloadfile.security;

import com.example.demouploadanddowloadfile.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private String username;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> roles;

    public static CustomUserDetails build(User user){
        List<GrantedAuthority> authorities = user.getRoles()
                .stream().map(role  ->{
                    return new SimpleGrantedAuthority(role.getRoleName());
                }).filter(Objects::nonNull).collect(Collectors.toList());
         return new CustomUserDetails(user.getUsername(),
                 user.getPassword(),
                 authorities);
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
