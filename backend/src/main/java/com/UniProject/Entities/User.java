package com.UniProject.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private int age;
    private double height;
    private double weight;
    private String gender;
    private String goal;
    private int point;
    private long phone_no;
    private boolean isEnabled;
    private String role;
    private String activity_level;
}
