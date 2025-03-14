package com.example.skillsharing.service;

import com.example.skillsharing.model.Booking;
import com.example.skillsharing.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

//    public void saveBooking(Booking booking) {
//        bookingRepository.save(booking);
//    }

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
        return bookingRepository.findById(id).orElse(null);
    }
    
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    
    

}
