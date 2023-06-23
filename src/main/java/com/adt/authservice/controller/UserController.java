/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.adt.authservice.controller;

import javax.validation.Valid;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adt.authservice.event.OnUserAccountChangeEvent;
import com.adt.authservice.event.OnUserLogoutSuccessEvent;
import com.adt.authservice.exception.UpdatePasswordException;
import com.adt.authservice.model.CustomUserDetails;
import com.adt.authservice.model.payload.ApiResponse;
import com.adt.authservice.model.payload.LogOutRequest;
import com.adt.authservice.model.payload.UpdatePasswordRequest;
import com.adt.authservice.security.JwtTokenValidator;
import com.adt.authservice.service.AuthService;
import com.adt.authservice.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private final AuthService authService;
	private final UserService userService;
	private final ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	JwtTokenValidator jwtTokenValidator;

	@Autowired
	public UserController(AuthService authService, UserService userService,
			ApplicationEventPublisher applicationEventPublisher) {
		this.authService = authService;
		this.userService = userService;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	/**
	 * Gets the current user profile of the logged in user
	 */
	@GetMapping("/me")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity getUserProfile() {
		CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		LOGGER.info(customUserDetails.getUsername() + " has role: " + customUserDetails.getRoles());
		return ResponseEntity.ok("Hello. This is about me");
	}

	/**
	 * Returns all admins in the system. Requires Admin access
	 */
	@GetMapping("/admins")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity getAllAdmins() {
		LOGGER.info("Inside secured resource with admin");
		return ResponseEntity.ok("Hello. This is about admins");
	}

	/**
	 * Updates the password of the current logged in user
	 */
	@PostMapping("/password/update")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity updateUserPassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {
		CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		return authService.updatePassword(customUserDetails, updatePasswordRequest).map(updatedUser -> {
			OnUserAccountChangeEvent onUserPasswordChangeEvent = new OnUserAccountChangeEvent(updatedUser,
					"Update Password", "Change successful");
			applicationEventPublisher.publishEvent(onUserPasswordChangeEvent);
			return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully"));
		}).orElseThrow(() -> new UpdatePasswordException("--Empty--", "No such user present."));
	}

	/**
	 * Log the user out from the app/device. Release the refresh token associated
	 * with the user device.
	 */
	@PostMapping("/logout")
	public ResponseEntity logoutUser(@Valid @RequestBody LogOutRequest logOutRequest) {
		CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		userService.logoutUser(customUserDetails, logOutRequest);
		Object credentials = SecurityContextHolder.getContext().getAuthentication().getCredentials();

		OnUserLogoutSuccessEvent logoutSuccessEvent = new OnUserLogoutSuccessEvent(customUserDetails.getEmail(),
				credentials.toString(), logOutRequest);
		applicationEventPublisher.publishEvent(logoutSuccessEvent);
		return ResponseEntity.ok(new ApiResponse(true, "Log out successful"));
	}

	/**
	 * This method is used to call from API GateWay.
	 * 
	 * @return
	 */
	@GetMapping("/isuservalid")
	public ResponseEntity<String> isUserValid() {
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}
}
