package com.wms.identity.service;

import com.wms.common.enums.UserStatus;
import com.wms.identity.entity.User;
import com.wms.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByEmailAndDeletedAtIsNull(email)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + email));

    boolean isActive = user.getStatus() == UserStatus.ACTIVE;

    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getEmail())
        .password(user.getPassword())
        .roles(user.getRole().name())
        .disabled(!isActive)
        .build();
  }
}
