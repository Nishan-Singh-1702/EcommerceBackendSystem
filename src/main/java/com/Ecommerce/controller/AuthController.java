package com.Ecommerce.controller;

import com.Ecommerce.model.AppRole;
import com.Ecommerce.model.Role;
import com.Ecommerce.model.User;
import com.Ecommerce.repository.RoleRepository;
import com.Ecommerce.repository.UserRepository;
import com.Ecommerce.security.jwt.JwtUtils;
import com.Ecommerce.security.request.LoginRequest;
import com.Ecommerce.security.request.SignupRequest;
import com.Ecommerce.security.response.MessageResponse;
import com.Ecommerce.security.response.userInfoResponse;
import com.Ecommerce.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
        }
        catch(AuthenticationException exception){
            Map<String, Object> map = new HashMap<>();
            map.put("message","Bad Credentials");
            map.put("status",false);
            return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        userInfoResponse response = new userInfoResponse(userDetails.getId(),userDetails.getUsername(),roles);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,jwtCookie.toString()).body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
        if(userRepository.existsByUsername(signupRequest.getUsername())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Error: Username is already taken !!"));
        }
        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Error: Email is already taken !!"));
        }

        User user = new User(signupRequest.getUsername(),signupRequest.getEmail(),passwordEncoder.encode(signupRequest.getPassword()));

        Set<String> strRole = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if(strRole==null){
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(()->new RuntimeException("Error: Role is not found !!"));
            roles.add(userRole);
        }
        else {
            strRole.forEach(role->{
                switch (role){
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN).orElseThrow(()->new RuntimeException("Error: Role is not found !!"));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER).orElseThrow(()->new RuntimeException("Error: Role is not found !!"));
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(()->new RuntimeException("Error: Role is not found !!"));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User register successfully !!"));
    }

    @GetMapping("/username")
    public String currentUsername(Authentication authentication){
        if(authentication == null || !authentication.isAuthenticated()){
            return "No User logged in !!";
        }
        return authentication.getName();
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        userInfoResponse response = new userInfoResponse(userDetails.getId(),userDetails.getUsername(),roles);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOutUser(){
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(new MessageResponse("Sign out !!"));

    }
}
