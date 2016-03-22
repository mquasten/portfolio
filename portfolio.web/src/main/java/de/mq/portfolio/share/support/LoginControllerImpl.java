package de.mq.portfolio.share.support;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import de.mq.portfolio.support.UserModelImpl;
@Component("loginController")
public class LoginControllerImpl {
	
	@Autowired
	@Qualifier("authentificationManager")
	private AuthenticationManager authenticationManager;
	
	public final String login(LoginAO login) {
		final SecurityContext securityConntext =  SecurityContextHolder.getContext();
		try {
			final Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getName(), login.getPassword()));
			securityConntext.setAuthentication(new UsernamePasswordAuthenticationToken(new UserModelImpl((String) auth.getPrincipal()), "", new ArrayList<>()));
		} catch (final AuthenticationException ex ) {
			
			return String.format("login?faces-redirect=true&name=%s&message=login_%s", login.getName() , StringUtils.uncapitalize(ex.getClass().getSimpleName()));
		}
	   
		
		return "shares?faces-redirect=true";
	}

}
