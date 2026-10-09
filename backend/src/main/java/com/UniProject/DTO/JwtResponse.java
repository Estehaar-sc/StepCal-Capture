package com.UniProject.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class JwtResponse {
    private String token;
    private String refreshToken;
}
