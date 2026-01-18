package com.projekt.wypozyczalnia.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDto {
    private String id;
    private String username;
    private String email;
    private String role;
}
