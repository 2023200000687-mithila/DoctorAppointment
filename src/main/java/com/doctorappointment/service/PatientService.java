package com.doctorappointment.service;

import com.doctorappointment.exception.ResourceNotFoundException;
import com.doctorappointment.model.Patient;
import com.doctorappointment.repository.AppointmentRepository;
import com.doctorappointment.repository.PatientRepository;
import com.doctorappointment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Patient findById(String id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found."));
    }

    public Patient save(Patient patient) {
        if (patient.getCreatedAt() == null) patient.setCreatedAt(LocalDateTime.now());
        return patientRepository.save(patient);
    }

    public Patient update(String id, Patient form) {
        Patient patient = findById(id);
        patient.setName(form.getName());
        patient.setAge(form.getAge());
        patient.setGender(form.getGender());
        patient.setPhone(form.getPhone());
        patient.setEmail(form.getEmail());
        patient.setAddress(form.getAddress());
        return patientRepository.save(patient);
    }

    public void delete(String id) {
        if (appointmentRepository.existsByPatientId(id)) {
            throw new IllegalStateException("This patient has appointments. Delete or handle those appointments first.");
        }
        userRepository.findByPatientId(id).ifPresent(userRepository::delete);
        patientRepository.deleteById(id);
    }

    public long count() {
        return patientRepository.count();
    }

    public Patient findByEmail(String email) {
        return patientRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found."));
    }

    public boolean existsByEmail(String email) {
        return patientRepository.findByEmailIgnoreCase(email).isPresent();
    }

    public void deleteIfNoAppointments(String id) {
        if (!appointmentRepository.existsByPatientId(id)) {
            patientRepository.deleteById(id);
        }
    }
}
