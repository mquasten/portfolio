package de.mq.portfolio.shareportfolio.support;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;


public class AuthentificationManagerImpl  implements AuthenticationManager{

	@Override
	public Authentication authenticate(Authentication arg0) throws AuthenticationException {
		System.out.println("*********************************");
		return null;
	}

}
