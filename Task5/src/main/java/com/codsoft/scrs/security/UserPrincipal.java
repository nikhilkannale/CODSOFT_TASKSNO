package com.codsoft.scrs.security;

import com.codsoft.scrs.entity.Student;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapts our {@link Student} entity (which represents both STUDENT and
 * ADMIN accounts) to Spring Security's UserDetails contract.
 */
@Getter
public class UserPrincipal implements UserDetails {

    private final Student student;

    public UserPrincipal(Student student) {
        this.student = student;
    }

    public Long getId() {
        return student.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + student.getRole().name()));
    }

    @Override
    public String getPassword() {
        return student.getPassword();
    }

    @Override
    public String getUsername() {
        return student.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
