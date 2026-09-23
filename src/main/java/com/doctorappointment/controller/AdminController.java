package com.doctorappointment.controller;

import com.doctorappointment.model.AppointmentStatus;
import com.doctorappointment.model.Doctor;
import com.doctorappointment.model.Patient;
import com.doctorappointment.model.Appointment;
import com.doctorappointment.service.AppointmentService;
import com.doctorappointment.service.DoctorService;
import com.doctorappointment.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("doctorCount", doctorService.count());
        model.addAttribute("patientCount", patientService.count());
        model.addAttribute("appointmentCount", appointmentService.count());
        model.addAttribute("pendingCount", appointmentService.countByStatus(AppointmentStatus.PENDING));
        model.addAttribute("confirmedCount", appointmentService.countByStatus(AppointmentStatus.CONFIRMED));
        model.addAttribute("completedCount", appointmentService.countByStatus(AppointmentStatus.COMPLETED));
        model.addAttribute("cancelledCount", appointmentService.countByStatus(AppointmentStatus.CANCELLED));
        return "admin/dashboard";
    }

    @GetMapping("/doctors")
    public String doctors(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("doctors", doctorService.findAll(keyword));
        model.addAttribute("keyword", keyword);
        return "admin/doctors";
    }

    @GetMapping("/doctors/new")
    public String newDoctor(Model model) {
        model.addAttribute("doctor", new Doctor());
        return "admin/doctor-form";
    }

    @PostMapping("/doctors/save")
    public String saveDoctor(@Valid @ModelAttribute Doctor doctor, BindingResult result, Model model) {
        if (result.hasErrors()) return "admin/doctor-form";
        doctorService.save(doctor);
        return "redirect:/doctors?success=Doctor+saved";
    }

    @GetMapping("/doctors/edit/{id}")
    public String editDoctor(@PathVariable String id, Model model) {
        model.addAttribute("doctor", doctorService.findById(id));
        return "admin/doctor-form";
    }

    @PostMapping("/doctors/update/{id}")
    public String updateDoctor(@PathVariable String id, @Valid @ModelAttribute Doctor doctor,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            doctor.setId(id);
            return "admin/doctor-form";
        }
        doctorService.update(id, doctor);
        return "redirect:/doctors?success=Doctor+updated";
    }

    @GetMapping("/doctors/{id}")
    public String doctorDetails(@PathVariable String id, Model model) {
        model.addAttribute("doctor", doctorService.findById(id));
        return "admin/doctor-details";
    }

    @PostMapping("/doctors/delete/{id}")
    public String deleteDoctor(@PathVariable String id) {
        doctorService.delete(id);
        return "redirect:/doctors?success=Doctor+deleted";
    }

    @GetMapping("/patients")
    public String patients(Model model) {
        model.addAttribute("patients", patientService.findAll());
        return "admin/patients";
    }

    @GetMapping("/patients/new")
    public String newPatient(Model model) {
        model.addAttribute("patient", new Patient());
        return "admin/patient-form";
    }

    @PostMapping("/patients/save")
    public String savePatient(@Valid @ModelAttribute Patient patient, BindingResult result) {
        if (result.hasErrors()) return "admin/patient-form";
        patientService.save(patient);
        return "redirect:/patients?success=Patient+saved";
    }

    @GetMapping("/patients/edit/{id}")
    public String editPatient(@PathVariable String id, Model model) {
        model.addAttribute("patient", patientService.findById(id));
        return "admin/patient-form";
    }

    @PostMapping("/patients/update/{id}")
    public String updatePatient(@PathVariable String id, @Valid @ModelAttribute Patient patient,
                                BindingResult result) {
        if (result.hasErrors()) {
            patient.setId(id);
            return "admin/patient-form";
        }
        patientService.update(id, patient);
        return "redirect:/patients?success=Patient+updated";
    }

    @GetMapping("/patients/{id}")
    public String patientDetails(@PathVariable String id, Model model) {
        model.addAttribute("patient", patientService.findById(id));
        return "admin/patient-details";
    }

    @PostMapping("/patients/delete/{id}")
    public String deletePatient(@PathVariable String id) {
        patientService.delete(id);
        return "redirect:/patients?success=Patient+deleted";
    }

    @GetMapping("/appointments")
    public String appointments(Model model) {
        model.addAttribute("appointments", appointmentService.findAll());
        model.addAttribute("doctors", doctorService.findAll(null));
        model.addAttribute("patients", patientService.findAll());
        return "admin/appointments";
    }

    @GetMapping("/appointments/edit/{id}")
    public String editAppointment(@PathVariable String id, Model model) {
        model.addAttribute("appointment", appointmentService.findById(id));
        model.addAttribute("doctors", doctorService.findAll(null));
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("statuses", AppointmentStatus.values());
        return "admin/appointment-form";
    }

    @PostMapping("/appointments/update/{id}")
    public String updateAppointment(@PathVariable String id,
                                    @ModelAttribute Appointment appointment) {
        appointmentService.updateByAdmin(id, appointment);
        return "redirect:/appointments?success=Appointment+updated";
    }

    @PostMapping("/appointments/status/{id}")
    public String status(@PathVariable String id, @RequestParam AppointmentStatus status) {
        appointmentService.setStatus(id, status);
        return "redirect:/appointments?success=Status+updated";
    }

    @PostMapping("/appointments/delete/{id}")
    public String deleteAppointment(@PathVariable String id) {
        appointmentService.delete(id);
        return "redirect:/appointments?success=Appointment+deleted";
    }
}
