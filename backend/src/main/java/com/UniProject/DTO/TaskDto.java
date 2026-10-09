package com.UniProject.DTO;

import lombok.*;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TaskDto {
    private double Target_Calorie;
    private Map<String,Double>Exercises;
}
