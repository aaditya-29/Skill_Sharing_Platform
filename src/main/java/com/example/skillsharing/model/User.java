package com.example.skillsharing.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(name = "rating")
	private double rating; // Stores the average rating of the user

	@Column(nullable = false, unique = true)
	private String contactNumber; // Contact number is required

	@Column(nullable = false)
	private String address; // Address is required

	@Lob
	@Column(columnDefinition = "LONGBLOB")
	private byte[] profilePicture; // Store image as a BLOB

	// OTP verification
	private String otp;
	private LocalDateTime otpGeneratedTime;
	@Column(nullable = false)
	private boolean enabled = false; // Only true after OTP verification

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public LocalDateTime getOtpGeneratedTime() {
		return otpGeneratedTime;
	}

	public void setOtpGeneratedTime(LocalDateTime otpGeneratedTime) {
		this.otpGeneratedTime = otpGeneratedTime;
	}

	public void setEnabled(boolean enabled) {
		System.out.println("Calling set enabled , enabled = " + enabled); // 🔥 Must say false

		this.enabled = enabled;
	}

	public boolean getEnabled() {
		return enabled;
	}

	// Default constructor
	public User() {
	}

	// Getters & Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public byte[] getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(byte[] profilePicture) {
		this.profilePicture = profilePicture;
	}

	public String getProfilePicUrl() {
		if (profilePicture != null && profilePicture.length > 0) {
			return "data:image/png;base64," + Base64.getEncoder().encodeToString(profilePicture);
		}
		return "/uploads/default.jpg"; // Default image in static folder
	}

	// Spring Security methods
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(() -> "ROLE_" + role.name());
	}

	public String getUsername() {
		return email;
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
