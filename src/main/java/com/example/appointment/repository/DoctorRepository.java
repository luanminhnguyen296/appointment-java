package com.example.appointment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.appointment.model.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query("SELECT d FROM Doctor d WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(d.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Doctor> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
