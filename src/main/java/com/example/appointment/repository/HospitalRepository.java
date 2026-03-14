package com.example.appointment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.appointment.model.Hospital;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    @Query("SELECT h FROM Hospital h WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(h.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(h.address) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Hospital> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
