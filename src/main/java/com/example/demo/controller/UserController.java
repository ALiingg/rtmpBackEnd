package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import com.example.demo.service.serviceImpl.UserServiceImpl;
import com.example.demo.utils.Result;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.utils.randomUUID;

@RestController
@RequestMapping("/user")
public class UserController {

    // Inject UserService to handle user-related operations
    @Resource
    private UserService userService;

    // Inject UserServiceImpl for direct access to specific implementation details
    @Autowired
    private UserServiceImpl userServiceImpl;

    /**
     * Handles user login requests.
     * @param uname Username of the user trying to log in
     * @param password Password of the user
     * @return Result object containing user data and success message if login is successful,
     * or error message if login fails
     */
    @PostMapping("/login")
    public Result<User> loginController(@RequestParam String uname, @RequestParam String password) {
        // Call the login service to authenticate user
        User user = userService.loginService(uname, password);

        // Check if user exists
        if (user != null) {
            // Generate a unique token and assign it to the user
            String token = randomUUID.getUUID();
            user.setToken(token);

            // Update the token in the database
            userService.updateToken(user);
            System.out.println(token); // Print the token to console for debugging

            // Return success result with user information
            return Result.success(user, "Login successful!");
        } else {
            // Return error if authentication fails
            return Result.error("123", "Incorrect username or password!");
        }
    }

    /**
     * Handles user registration requests.
     * @param newUser New user object containing registration details
     * @return Result object with success message if registration is successful,
     * or error message if the username already exists
     */
    @PostMapping("/register")
    public Result<User> registController(@RequestBody User newUser) {
        // Call the registration service to create a new user
        User user = userService.registService(newUser);

        // Check if the user was successfully created
        if (user != null) {
            // Return success result with the new user's information
            return Result.success(user, "Registration successful!");
        } else {
            // Return error if username already exists
            return Result.error("456", "Username already exists!");
        }
    }

    /**
     * Handles token-based login requests.
     * @param token Unique token provided by the user
     * @return Result object containing user data and success message if token is valid,
     * or error message if token is invalid
     */
    @PostMapping("/tokenlogin")
    public Result<User> tokenLoginController(@RequestBody String token) {
        // Use the token to retrieve the user via the token service
        User user = userServiceImpl.tokenService(token);
        System.out.println(token); // Print the token to console for debugging

        // Check if a valid user was found
        if (user != null) {
            // Return success result with user information
            return Result.success(user, "Token valid");
        } else {
            // Return error if the token is invalid
            return Result.error("456", "Token invalid");
        }
    }
}
