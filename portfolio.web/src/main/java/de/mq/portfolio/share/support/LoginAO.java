package de.mq.portfolio.share.support;



import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("login")
@Scope("request")
public class LoginAO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	
	private String password;

	
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
	
	public Authentication getAuthentification() {
		return new UsernamePasswordAuthenticationToken(name, password);
		
	}
}
