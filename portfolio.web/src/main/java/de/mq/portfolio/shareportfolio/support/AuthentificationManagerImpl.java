package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service("authentificationManager")
public class AuthentificationManagerImpl  implements AuthenticationManager{

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if( !authentication.getCredentials().equals("test")) {
			throw new BadCredentialsException("");
		}
		return new UsernamePasswordAuthenticationToken("test", "", new ArrayList<>() );
	
	}

}
