package com.example.appointment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.appointment.model.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a JOIN a.patient p JOIN a.doctor d WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Appointment> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT a FROM Appointment a " +
            "JOIN a.doctor d " +
            "LEFT JOIN d.specialty s " +
            "JOIN a.patient p " +
            "WHERE (:specialtyId IS NULL OR s.id = :specialtyId) " +
            "AND (:doctorId IS NULL OR d.id = :doctorId) " +
            "AND (:patientId IS NULL OR p.id = :patientId) " +
            "AND (a.appointmentDate >= :startDate) " +
            "AND (a.appointmentDate <= :endDate)")
    java.util.List<Appointment> findByFilters(
            @Param("specialtyId") Long specialtyId,
            @Param("doctorId") Long doctorId,
            @Param("patientId") Long patientId,
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate);
}
