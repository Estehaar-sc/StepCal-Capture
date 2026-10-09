package com.UniProject.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class SignInRequest {
    private String email;
    private String password;
}
