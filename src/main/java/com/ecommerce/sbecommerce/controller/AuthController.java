package com.ecommerce.sbecommerce.controller;

import com.ecommerce.sbecommerce.model.Role;
import com.ecommerce.sbecommerce.model.RoleType;
import com.ecommerce.sbecommerce.model.User;
import com.ecommerce.sbecommerce.payload.UserDTO;
import com.ecommerce.sbecommerce.repository.RoleRepository;
import com.ecommerce.sbecommerce.repository.UserRepository;
import com.ecommerce.sbecommerce.security.JwtUtils;
import com.ecommerce.sbecommerce.security.payloads.*;
import com.ecommerce.sbecommerce.service.UserService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder encoder;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch(AuthenticationException e){
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        List<String> roles = user.getAuthorities().stream()
                .map(Role::getAuthority)
                .collect(Collectors.toList());

        // Token Authentication
//        String jwtToken = jwtUtils.generateTokenFromUsername(user.getUsername());
//        LoginResponse response = new LoginResponse(jwtToken, user.getUsername(), roles);
//        return ResponseEntity.ok(response);

        // Cookie authentication
        ResponseCookie jwtCookie = jwtUtils.generateCookie(user);
        LoginResponse response = new LoginResponse(user.getUsername(), roles);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(response);

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest){
        if(userService.existsByUsername(signUpRequest.getUsername())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken."));
        }

        if(userService.existsByEmail(signUpRequest.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use."));
        }

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()){
            Role userRole = roleRepository.findByRoleType(RoleType.USER)
                    .orElseThrow(()-> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }else{
            strRoles.forEach(role -> {
                switch (role.toLowerCase()){
                    case "admin":
                        Role adminRole = roleRepository.findByRoleType(RoleType.ADMIN)
                                .orElseThrow(()-> new RuntimeException("Error: Role is not found: " + role));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole = roleRepository.findByRoleType(RoleType.SELLER)
                                .orElseThrow(()-> new RuntimeException("Error: Role is not found: " + role));
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByRoleType(RoleType.USER)
                                .orElseThrow(()-> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        user.setAuthorities(roles);
        userService.create(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully."));
    }

    @GetMapping("/username")
    public String currentUserName(Authentication authentication){
        if(authentication == null)
            return "";
        return authentication.getName();
    }

    @GetMapping("/getuser")
    public ResponseEntity<UserDTO> getUser(Authentication authentication){
        if(authentication == null)
            return null;
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(userService.getDTO(user));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signout(){
        ResponseCookie cleanCookie = jwtUtils.generateCleanCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .body(new MessageResponse("You have been signed out."));
    }
}
