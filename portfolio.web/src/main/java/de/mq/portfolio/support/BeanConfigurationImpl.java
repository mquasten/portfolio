package de.mq.portfolio.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class BeanConfigurationImpl {

	@Bean
	@Scope("prototype")
	public UserModel userModel(final SecurityContext securityContext) {
	
		if( securityContext.getAuthentication() == null){
			return null;
		}
		
		return (UserModel) securityContext().getAuthentication().getPrincipal();
	
		
	}
	
	
	@Bean(name="securityContext")
	@Scope("prototype")
	public SecurityContext securityContext() {
		final SecurityContext securityContext = SecurityContextHolder.getContext();
		if (!( securityContext.getAuthentication().getPrincipal() instanceof UserModel)) {
			securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(new UserModelImpl(""), ""));
			
			
		}
		
		return securityContext;
	}
	
}



	
