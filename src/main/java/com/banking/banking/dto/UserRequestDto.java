package com.banking.banking.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    @NotBlank(message = "Invalid name")
    @Size(min=2,max = 10,message = "Name must between 2 and 10 Characters")
    @Pattern(regexp = "^[a-zA-Z]*$",message = "Name must be Characters Only")
    private  String firstName;
    @NotBlank(message = "Invalid name")
    @Size(min=2,max = 10,message = "Name must between 2 and 10 Characters")
    @Pattern(regexp = "^[a-zA-Z]*$",message = "Name must be Characters Only")
    private  String lastName;
    @NotBlank(message = "Invalid name")
    @Size(min=2,max = 10,message = "Name must between 2 and 10 Characters")
    @Pattern(regexp = "^[a-zA-Z]*$",message = "Name must be Characters Only")
    private  String otherName;
    @NotBlank(message = "Invalid gender")
    @Size(min=4,max=6,message = "Invalid gender")
    private String gender;
    @NotBlank(message = "Invalid address")
    @Size(min=2,max = 70,message = "Address must between 2 and 70 Characters")
    private  String address;
    @NotBlank(message = "Invalid state")
    @Size(min=3,max = 15,message = "Address must between 3 and 15 Characters")
    private String stateOfOrigin;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Password cannot be null or empty")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$", message = "Password must contain at least one digit, one lowercase, one uppercase letter, one special character, and no whitespace")
    private  String password;
    @NotBlank(message = "Number cannot be null or empty")
    @Size(min = 10,max = 10,message = "Number cannot be more than 10 digits")
    @Pattern(regexp = "^\\d{10}$")
    private String phoneNumber;
    private String alternativePhoneNumber;
}
