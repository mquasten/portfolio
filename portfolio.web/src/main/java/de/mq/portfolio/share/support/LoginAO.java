package de.mq.portfolio.share.support;



import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("login")
@Scope("view")
public class LoginAO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	
	private String password;
	
	private String message;

	
	public String getMessage() {
		return message;
	}
	

	public void setMessage(String message) {
		this.message = message;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
