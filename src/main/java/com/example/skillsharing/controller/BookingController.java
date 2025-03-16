//package com.example.skillsharing.controller;
//
//import com.example.skillsharing.model.Booking;
//import com.example.skillsharing.model.BookingStatus;
//import com.example.skillsharing.model.SkillListing;
//import com.example.skillsharing.model.User;
//import com.example.skillsharing.service.BookingService;
//import com.example.skillsharing.service.SkillListingService;
//import com.example.skillsharing.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Controller
//public class BookingController {
//
//	@Autowired
//	private BookingService bookingService;
//
//	@Autowired
//	private UserService userService;
//	
//	@Autowired
//	private SkillListingService skillListingService;
//
//	@GetMapping("/bookings/create")
//	public String showBookingForm(@RequestParam Long workerId, @RequestParam String skillName, Model model) {
//		String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
//				.getUsername();
//		User requester = userService.findByEmail(email);
//		User worker = userService.getUserById(workerId);
//
//		Booking booking = new Booking();
//		booking.setWorker(worker);
//		booking.setRequester(requester);
//
//		model.addAttribute("booking", booking);
//		model.addAttribute("skillName", skillName);
//		return "bookings/create-booking";
//	}
//
//	// @PostMapping("/bookings/create")
//	// public String createBooking(@ModelAttribute Booking booking, @RequestParam
//	// String startTime,
//	// @RequestParam String endTime) {
//	// booking.setStartTime(LocalDateTime.parse(startTime));
//	// booking.setEndTime(LocalDateTime.parse(endTime));
//	// booking.setStatus(BookingStatus.PENDING);
//	//
//	// bookingService.saveBooking(booking);
//	// return "redirect:/requester/dashboard";
//	// }
//
////		@PostMapping("/bookings/create")
////		public String createBooking(@ModelAttribute Booking booking, @RequestParam String startTime,
////		                            @RequestParam String endTime) {
////		    booking.setStartTime(LocalDateTime.parse(startTime));
////		    booking.setEndTime(LocalDateTime.parse(endTime));
////		    booking.setStatus(BookingStatus.PENDING);
////	
////		    Booking savedBooking = bookingService.saveBooking(booking);
////		    System.out.println("Booking saved with ID: " + savedBooking.getId());
////		    return "redirect:/requester/dashboard";
////		}
////	
//
//	@PostMapping("/bookings/create")
//	public String createBooking(@RequestParam Long skillId, @RequestParam LocalDateTime startTime,
//			@RequestParam LocalDateTime endTime) {
//		String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
//				.getUsername();
//		User requester = userService.findByEmail(email);
//
//		if (requester == null) {
//			System.out.println("❌ Error: Requester not found for email: " + email);
//			return "error";
//		}
//
//		SkillListing skillListing = skillListingService.getSkillById(skillId); // ✅ Ensure SkillListing is retrieved
//
//		if (skillListing == null) {
//			System.out.println("❌ Error: No skill found with ID: " + skillId);
//			return "error"; // ❌ Prevents null skill_id in the database
//		}
//
//		Booking booking = new Booking();
//		booking.setWorker(skillListing.getWorker()); // ✅ Assign worker
//		booking.setRequester(requester);
//		booking.setSkillListing(skillListing); // ✅ Ensure skill_id is assigned
//		booking.setStartTime(startTime);
//		booking.setEndTime(endTime);
//		booking.setStatus(BookingStatus.PENDING);
//
//		bookingService.saveBooking(booking);
//
//		System.out.println("✅ Booking Created! Skill ID: " + booking.getSkillListing().getId());
//		return "redirect:/requester/bookings";
//	}
//
//	@GetMapping("/worker/bookings")
//	public String getWorkerBookings(Model model) {
//		String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
//				.getUsername();
//		User worker = userService.findByEmail(email);
//
//		if (worker != null) {
//			List<Booking> bookings = bookingService.getBookingsByWorker(worker.getId());
//
//			System.out.println("Bookings fetched: " + bookings.size());
//			bookings.forEach(booking -> {
//				System.out.println("Booking ID: " + booking.getId());
//				System.out.println("Requester: "
//						+ (booking.getRequester() != null ? booking.getRequester().getName() : "Requester is NULL"));
//				System.out.println(
//						"Worker: " + (booking.getWorker() != null ? booking.getWorker().getName() : "Worker is NULL"));
//			});
//
//			model.addAttribute("bookings", bookings);
//			return "worker/bookings";
//		}
//
//		return "error";
//	}
//
//	@PostMapping("/bookings/update-status")
//	public String updateBookingStatus(@RequestParam Long bookingId, @RequestParam BookingStatus status) {
//		Booking booking = bookingService.getBookingById(bookingId);
//		if (booking != null) {
//			booking.setStatus(status);
//			bookingService.saveBooking(booking);
//		}
//		return "redirect:/worker/bookings?workerId=" + booking.getWorker().getId();
//	}
//
//	// @GetMapping("/requester/bookings") // Changed URL
//	// public String showRequesterBookings(Model model, @RequestParam Long
//	// requesterId) {
//	// model.addAttribute("bookings",
//	// bookingService.getBookingsByRequester(requesterId));
//	// return "requester/bookings"; // Also, create a separate Thymeleaf template
//	// for bookings
//	// }
//
//	@GetMapping("/requester/bookings")
//	public String showRequesterBookings(Model model) {
//		String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
//				.getUsername();
//		User requester = userService.findByEmail(email);
//		List<Booking> bookings = bookingService.getBookingsByRequester(requester.getId());
//
//		System.out.println("Total bookings found: " + bookings.size()); // Debugging line
//		model.addAttribute("bookings", bookings);
//		return "requester/bookings";
//	}
//
//}
//
//package com.example.skillsharing.controller;
//
//import com.example.skillsharing.model.Booking;
//import com.example.skillsharing.model.BookingStatus;
//import com.example.skillsharing.model.SkillListing;
//import com.example.skillsharing.model.User;
//import com.example.skillsharing.service.BookingService;
//import com.example.skillsharing.service.SkillListingService;
//import com.example.skillsharing.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.security.Principal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Controller
//public class BookingController {
//
//	@Autowired
//	private BookingService bookingService;
//
//	@Autowired
//	private UserService userService;
//
//	@Autowired
//	private SkillListingService skillListingService;
//
//	@GetMapping("/requester/bookings")
//	public String showRequesterBookings(Model model, Principal principal) {
//		String email = principal.getName();
//		User requester = userService.findByEmail(email);
//
//		if (requester != null) {
//			List<Booking> bookings = bookingService.getBookingsByRequester(requester.getId());
//			System.out.println("📌 Retrieved " + bookings.size() + " bookings for requester: " + requester.getName());
//			model.addAttribute("bookings", bookings);
//		} else {
//			model.addAttribute("bookings", new ArrayList<>()); // If no user found, return empty list
//		}
//
//		return "requester/bookings"; // Ensure Thymeleaf template is correctly named
//	}
//
//	@GetMapping("/skills/skills")
//	public String ShowSkills(Model model, Principal principal) {
//
//		return "skills/skills";
//
//	}
//
//	@GetMapping("/worker/bookings")
//	public String getWorkerBookings(Model model, Principal principal) {
//		String email = principal.getName();
//		User worker = userService.findByEmail(email);
//
//		if (worker != null) {
//			List<Booking> bookings = bookingService.getBookingsByWorker(worker.getId());
//			model.addAttribute("bookings", bookings);
//		} else {
//			model.addAttribute("bookings", new ArrayList<>());
//		}
//
//		return "worker/bookings"; // En sure it returns the correct worker dashboard
//	}
//
//	@PostMapping("/bookings/create")
//	public String createBooking(@RequestParam Long skillId, @RequestParam Long workerId, @RequestParam String startTime,
//			@RequestParam String endTime) {
//		String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
//				.getUsername();
//		User requester = userService.findByEmail(email);
//		SkillListing skillListing = skillListingService.getSkillById(skillId);
//		User worker = userService.getUserById(workerId);
//
//		if (requester == null || skillListing == null || worker == null) {
//			return "error";
//		}
//
//		LocalDateTime parsedStartTime = LocalDateTime.parse(startTime);
//		LocalDateTime parsedEndTime = LocalDateTime.parse(endTime);
//
//		Booking booking = new Booking();
//		booking.setWorker(worker);
//		booking.setRequester(requester);
//		booking.setSkillListing(skillListing);
//		booking.setStartTime(parsedStartTime);
//		booking.setEndTime(parsedEndTime);
//		booking.setStatus(BookingStatus.PENDING);
//
//		bookingService.saveBooking(booking);
//		return "redirect:/requester/bookings";
//	}
//
//	@PostMapping("/bookings/update-status")
//	public String updateBookingStatus(@RequestParam Long bookingId, @RequestParam BookingStatus status,
//			Principal principal) {
//		Booking booking = bookingService.getBookingById(bookingId);
//
//		if (booking != null) {
//			booking.setStatus(status);
//			bookingService.saveBooking(booking);
//
//			// Redirect worker back to their dashboard
//			return "redirect:/worker/bookings";
//		}
//
//		return "error";
//	}
//}

