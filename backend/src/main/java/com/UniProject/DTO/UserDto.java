package com.UniProject.DTO;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDto {
    private long id;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private long phone_no;
    private int age;
    private double height;
    private double weight;
    private String goal;
    private String gender;
    private String activity_level;
    int point;
}
