package com.example.demo.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.splunk.logging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.ApplicationConfiguration;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	Logger logger = LoggerFactory.getLogger(ApplicationConfiguration.getSplunkLogName());
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		Optional<User> findUserResult = userRepository.findById(id);

		if (!findUserResult.isPresent()) {
			logger.error("FIND_USER_BY_ID_FAILED: ID_NOT_FOUND");
			return ResponseEntity.notFound().build();
		}

		logger.info("FIND_USER_BY_ID_SUCCESS");
		return ResponseEntity.ok(findUserResult.get());
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);

		if (user == null) {
			logger.error("FIND_USER_BY_USERNAME_FAILED: USERNAME_NOT_FOUND");
			return ResponseEntity.notFound().build();
		}

		logger.info("FIND_USER_BY_USERNAME_SUCCESS");
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		if (userRepository.findByUsername(createUserRequest.getUsername()) != null) {
			logger.error("CREATE_USER_FAILED: USERNAME_CHECK");
			return ResponseEntity.badRequest().build();
		}

		User user = new User();
		user.setUsername(createUserRequest.getUsername());

		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);

		if (createUserRequest.getPassword().length() < 7
				|| !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			logger.error("CREATE_USER_FAILED: PASSWORD_CHECK");
			return ResponseEntity.badRequest().build();
		}

		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

		userRepository.save(user);
		logger.info("CREATE_USER_SUCCESS");
		return ResponseEntity.ok(user);
	}
	
}
