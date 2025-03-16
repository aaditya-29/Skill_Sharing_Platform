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
		// ✅ Ensure Optional is properly handled
		Optional<Booking> optionalBooking = bookingRepository.findById(id);
		return optionalBooking.orElse(null);
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
}
