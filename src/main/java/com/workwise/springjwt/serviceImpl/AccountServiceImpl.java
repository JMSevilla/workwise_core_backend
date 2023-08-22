package com.workwise.springjwt.serviceImpl;

import com.workwise.springjwt.models.ERole;
import com.workwise.springjwt.models.Role;
import com.workwise.springjwt.models.User;
import com.workwise.springjwt.payload.request.LoginRequest;
import com.workwise.springjwt.payload.request.SignupRequest;
import com.workwise.springjwt.payload.request.TokenRefreshRequest;
import com.workwise.springjwt.payload.response.MessageResponse;
import com.workwise.springjwt.repository.AccountRepository;
import com.workwise.springjwt.repository.RoleRepository;
import com.workwise.springjwt.security.jwt.JwtUtils;
import com.workwise.springjwt.security.services.UserDetailsImpl;
import com.workwise.springjwt.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Override
    public Integer accountCreation(SignupRequest signupRequest) {
        if(accountRepository.existsByUsername(signupRequest.getUsername())){
            return 301;
        }
        if(accountRepository.existsByEmail(signupRequest.getEmail())){
            return 302;
        }
        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()),
                signupRequest.getFirstname(),
                signupRequest.getMiddlename(),
                signupRequest.getLastname(),
                signupRequest.getStatus(),
                signupRequest.getImage_url(),
                signupRequest.getAccess_level()
        );
        Set<String> rolesProvided = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if(rolesProvided == null){
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            roles.add(userRole);
        }else{
            rolesProvided.forEach(role -> {
                switch (role){
                    case "admin":
                        Role ad = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(ad);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(userRole);
                        break;
                }
            });
        }
        user.setRoles(roles);
        accountRepository.save(user);
        return 200;
    }


}
