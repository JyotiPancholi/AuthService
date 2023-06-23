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
package com.adt.authservice.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.adt.authservice.exception.UserLogoutException;
import com.adt.authservice.model.CustomUserDetails;
import com.adt.authservice.model.Role;
import com.adt.authservice.model.User;
import com.adt.authservice.model.UserDevice;
import com.adt.authservice.model.payload.LogOutRequest;
import com.adt.authservice.model.payload.RegistrationRequest;
import com.adt.authservice.repository.UserRepository;

@Service
public class UserService {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final RoleService roleService;
	private final UserDeviceService userDeviceService;
	private final RefreshTokenService refreshTokenService;

	@Autowired
	public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleService roleService,
			UserDeviceService userDeviceService, RefreshTokenService refreshTokenService) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.roleService = roleService;
		this.userDeviceService = userDeviceService;
		this.refreshTokenService = refreshTokenService;
	}

	/**
	 * Finds a user in the database by username
	 */
	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	/**
	 * Finds a user in the database by email
	 */
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	/**
	 * Find a user in db by id.
	 */
	public Optional<User> findById(Long Id) {
		return userRepository.findById(Id);
	}

	/**
	 * Save the user to the database
	 */
	public User save(User user) {
		return userRepository.save(user);
	}

	/**
	 * Check is the user exists given the email: naturalId
	 */
	public Boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	/**
	 * Check is the user exists given the username: naturalId
	 */
	public Boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}

	/**
	 * Creates a new user from the registration request
	 */
	public User createUser(RegistrationRequest registerRequest) {
		User newUser = new User();
		Boolean isNewUserAsAdmin = registerRequest.getRegisterAsAdmin();
		newUser.setEmail(registerRequest.getEmail());
		newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		newUser.setUsername(registerRequest.getUsername());
		newUser.addRoles(getRolesForNewUser(isNewUserAsAdmin));
		newUser.setActive(true);
		newUser.setEmailVerified(false);
		return newUser;
	}

	/**
	 * Performs a quick check to see what roles the new user could be assigned to.
	 *
	 * @return list of roles for the new user
	 */
	private Set<Role> getRolesForNewUser(Boolean isToBeMadeAdmin) {
		Set<Role> newUserRoles = new HashSet<>(roleService.findAll());
		if (!isToBeMadeAdmin) {
			newUserRoles.removeIf(Role::isAdminRole);
		}
		LOGGER.info("Setting user roles: " + newUserRoles);
		return newUserRoles;
	}

	/**
	 * Log the given user out and delete the refresh token associated with it. If no
	 * device id is found matching the database for this user, throw a log out
	 * exception.
	 */
	public void logoutUser(CustomUserDetails customUserDetails, LogOutRequest logOutRequest) {
		String deviceId = logOutRequest.getDeviceInfo().getDeviceId();
		List<UserDevice> userDeviceList = userDeviceService.findDeviceByUserId(customUserDetails.getId(), deviceId)
											.stream()
											.filter(device -> device.getDeviceId().equals(deviceId))
											.collect(Collectors.toList());
				
		Optional.of(userDeviceList).		
				orElseThrow(() -> new UserLogoutException(logOutRequest.getDeviceInfo().getDeviceId(),
						"Invalid device Id supplied. No matching device found for the given user "));

		userDeviceList.forEach(userDevice->{
			LOGGER.info("Removing refresh token associated with device [" + userDevice + "]");
			refreshTokenService.deleteById(userDevice.getRefreshToken().getId());
		});
	}
}
