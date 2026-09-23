package com.doctorappointment.controller;

import com.doctorappointment.model.Appointment;
import com.doctorappointment.model.Doctor;
import com.doctorappointment.model.Patient;
import com.doctorappointment.service.AppointmentService;
import com.doctorappointment.service.DoctorService;
import com.doctorappointment.service.PatientService;
import com.doctorappointment.service.UserService;
import com.doctorappointment.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/patient")
public class PatientController {
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final UserService userService;

    private Patient currentPatient(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        if (user == null || user.getPatientId() == null) {
            throw new IllegalStateException("Patient account is not linked to a profile.");
        }
        return patientService.findById(user.getPatientId());
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        Patient patient = currentPatient(authentication);
        var appointments = appointmentService.findByPatient(patient.getId());
        model.addAttribute("patient", patient);
        model.addAttribute("doctors", doctorService.findAll(null));
        model.addAttribute("appointments", appointments);
        model.addAttribute("pendingCount", appointments.stream().filter(a -> a.getStatus().name().equals("PENDING")).count());
        model.addAttribute("confirmedCount", appointments.stream().filter(a -> a.getStatus().name().equals("CONFIRMED")).count());
        model.addAttribute("cancelledCount", appointments.stream().filter(a -> a.getStatus().name().equals("CANCELLED")).count());
        return "patient/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        model.addAttribute("patient", currentPatient(authentication));
        return "patient/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Authentication authentication, Model model) {
        model.addAttribute("patient", currentPatient(authentication));
        return "patient/profile-form";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Authentication authentication, @Valid @ModelAttribute Patient form,
                                BindingResult result, Model model) {
        Patient current = currentPatient(authentication);
        if (result.hasErrors()) {
            form.setId(current.getId());
            return "patient/profile-form";
        }
        patientService.update(current.getId(), form);
        return "redirect:/patient/profile?success=Profile+updated";
    }

    @GetMapping("/doctors")
    public String doctors(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("doctors", doctorService.findAll(keyword));
        model.addAttribute("keyword", keyword);
        return "patient/doctors";
    }

    @GetMapping("/doctors/{id}")
    public String doctorDetails(@PathVariable String id, Model model) {
        model.addAttribute("doctor", doctorService.findById(id));
        return "patient/doctor-details";
    }

    @GetMapping("/appointments")
    public String appointments(Authentication authentication, Model model) {
        Patient patient = currentPatient(authentication);
        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appointmentService.findByPatient(patient.getId()));
        return "patient/appointments";
    }

    @GetMapping("/appointments/new")
    public String newAppointment(Authentication authentication, Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("doctors", doctorService.findAll(null));
        model.addAttribute("patient", currentPatient(authentication));
        return "patient/appointment-form";
    }

    @PostMapping("/appointments/save")
    public String saveAppointment(Authentication authentication,
                                  @Valid @ModelAttribute Appointment appointment,
                                  BindingResult result, Model model) {
        Patient patient = currentPatient(authentication);
        appointment.setPatientId(patient.getId());

        if (result.hasErrors()) {
            model.addAttribute("doctors", doctorService.findAll(null));
            model.addAttribute("patient", patient);
            return "patient/appointment-form";
        }

        try {
            appointmentService.create(appointment);
            return "redirect:/patient/appointments?success=Appointment+booked";
        } catch (Exception ex) {
            model.addAttribute("doctors", doctorService.findAll(null));
            model.addAttribute("patient", patient);
            model.addAttribute("error", ex.getMessage());
            return "patient/appointment-form";
        }
    }

    @GetMapping("/appointments/edit/{id}")
    public String editAppointment(@PathVariable String id, Authentication authentication, Model model) {
        Patient patient = currentPatient(authentication);
        Appointment appointment = appointmentService.findById(id);
        if (!patient.getId().equals(appointment.getPatientId())) {
            throw new IllegalStateException("You are not allowed to access this appointment.");
        }
        model.addAttribute("appointment", appointment);
        model.addAttribute("doctors", doctorService.findAll(null));
        return "patient/appointment-form";
    }

    @PostMapping("/appointments/update/{id}")
    public String updateAppointment(@PathVariable String id, Authentication authentication,
                                    @Valid @ModelAttribute Appointment appointment,
                                    BindingResult result, Model model) {
        Patient patient = currentPatient(authentication);
        appointment.setPatientId(patient.getId());
        if (result.hasErrors()) {
            model.addAttribute("doctors", doctorService.findAll(null));
            return "patient/appointment-form";
        }
        try {
            appointmentService.updateByPatient(id, appointment, patient.getId());
            return "redirect:/patient/appointments?success=Appointment+updated";
        } catch (Exception ex) {
            model.addAttribute("doctors", doctorService.findAll(null));
            model.addAttribute("error", ex.getMessage());
            return "patient/appointment-form";
        }
    }

    @PostMapping("/appointments/cancel/{id}")
    public String cancelAppointment(@PathVariable String id, Authentication authentication) {
        appointmentService.cancelForPatient(id, currentPatient(authentication).getId());
        return "redirect:/patient/appointments?success=Appointment+cancelled";
    }
}
