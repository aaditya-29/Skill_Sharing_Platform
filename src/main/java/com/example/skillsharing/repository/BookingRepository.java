package com.example.skillsharing.repository;

import com.example.skillsharing.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByWorkerId(Long workerId);
    List<Booking> findByRequesterId(Long requesterId);
}
