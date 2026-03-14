package com.wms.identity.controller;

import com.wms.common.enums.UserRole;
import com.wms.common.enums.UserStatus;
import com.wms.identity.entity.User;
import com.wms.identity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public User getUser(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public List<User> filterUsers(
        @RequestParam Optional<String> email,
        @RequestParam Optional<UserRole> role,
        @RequestParam Optional<UserStatus> status) {
        return userService.filterUsers(email, role, status);
    }
}
