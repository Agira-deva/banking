package com.banking.banking.dto;

import jakarta.annotation.security.DenyAll;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    public String email;
    public String password;

}
