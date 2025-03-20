package com.example.skillsharing.service;

import com.example.skillsharing.model.User;
import com.example.skillsharing.repository.FeedbackRepository;
import com.example.skillsharing.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final FeedbackRepository feedbackRepository; // ✅ Fixed null issue

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			FeedbackRepository feedbackRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.feedbackRepository = feedbackRepository;
	}

	public void saveUser(User user) {
		// Encode password only if it's not already encoded
		if (!user.getPassword().startsWith("$2a$")) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		userRepository.save(user);
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email).orElse(null);
	}

	public User findByContactNumber(String contactNumber) {
		return userRepository.findByContactNumber(contactNumber).orElse(null);
	}

	public User getUserById(Long id) {
		return userRepository.findById(id).orElse(null);
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public void updateUser(User user) {
		user.setRating(calculateAverageRating(user.getId())); // ✅ Automatically update rating
		userRepository.save(user);
	}

	public double calculateAverageRating(Long userId) {
		List<Double> ratings = feedbackRepository.findRatingsByUserId(userId);
		return ratings.isEmpty() ? 0.0 : ratings.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
	}

	public String saveProfilePicture(MultipartFile file, Long userId) throws IOException {
		String directory = "uploads/profile_pictures/";
		String fileName = userId + "_" + file.getOriginalFilename();
		Path path = Paths.get(directory + fileName);

		// ✅ Ensure directory exists
		Files.createDirectories(path.getParent());

		// ✅ Save file to disk
		Files.write(path, file.getBytes());

		return "/" + directory + fileName; // ✅ Ensure correct relative URL for web access
	}

	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}

}
