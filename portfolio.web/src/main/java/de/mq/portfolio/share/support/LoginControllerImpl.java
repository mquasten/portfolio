package de.mq.portfolio.share.support;

import java.util.ArrayList;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

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
	
	static final String ERROR_PATTERN = "login?faces-redirect=true&name=%s&message=login_%s";
	static final String SUCCESS = "shares?faces-redirect=true";
	@Autowired
	@Qualifier("authentificationManager")
	private AuthenticationManager authenticationManager;
	
	public  String login(LoginAO login) {
		final SecurityContext securityConntext =  SecurityContextHolder.getContext();
		try {
			final Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getName(), login.getPassword()));
			
			securityConntext.setAuthentication(new UsernamePasswordAuthenticationToken(new UserModelImpl((String) auth.getPrincipal()), "", new ArrayList<>()));
		} catch (final AuthenticationException ex ) {
			
			return String.format(ERROR_PATTERN, login.getName() , StringUtils.uncapitalize(ex.getClass().getSimpleName().replaceFirst("Exception", "")));
		}
	   
		
		return SUCCESS;
	}
	
	public final String logout(final SecurityContext securityContext, final FacesContext facesContext) {
	
		securityContext.setAuthentication(null);
		((HttpSession) facesContext.getExternalContext().getSession(false)).invalidate();
		
		return facesContext.getViewRoot().getViewId() + "?faces-redirect=true" ; 
	}
	
	public final String language(final FacesContext facesContext, final String language) {
		return facesContext.getViewRoot().getViewId() + "?faces-redirect=true&language=" + language.toLowerCase().trim() ;
	}
	
	

}
