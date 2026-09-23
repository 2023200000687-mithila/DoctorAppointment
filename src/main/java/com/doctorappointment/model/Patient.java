package com.doctorappointment.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "patients")
public class Patient {
    @Id
    private String id;

    @NotBlank(message = "Patient name is required.")
    private String name;

    @Min(value = 1, message = "Age must be greater than 0.")
    private Integer age;

    @NotBlank(message = "Gender is required.")
    private String gender;

    @NotBlank(message = "Phone is required.")
    private String phone;

    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email address.")
    private String email;

    private String address;
    private LocalDateTime createdAt;
}