package com.example.skillsharing.controller;

import com.example.skillsharing.model.Booking;
import com.example.skillsharing.model.BookingStatus;
import com.example.skillsharing.model.SkillListing;
import com.example.skillsharing.model.User;
import com.example.skillsharing.service.BookingService;
import com.example.skillsharing.service.SkillListingService;
import com.example.skillsharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BookingController {

	@Autowired
	private BookingService bookingService;

	@Autowired
	private UserService userService;

	@Autowired
	private SkillListingService skillListingService;

	@GetMapping("/requester/bookings")
	public String showRequesterBookings(Model model, Principal principal) {
		String email = principal.getName();
		User requester = userService.findByEmail(email);

		if (requester != null) {
			List<Booking> bookings = bookingService.getBookingsByRequester(requester.getId());
			System.out.println("📌 Retrieved " + bookings.size() + " bookings for requester: " + requester.getName());
			model.addAttribute("bookings", bookings);
		} else {
			model.addAttribute("bookings", new ArrayList<>());
		}

		return "requester/bookings";
	}

	@GetMapping("/worker/bookings")
	public String getWorkerBookings(Model model, Principal principal) {
		String email = principal.getName();
		User worker = userService.findByEmail(email);

		if (worker != null) {
			List<Booking> bookings = bookingService.getBookingsByWorker(worker.getId());
			model.addAttribute("bookings", bookings);
		} else {
			model.addAttribute("bookings", new ArrayList<>());
		}

		return "worker/bookings";
	}

	@PostMapping("/bookings/create")
	public String createBooking(@RequestParam Long skillId, @RequestParam Long workerId, @RequestParam String startTime,
			@RequestParam String endTime) {
		// Get the logged-in user's email
		String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getUsername();
		User requester = userService.findByEmail(email);

		// Retrieve skill listing and worker
		SkillListing skillListing = skillListingService.getSkillById(skillId);
		User worker = userService.getUserById(workerId);

		// Validation: Ensure requester and worker are not the same
		if (requester == null || skillListing == null || worker == null || requester.getId().equals(worker.getId())) {
			System.out.println("⚠️ Requester and Worker cannot be the same user!");
			return "error"; // Redirect to an error page or handle it gracefully
		}

		// Parse start and end times
		LocalDateTime parsedStartTime = LocalDateTime.parse(startTime);
		LocalDateTime parsedEndTime = LocalDateTime.parse(endTime);

		// Create and save booking
		Booking booking = new Booking();
		booking.setWorker(worker);
		booking.setRequester(requester);
		booking.setSkillListing(skillListing);
		booking.setStartTime(parsedStartTime);
		booking.setEndTime(parsedEndTime);
		booking.setStatus(BookingStatus.PENDING);

		bookingService.saveBooking(booking);
		return "redirect:/requester/bookings";
	}

	@PostMapping("/bookings/update-status")
	public String updateBookingStatus(@RequestParam Long bookingId, @RequestParam BookingStatus status,
			Principal principal) {
		Booking booking = bookingService.getBookingById(bookingId);

		if (booking != null) {
			booking.setStatus(status);
			bookingService.saveBooking(booking);
			return "redirect:/worker/bookings";
		}

		return "error";
	}
}
