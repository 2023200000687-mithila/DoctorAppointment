package com.doctorappointment.repository;

import com.doctorappointment.model.Appointment;
import com.doctorappointment.model.AppointmentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    List<Appointment> findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(String patientId);
    List<Appointment> findByDoctorIdOrderByAppointmentDateDescAppointmentTimeDesc(String doctorId);
    List<Appointment> findByStatusOrderByAppointmentDateAscAppointmentTimeAsc(AppointmentStatus status);
    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
            String doctorId, LocalDate date, LocalTime time, AppointmentStatus status);
    boolean existsByDoctorId(String doctorId);
    boolean existsByPatientId(String patientId);
}
