package com.example.service;

import com.example.payload.request.SignupRequest;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User with email '%s' not found", email)
        ));

        log.debug("Loaded user details for email: {}", email);
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .toList());
    }


    public User createNewUser(SignupRequest signupRequest) {
        String hashedPassword = passwordEncoder.encode(signupRequest.password());
        User user = new User();
        user.setEmail(signupRequest.email());
        user.setPassword(hashedPassword);
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findByName("ROLE_USER").get());
        user.setRoles(roles);
        userRepository.save(user);

        log.info("New user created with email: {}", signupRequest.email());
        return user;
    }

    public boolean isEmailVerified(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        boolean isVerified = userOptional.map(User::isEmailVerified).orElse(false);

        log.debug("Email verification status for {}: {}", email, isVerified);
        return isVerified;
    }

    public Page<User> findAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);
        log.debug("Found {} users", users.getTotalElements());
        return users;
    }
}
