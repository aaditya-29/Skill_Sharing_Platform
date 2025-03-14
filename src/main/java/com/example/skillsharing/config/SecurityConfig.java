package com.example.skillsharing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf().disable()
				.authorizeHttpRequests(auth -> auth.requestMatchers("/", "/auth/register", "/auth/login").permitAll()
						.requestMatchers("/dashboard/worker").hasRole("WORKER").requestMatchers("/dashboard/requester")
						.hasRole("REQUESTER").anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/auth/login").loginProcessingUrl("/auth/login")
						.usernameParameter("email") // ✅ Ensure login uses "email" as username
						.passwordParameter("password") // ✅ Ensure password field is read correctly
						.defaultSuccessUrl("/dashboard", true).failureUrl("/auth/login?error=true").permitAll())
				.logout(logout -> logout.logoutSuccessUrl("/").permitAll());

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
