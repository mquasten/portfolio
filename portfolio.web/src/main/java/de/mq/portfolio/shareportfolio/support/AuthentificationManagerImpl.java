package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.mq.portfolio.user.User;
import de.mq.portfolio.user.UserService;

@Service("authentificationManager")
class AuthentificationManagerImpl  implements AuthenticationManager{

	private final UserService userService; 
	
	@Autowired
	AuthentificationManagerImpl(UserService userService) {
		this.userService = userService;
	}

	@Override
	public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
		
		final Optional<User> user = userService.user(authentication.getName());
		if(!user.isPresent()){
			throw new UsernameNotFoundException(authentication.getName());
			
		}
		
		if( ! user.get().checkPassword((String) authentication.getCredentials())) {
			throw new BadCredentialsException("");
		}
		return new UsernamePasswordAuthenticationToken(authentication.getName(), "", new ArrayList<>() );
	
	}

}
