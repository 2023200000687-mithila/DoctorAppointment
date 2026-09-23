package com.doctorappointment.controller;

import com.doctorappointment.dto.RegistrationForm;
import com.doctorappointment.model.Patient;
import com.doctorappointment.service.PatientService;
import com.doctorappointment.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final PatientService patientService;
    private final UserService userService;

    @GetMapping("/register")
    public String register(Model model) {
        RegistrationForm form = new RegistrationForm();
        model.addAttribute("registrationForm", form);
        // Thymeleaf th:errors expects a BindingResult. Add an empty one for the initial GET request.
        model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "registrationForm",
                new BeanPropertyBindingResult(form, "registrationForm"));
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        String email = form.getEmail().trim().toLowerCase();
        form.setEmail(email);

        if (userService.existsByUsername(email)) {
            model.addAttribute("error", "An account with this email already exists. Please log in.");
            return "auth/register";
        }
        if (patientService.existsByEmail(email)) {
            model.addAttribute("error", "A patient profile with this email already exists. Please use another email.");
            return "auth/register";
        }

        Patient patient = null;
        try {
            patient = patientService.save(Patient.builder()
                    .name(form.getName().trim())
                    .age(form.getAge())
                    .gender(form.getGender())
                    .phone(form.getPhone().trim())
                    .email(email)
                    .address(form.getAddress())
                    .build());

            userService.createPatientUser(email, form.getPassword(), patient.getId());
            return "redirect:/login?registered=true";
        } catch (Exception ex) {
            // Do not leave an orphan patient profile if account creation fails.
            if (patient != null && patient.getId() != null) {
                try {
                    patientService.deleteIfNoAppointments(patient.getId());
                } catch (Exception ignored) {
                    // Keep the original user-friendly registration error.
                }
            }
            model.addAttribute("error", "Registration could not be completed. Please check your details and try again.");
            return "auth/register";
        }
    }
}
