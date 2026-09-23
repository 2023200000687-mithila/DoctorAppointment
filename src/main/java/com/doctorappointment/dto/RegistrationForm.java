package com.doctorappointment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationForm {
    @NotBlank(message = "Name is required.")
    private String name;

    @NotNull(message = "Age is required.")
    @Min(value = 1, message = "Enter a valid age.")
    private Integer age;

    @NotBlank(message = "Gender is required.")
    private String gender;

    @NotBlank(message = "Phone is required.")
    private String phone;

    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email address.")
    private String email;

    private String address;

    @NotBlank(message = "Password is required.")
    private String password;
}
