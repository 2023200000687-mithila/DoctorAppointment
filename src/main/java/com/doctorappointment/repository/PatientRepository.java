package com.doctorappointment.repository;

import com.doctorappointment.model.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PatientRepository extends MongoRepository<Patient, String> {
    Optional<Patient> findByEmailIgnoreCase(String email);
}
