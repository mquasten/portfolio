package de.mq.portfolio.share.support;

import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import de.mq.portfolio.support.UserModelImpl;
@Component("loginController")
public class LoginControllerImpl {
	
	public final String login(Authentication authentication, FacesContext facesContext) {
		final SecurityContext securityConntext =  SecurityContextHolder.getContext();
		 if( ! authentication.getCredentials().equals("test")){
			 facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login/Password ungültig", null));
		    	return  null; 
		    }
		securityConntext.setAuthentication(new UsernamePasswordAuthenticationToken(new UserModelImpl("kylie"), "test", new ArrayList<>()));
	   
		
		return "shares?faces-redirect=true";
	}

}
