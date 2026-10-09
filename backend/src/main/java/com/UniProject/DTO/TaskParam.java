package com.UniProject.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TaskParam {
    private int age;
    private double height;
    private double weight;
    private String Goal;
    private String gender;
    private String activity_level;
}
