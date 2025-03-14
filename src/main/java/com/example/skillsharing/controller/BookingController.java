	package com.example.skillsharing.controller;
	
	import com.example.skillsharing.model.Booking;
	import com.example.skillsharing.model.BookingStatus;
	import com.example.skillsharing.model.User;
	import com.example.skillsharing.service.BookingService;
	import com.example.skillsharing.service.UserService;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.security.core.context.SecurityContextHolder;
	import org.springframework.security.core.userdetails.UserDetails;
	import org.springframework.stereotype.Controller;
	import org.springframework.ui.Model;
	import org.springframework.web.bind.annotation.GetMapping;
	import org.springframework.web.bind.annotation.ModelAttribute;
	import org.springframework.web.bind.annotation.PostMapping;
	import org.springframework.web.bind.annotation.RequestParam;
	
	import java.time.LocalDateTime;
	import java.util.List;
	
	@Controller
	public class BookingController {
	
		@Autowired
		private BookingService bookingService;
	
		@Autowired
		private UserService userService;
	
		@GetMapping("/bookings/create")
		public String showBookingForm(@RequestParam Long workerId, @RequestParam String skillName, Model model) {
			String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
					.getUsername();
			User requester = userService.findByEmail(email);
			User worker = userService.getUserById(workerId);
	
			Booking booking = new Booking();
			booking.setWorker(worker);
			booking.setRequester(requester);
	
			model.addAttribute("booking", booking);
			model.addAttribute("skillName", skillName);
			return "bookings/create-booking";
		}
	
	//	@PostMapping("/bookings/create")
	//	public String createBooking(@ModelAttribute Booking booking, @RequestParam String startTime,
	//			@RequestParam String endTime) {
	//		booking.setStartTime(LocalDateTime.parse(startTime));
	//		booking.setEndTime(LocalDateTime.parse(endTime));
	//		booking.setStatus(BookingStatus.PENDING);
	//
	//		bookingService.saveBooking(booking);
	//		return "redirect:/requester/dashboard";
	//	}
		
		@PostMapping("/bookings/create")
		public String createBooking(@ModelAttribute Booking booking, @RequestParam String startTime,
		                            @RequestParam String endTime) {
		    booking.setStartTime(LocalDateTime.parse(startTime));
		    booking.setEndTime(LocalDateTime.parse(endTime));
		    booking.setStatus(BookingStatus.PENDING);
	
		    Booking savedBooking = bookingService.saveBooking(booking);
		    System.out.println("Booking saved with ID: " + savedBooking.getId());
		    return "redirect:/requester/dashboard";
		}
	
	
		@GetMapping("/worker/bookings")
		public String getWorkerBookings(Model model) {
			String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
					.getUsername();
			User worker = userService.findByEmail(email);
	
			if (worker != null) {
				List<Booking> bookings = bookingService.getBookingsByWorker(worker.getId());
	
				System.out.println("Bookings fetched: " + bookings.size());
				bookings.forEach(booking -> {
					System.out.println("Booking ID: " + booking.getId());
					System.out.println("Requester: "
							+ (booking.getRequester() != null ? booking.getRequester().getName() : "Requester is NULL"));
					System.out.println(
							"Worker: " + (booking.getWorker() != null ? booking.getWorker().getName() : "Worker is NULL"));
				});
	
				model.addAttribute("bookings", bookings);
				return "worker/bookings";
			}
	
			return "error";
		}
	
		@PostMapping("/bookings/update-status")
		public String updateBookingStatus(@RequestParam Long bookingId, @RequestParam BookingStatus status) {
			Booking booking = bookingService.getBookingById(bookingId);
			if (booking != null) {
				booking.setStatus(status);
				bookingService.saveBooking(booking);
			}
			return "redirect:/worker/bookings?workerId=" + booking.getWorker().getId();
		}
	
	//	@GetMapping("/requester/bookings") // Changed URL
	//	public String showRequesterBookings(Model model, @RequestParam Long requesterId) {
	//		model.addAttribute("bookings", bookingService.getBookingsByRequester(requesterId));
	//		return "requester/bookings"; // Also, create a separate Thymeleaf template for bookings
	//	}
		
		@GetMapping("/requester/bookings")
		public String showRequesterBookings(Model model) {
		    String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
		    User requester = userService.findByEmail(email);
		    List<Booking> bookings = bookingService.getBookingsByRequester(requester.getId());
	
		    System.out.println("Total bookings found: " + bookings.size()); // Debugging line
		    model.addAttribute("bookings", bookings);
		    return "requester/bookings";
		}
	
	
	}
