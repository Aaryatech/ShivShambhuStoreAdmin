package com.ats.shivstore.model.login;

import com.ats.shivstore.model.ErrorMessage;

public class UserResponse {
	User user;
	ErrorMessage errorMessage;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public ErrorMessage getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(ErrorMessage errorMessage) {
		this.errorMessage = errorMessage;
	}
	@Override
	public String toString() {
		return "UserResponse [user=" + user + ", errorMessage=" + errorMessage + "]";
	}
	
	

}
