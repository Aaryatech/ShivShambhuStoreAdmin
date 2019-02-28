package com.ats.shivstore.model.user;


public class LoginResUser {
	
	private boolean error;
	private String msg;
	private User user;
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	@Override
	public String toString() {
		return "LoginResUser [error=" + error + ", msg=" + msg + ", user=" + user + "]";
	}
	
	
}
