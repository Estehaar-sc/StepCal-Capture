package com.UniProject.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/message")
    public ResponseEntity<String>showMessage(){
        return ResponseEntity.status(HttpStatus.OK).body("This is for Admin");
    }
}
