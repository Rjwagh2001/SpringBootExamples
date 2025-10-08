package com.rahul.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginData {

	@NotBlank(message = "User will not be blank!!")
	@Size(min = 3, max = 12, message = "User name must have 3-12 characters")
	private String userName;

	@NotBlank(message = "Email cannot be blank!!")
	@Email(message = "Enter a valid email!!")
	private String userEmail;

	private boolean checkMe; // ✅ boolean type is correct

	public LoginData() {
	}

	public LoginData(String userName, String userEmail, boolean checkMe) {
		this.userName = userName;
		this.userEmail = userEmail;
		this.checkMe = checkMe;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public boolean isCheckMe() {
		return checkMe;
	}

	public void setCheckMe(boolean checkMe) {
		this.checkMe = checkMe;
	}

	@Override
	public String toString() {
		return "LoginData [userName=" + userName + ", userEmail=" + userEmail + ", checkMe=" + checkMe + "]";
	}
}
