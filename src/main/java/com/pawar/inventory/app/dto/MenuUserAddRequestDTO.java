package com.pawar.inventory.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MenuUserAddRequestDTO {

	@NotBlank(message = "First name is required")
	@Size(max = 255, message = "First name must not exceed 255 characters")
	private String firstname;

	@Size(max = 255, message = "Middle name must not exceed 255 characters")
	private String middlename;

	@NotBlank(message = "Last name is required")
	@Size(max = 255, message = "Last name must not exceed 255 characters")
	private String lastname;

	@NotBlank(message = "Username is required")
	@Size(max = 255, message = "Username must not exceed 255 characters")
	private String username;

	@NotBlank(message = "Password is required")
	@Size(max = 255, message = "Password must not exceed 255 characters")
	private String password;

	@NotBlank(message = "Email is required")
	@Email(message = "Email must be valid")
	private String email;

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getMiddlename() {
		return middlename;
	}

	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}