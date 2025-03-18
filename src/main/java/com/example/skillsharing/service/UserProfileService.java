//package com.example.skillsharing.service;
//
//import com.example.skillsharing.model.User;
//import com.example.skillsharing.model.UserProfile;
//import com.example.skillsharing.repository.UserProfileRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserProfileService {
//   @Autowired
//   private UserProfileRepository userProfileRepository;
//
//   @Autowired
//   private UserService userService; // To get current user
//
//   public UserProfile getProfile() {
//       User currentUser = userService.getCurrentUser();
//       return userProfileRepository.findByUser(currentUser)
//               .orElse(new UserProfile());
//   }
//
//   public void saveProfile(UserProfile profile) {
//       User currentUser = userService.getCurrentUser();
//       profile.setUser(currentUser);
//       userProfileRepository.save(profile);
//   }
//
//   // Additional Methods
//
//   public void updateProfile(UserProfile profile) {
//       User currentUser = userService.getCurrentUser();
//       profile.setUser(currentUser);
//       userProfileRepository.save(profile);
//   }
//
//   
//}
