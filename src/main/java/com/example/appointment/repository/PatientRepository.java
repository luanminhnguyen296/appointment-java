package com.example.appointment.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.appointment.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Tìm kiếm theo tên (IgnoreCase)
    List<Patient> findByFullNameContainingIgnoreCase(String name);

    // Tìm kiếm theo số điện thoại
    List<Patient> findByPhoneContaining(String phone);

    // Tìm theo email
    Patient findByEmail(String email);

    // Kiểm tra tồn tại
    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    // Tìm kiếm nâng cao: Tên hoặc Số điện thoại hoặc Email
    List<Patient> findByFullNameContainingIgnoreCaseOrPhoneContainingOrEmailContainingIgnoreCase(String name,
            String phone, String email);

    // Hỗ trợ phân trang cho tìm kiếm nâng cao
    Page<Patient> findByFullNameContainingIgnoreCaseOrPhoneContainingOrEmailContainingIgnoreCase(String name,
            String phone, String email, Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE " +
            "(:gender IS NULL OR p.gender = :gender OR :gender = '') AND " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "p.phone LIKE CONCAT('%', :keyword, '%') OR LOWER(p.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Patient> findAllWithFilter(@Param("keyword") String keyword, @Param("gender") String gender,
            Pageable pageable);
}
