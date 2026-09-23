package com.doctorappointment.service;

import com.doctorappointment.exception.ResourceNotFoundException;
import com.doctorappointment.model.Doctor;
import com.doctorappointment.repository.AppointmentRepository;
import com.doctorappointment.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public List<Doctor> findAll(String keyword) {
        if (keyword == null || keyword.isBlank()) return doctorRepository.findAll();
        return doctorRepository.search(keyword.trim());
    }

    public Doctor findById(String id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found."));
    }

    public Doctor save(Doctor doctor) {
        if (doctor.getCreatedAt() == null) doctor.setCreatedAt(LocalDateTime.now());
        return doctorRepository.save(doctor);
    }

    public Doctor update(String id, Doctor form) {
        Doctor doctor = findById(id);
        doctor.setName(form.getName());
        doctor.setSpecialization(form.getSpecialization());
        doctor.setPhone(form.getPhone());
        doctor.setEmail(form.getEmail());
        doctor.setAvailableDays(form.getAvailableDays());
        doctor.setAvailableTime(form.getAvailableTime());
        return doctorRepository.save(doctor);
    }

    public void delete(String id) {
        if (appointmentRepository.existsByDoctorId(id)) {
            throw new IllegalStateException("This doctor has appointments. Delete or reassign those appointments first.");
        }
        doctorRepository.deleteById(id);
    }

    public long count() {
        return doctorRepository.count();
    }
}
