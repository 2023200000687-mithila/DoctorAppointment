package com.doctorappointment.repository;

import com.doctorappointment.model.Doctor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface DoctorRepository extends MongoRepository<Doctor, String> {
    @Query("{'$or':[{'name':{'$regex':?0,'$options':'i'}},{'specialization':{'$regex':?0,'$options':'i'}}]}")
    List<Doctor> search(String keyword);
}
