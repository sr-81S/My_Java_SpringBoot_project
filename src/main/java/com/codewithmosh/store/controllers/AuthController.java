package com.codewithmosh.store.controllers;

import com.codewithmosh.store.config.JwtConfig;
import com.codewithmosh.store.dtos.JwtResponse;
import com.codewithmosh.store.dtos.LoginRequest;

import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import com.codewithmosh.store.services.JwtServices;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtServices jwtServices;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtConfig jwtConfig;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        var accessToken = jwtServices.generateAccessToken(user);
        var refreshToken = jwtServices.generateRefreshToken(user);

        var httpCookie = new Cookie("refreshToken", refreshToken);
        httpCookie.setHttpOnly(true);
        httpCookie.setPath("/auth/");
        httpCookie.setMaxAge((int) jwtConfig.getRefreshTokenExpiration()); // 7 days in seconds
        response.addCookie(httpCookie);

        return ResponseEntity.ok(new JwtResponse(accessToken));

    }

//controller for validateing the token

//    @PostMapping("/validate")
//    public Boolean validateToken(@RequestHeader("Authorization") String AuthHeader) {
//        String token = AuthHeader.replace("Bearer ", "");
//        return jwtServices.validateToken(token);
//    }

    //controller for refresh the access token

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@CookieValue("refreshToken") String refreshToken) {
        if (!jwtServices.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var userId = jwtServices.getUserIdFromToken(refreshToken);
        var user = userRepository.findById(userId).orElseThrow();
        var newAccessToken = jwtServices.generateAccessToken(user);
        return ResponseEntity.ok(new JwtResponse(newAccessToken));
    }

    //get the current user conttext by AIP

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentContext(){
        System.out.println("check");
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        var user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(userMapper.toDto(user));

    }

    //handel bad request exceptions
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
