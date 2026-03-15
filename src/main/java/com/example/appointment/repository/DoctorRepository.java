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

        @Query(value = "SELECT DISTINCT d FROM Doctor d " +
                        "LEFT JOIN d.specialty s " +
                        "LEFT JOIN d.hospitals h WHERE " +
                        "(:keyword IS NULL OR :keyword = '' OR LOWER(d.name) LIKE LOWER(:keyword) OR (s IS NOT NULL AND LOWER(s.name) LIKE LOWER(:keyword))) AND "
                        +
                        "(:specialtyId IS NULL OR s.id = :specialtyId) AND " +
                        "(:hospitalId IS NULL OR h.id = :hospitalId) AND " +
                        "(:gender IS NULL OR :gender = '' OR d.gender = :gender)", countQuery = "SELECT COUNT(DISTINCT d) FROM Doctor d "
                                        +
                                        "LEFT JOIN d.specialty s " +
                                        "LEFT JOIN d.hospitals h WHERE " +
                                        "(:keyword IS NULL OR :keyword = '' OR LOWER(d.name) LIKE LOWER(:keyword) OR (s IS NOT NULL AND LOWER(s.name) LIKE LOWER(:keyword))) AND "
                                        +
                                        "(:specialtyId IS NULL OR s.id = :specialtyId) AND " +
                                        "(:hospitalId IS NULL OR h.id = :hospitalId) AND " +
                                        "(:gender IS NULL OR :gender = '' OR d.gender = :gender)")
        Page<Doctor> findWithFilters(@Param("keyword") String keyword,
                        @Param("specialtyId") Long specialtyId,
                        @Param("hospitalId") Long hospitalId,
                        @Param("gender") String gender,
                        Pageable pageable);
}
