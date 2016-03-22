package de.mq.portfolio.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
class BeanConfigurationImpl {

	@Bean
	@Scope("prototype")
	UserModel userModel() {
	
		if( securityContext().getAuthentication() == null){
			return null;
		}
		return (UserModel) securityContext().getAuthentication().getPrincipal();
	
		
	}
	
	

	private SecurityContext securityContext() {
		final SecurityContext securityConntext =  SecurityContextHolder.getContext();
		
		if( securityConntext.getAuthentication() == null){
			securityConntext.setAuthentication(new UsernamePasswordAuthenticationToken(new UserModelImpl(""), ""));
		}
		return securityConntext;
	}
	
}



	
