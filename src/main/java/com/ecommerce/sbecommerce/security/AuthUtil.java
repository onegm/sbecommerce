package com.ecommerce.sbecommerce.security;

import com.ecommerce.sbecommerce.model.User;
import com.ecommerce.sbecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    @Autowired
    UserService userService;

    public Long loggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
        return user.getId();
    }

    public User loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.loadUserByUsername(authentication.getName());
        return user;
    }
}
