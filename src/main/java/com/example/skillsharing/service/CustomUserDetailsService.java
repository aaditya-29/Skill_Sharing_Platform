package com.example.skillsharing.service;

import com.example.skillsharing.model.CustomUserDetails;
import com.example.skillsharing.model.User;
import com.example.skillsharing.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import javax.security.auth.login.AccountNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
	    System.out.println("Loading user for login: " + user.getEmail() + ", enabled=" + user.isEnabled());

		return new CustomUserDetails(user); // ✅ Return your custom class here
	}
}
