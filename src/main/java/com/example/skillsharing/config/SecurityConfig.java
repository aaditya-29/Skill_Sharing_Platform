package com.example.skillsharing.config;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.skillsharing.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

	private final CustomUserDetailsService customUserDetailsService;

	public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
		this.customUserDetailsService = customUserDetailsService;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf().and()
				.authorizeHttpRequests(auth -> auth.requestMatchers("/", "/auth/**", "/js/**", "/images/**", "/about")
						.permitAll().requestMatchers(HttpMethod.POST, "/auth/verify-otp", "/auth/resend-otp")
						.permitAll().requestMatchers("/dashboard/worker").hasRole("WORKER")
						.requestMatchers("/dashboard/requester", "/requester/payment/**").hasRole("REQUESTER")
						.requestMatchers("/profile/**").authenticated().anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/auth/login").loginProcessingUrl("/auth/login")
						.usernameParameter("email").passwordParameter("password").defaultSuccessUrl("/dashboard", true)
						.failureUrl("/auth/login?error=true").permitAll())
				.logout(logout -> logout.logoutUrl("/auth/logout").logoutSuccessUrl("/auth/login")
						.invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll());

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(customUserDetailsService)
				.passwordEncoder(passwordEncoder()) // you already use passwordEncoder in registration
				.and().build();
	}


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
