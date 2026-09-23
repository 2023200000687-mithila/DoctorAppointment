package com.doctorappointment.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "doctors")
public class Doctor {
    @Id
    private String id;

    @NotBlank(message = "Doctor name is required.")
    private String name;

    @NotBlank(message = "Specialization is required.")
    private String specialization;

    @NotBlank(message = "Phone is required.")
    private String phone;

    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email address.")
    private String email;

    private List<String> availableDays;
    private String availableTime;
    private LocalDateTime createdAt;
}
