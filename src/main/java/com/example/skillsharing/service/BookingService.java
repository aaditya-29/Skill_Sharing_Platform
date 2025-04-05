package com.example.skillsharing.service;

import com.example.skillsharing.model.Booking;
import com.example.skillsharing.model.User;
import com.example.skillsharing.repository.BookingRepository;
import com.example.skillsharing.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // ✅ Import the missing Optional class

@Service
public class BookingService {

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private UserRepository userRepository;

	public Booking saveBooking(Booking booking) {
		return bookingRepository.save(booking); // This returns the saved booking
	}

	public List<Booking> getBookingsByWorker(Long workerId) {
		return bookingRepository.findByWorkerId(workerId);
	}

	public List<Booking> getBookingsByRequester(Long requesterId) {
		return bookingRepository.findByRequesterId(requesterId);
	}

	public Booking getBookingById(Long id) {
		Optional<Booking> booking = bookingRepository.findById(id);

		if (booking.isPresent()) {
			System.out.println("📌 Booking found: ID=" + id + ", Status=" + booking.get().getStatus());
			return booking.get();
		} else {
			System.out.println("❌ ERROR: Booking ID " + id + " not found in the database.");
			return null;
		}
	}

	public List<Booking> getAllBookings() {
		return bookingRepository.findAll();
	}

	public List<Booking> getBookingsForWorker(String workerEmail) {
		// ✅ Ensure Optional is properly handled
		Optional<User> optionalWorker = userRepository.findByEmail(workerEmail);

		if (optionalWorker.isEmpty()) {
			return new ArrayList<>();
		}

		User worker = optionalWorker.get();
		return bookingRepository.findByWorker(worker);
	}

	public List<Booking> findCompletedBookingsForUser(Long userId) {
		return bookingRepository.findCompletedBookingsByUser(userId);
	}

	public List<Booking> findByRequesterId(Long requesterId) {
		return bookingRepository.findByRequesterId(requesterId);
	}

	public void deleteBooking(Long id) {
		bookingRepository.deleteById(id);
	}

	public void updateBooking(Booking booking) {
		bookingRepository.save(booking);
	}

	public List<Booking> getBookingsWithReportsByWorker(Long workerId) {
		return bookingRepository.findByWorkerIdWithInspectionReport(workerId);
	}
	public List<Booking> findByRequester(User requester) {
	    return bookingRepository.findByRequester(requester);
	}

	public List<Booking> findByWorker(User worker) {
	    return bookingRepository.findByWorker(worker);
	}


}
