package com.adt.authservice.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Proxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.adt.authservice.model.audit.DateAudit;
import com.adt.authservice.validation.annotation.NullOrNotBlank;

@Table(catalog = "EmployeeDB", schema = "user_schema", name = "_EMPLOYEE")
@Entity(name = "_EMPLOYEE")
@Proxy(lazy = false)
public class User extends DateAudit implements UserDetails {

	@Id
	@Column(name = "EMPLOYEE_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
	@SequenceGenerator(name = "user_seq", allocationSize = 1)
	private Long id;

	@NaturalId
	@Column(name = "EMAIL", unique = true)
	@NotBlank(message = "User email cannot be null")
	private String email;

	@Column(name = "USERNAME", unique = true)
	@NullOrNotBlank(message = "Username can not be blank")
	private String username;

	@Column(name = "PASSWORD")
	@NotNull(message = "Password cannot be null")
	private String password;

	@Column(name = "FIRST_NAME")
	@NullOrNotBlank(message = "First name can not be blank")
	private String firstName;

	@Column(name = "LAST_NAME")
	@NullOrNotBlank(message = "Last name can not be blank")
	private String lastName;

	@Column(name = "IS_ACTIVE", nullable = false)
	private Boolean active;

	@ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE })
	@JoinTable(name = "USER_AUTHORITY", joinColumns = {
			@JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "EMPLOYEE_ID") }, inverseJoinColumns = {
					@JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID") })
	private Set<Role> roles = new HashSet<>();

	@Column(name = "IS_EMAIL_VERIFIED", nullable = false)
	private Boolean isEmailVerified;

	public User() {
		super();
	}

	public User(User user) {
		if (user != null) {
			id = user.getId();
			username = user.getUsername();
			password = user.getPassword();
			firstName = user.getFirstName();
			lastName = user.getLastName();
			email = user.getEmail();
			active = user.getActive();
			roles = user.getRoles();
			isEmailVerified = user.getEmailVerified();
		}
	}

	public void addRole(Role role) {
		roles.add(role);
		role.getUserList().add(this);
	}

	public void addRoles(Set<Role> roles) {
		roles.forEach(this::addRole);
	}

	public void removeRole(Role role) {
		roles.remove(role);
		role.getUserList().remove(this);
	}

	public void markVerificationConfirmed() {
		setEmailVerified(true);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> authorities) {
		roles = authorities;
	}

	public Boolean getEmailVerified() {
		return isEmailVerified;
	}

	public void setEmailVerified(Boolean emailVerified) {
		isEmailVerified = emailVerified;
	}

	@Override
	public String toString() {
		return "User{" + "id=" + id + ", email='" + email + '\'' + ", username='" + username + '\'' + ", password='"
				+ password + '\'' + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", active="
				+ active + ", roles=" + roles + ", isEmailVerified=" + isEmailVerified + '}';
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}
}