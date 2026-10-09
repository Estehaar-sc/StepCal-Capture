package com.UniProject.Controller;

import com.UniProject.DTO.JwtResponse;
import com.UniProject.DTO.Message;
import com.UniProject.DTO.RefreshToken;
import com.UniProject.DTO.SignInRequest;
import com.UniProject.DTO.UserDto;
import com.UniProject.Services.AuthenticationService;
import com.UniProject.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public Object userLogin(@RequestBody SignInRequest info) {
        try {
            JwtResponse response = authenticationService.login(info);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Message message = new Message();
            message.setMessage("Wrong username or password");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(message);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveUser(@RequestBody UserDto user) {
        if (!userService.checkForDuplicateEmail(user.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Email already exist try another email");
        }

        int result = userService.saveUser(user);

        if (result == 1) {
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("Information saved Successfully");
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error occurred while saving");
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(
            @RequestBody RefreshToken refreshToken) {
        return ResponseEntity.ok(
                authenticationService.refreshToken(refreshToken)
        );
    }
}