package com.doctorappointment.service;

import com.doctorappointment.exception.DuplicateAppointmentException;
import com.doctorappointment.exception.ResourceNotFoundException;
import com.doctorappointment.model.Appointment;
import com.doctorappointment.model.AppointmentStatus;
import com.doctorappointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    public Appointment findById(String id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found."));
    }

    public List<Appointment> findByPatient(String patientId) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(patientId);
    }

    public Appointment create(Appointment appointment) {
        validate(appointment);
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setCreatedAt(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }

    public Appointment updateByAdmin(String id, Appointment form) {
        Appointment appointment = findById(id);
        appointment.setDoctorId(form.getDoctorId());
        appointment.setPatientId(form.getPatientId());
        appointment.setAppointmentDate(form.getAppointmentDate());
        appointment.setAppointmentTime(form.getAppointmentTime());
        appointment.setProblem(form.getProblem());
        appointment.setStatus(form.getStatus() == null ? AppointmentStatus.PENDING : form.getStatus());
        validateDuplicate(appointment, id);
        return appointmentRepository.save(appointment);
    }

    public Appointment updateByPatient(String id, Appointment form, String patientId) {
        Appointment appointment = findById(id);
        ensureOwner(appointment, patientId);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED ||
                appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("This appointment cannot be edited.");
        }
        appointment.setDoctorId(form.getDoctorId());
        appointment.setAppointmentDate(form.getAppointmentDate());
        appointment.setAppointmentTime(form.getAppointmentTime());
        appointment.setProblem(form.getProblem());
        appointment.setStatus(AppointmentStatus.PENDING);
        validate(appointment);
        validateDuplicate(appointment, id);
        return appointmentRepository.save(appointment);
    }

    public void cancelForPatient(String id, String patientId) {
        Appointment appointment = findById(id);
        ensureOwner(appointment, patientId);
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("A completed appointment cannot be cancelled.");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    public void delete(String id) {
        appointmentRepository.deleteById(id);
    }

    public void setStatus(String id, AppointmentStatus status) {
        Appointment appointment = findById(id);
        appointment.setStatus(status);
        appointmentRepository.save(appointment);
    }

    public long count() { return appointmentRepository.count(); }
    public long countByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatusOrderByAppointmentDateAscAppointmentTimeAsc(status).size();
    }

    private void validate(Appointment appointment) {
        if (appointment.getDoctorId() == null || appointment.getDoctorId().isBlank()) {
            throw new IllegalArgumentException("Doctor is required.");
        }
        if (appointment.getPatientId() == null || appointment.getPatientId().isBlank()) {
            throw new IllegalArgumentException("Patient is required.");
        }
        doctorService.findById(appointment.getDoctorId());
        patientService.findById(appointment.getPatientId());
        if (appointment.getAppointmentDate() == null || appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Appointment date cannot be in the past.");
        }
        if (appointment.getAppointmentTime() == null) {
            throw new IllegalArgumentException("Appointment time is required.");
        }
        if (appointment.getProblem() == null || appointment.getProblem().isBlank()) {
            throw new IllegalArgumentException("Problem/reason is required.");
        }
        validateDuplicate(appointment, appointment.getId());
    }

    private void validateDuplicate(Appointment appointment, String currentId) {
        boolean duplicate = appointmentRepository
                .existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                        appointment.getDoctorId(),
                        appointment.getAppointmentDate(),
                        appointment.getAppointmentTime(),
                        AppointmentStatus.CANCELLED);
        if (duplicate && (currentId == null || !sameSlotBelongsToCurrent(currentId, appointment))) {
            throw new DuplicateAppointmentException("This doctor already has an active appointment at that date and time.");
        }
    }

    private boolean sameSlotBelongsToCurrent(String id, Appointment appointment) {
        return appointmentRepository.findById(id)
                .map(existing -> existing.getDoctorId().equals(appointment.getDoctorId())
                        && existing.getAppointmentDate().equals(appointment.getAppointmentDate())
                        && existing.getAppointmentTime().equals(appointment.getAppointmentTime())
                        && existing.getStatus() != AppointmentStatus.CANCELLED)
                .orElse(false);
    }

    private void ensureOwner(Appointment appointment, String patientId) {
        if (!patientId.equals(appointment.getPatientId())) {
            throw new IllegalStateException("You are not allowed to access this appointment.");
        }
    }
}
