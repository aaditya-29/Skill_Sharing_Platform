package com.example.skillsharing.repository;

import com.example.skillsharing.model.Booking;
import com.example.skillsharing.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	List<Booking> findByWorkerId(Long workerId);

	List<Booking> findByRequesterId(Long requesterId);

	@Query("SELECT b FROM Booking b LEFT JOIN FETCH b.skillListing WHERE b.requester.id = :requesterId")
	List<Booking> getBookingsByRequester(@Param("requesterId") Long requesterId);


	@Query("SELECT b FROM Booking b WHERE b.status = 'COMPLETED' AND (b.requester.id = :userId OR b.worker.id = :userId)")
	List<Booking> findCompletedBookingsByUser(@Param("userId") Long userId);

//	@Query("SELECT b FROM Booking b WHERE b.id = :bookingId")
//	Booking findById(@Param("bookingId") Long bookingId);

	Optional<Booking> findById(Long id);

	@Query("SELECT b FROM Booking b LEFT JOIN FETCH b.inspectionReport WHERE b.worker.id = :workerId")
	List<Booking> findByWorkerIdWithInspectionReport(@Param("workerId") Long workerId);
	
	List<Booking> findByRequester(User requester);
	List<Booking> findByWorker(User worker);


}
