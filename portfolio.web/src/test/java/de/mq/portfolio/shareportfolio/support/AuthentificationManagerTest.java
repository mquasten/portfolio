package de.mq.portfolio.shareportfolio.support;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;

import de.mq.portfolio.user.User;
import de.mq.portfolio.user.UserService;
import junit.framework.Assert;

public class AuthentificationManagerTest {
	
	private static final String PASSWD = "kinkyKylie";

	private static final String USER = "kminogue";

	private final UserService userService = Mockito.mock(UserService.class);
	
	private final AuthenticationManager authenticationManager = new AuthentificationManagerImpl(userService);
	
	private final Authentication authentication = Mockito.mock(Authentication.class);
	
	private final User user = Mockito.mock(User.class);
	
	@Before
	public final void setup() {
		Mockito.when(userService.user(USER)).thenReturn(Optional.of(user));
		Mockito.when(authentication.getName()).thenReturn(USER);
		Mockito.when(authentication.getCredentials()).thenReturn(PASSWD);
		Mockito.when(user.checkPassword(PASSWD)).thenReturn(Boolean.TRUE);
	}
	
	@Test
	public final void authenticate() {
		final Authentication result = authenticationManager.authenticate(authentication);
		Assert.assertEquals(USER, result.getName());
		Assert.assertTrue(result.getAuthorities().isEmpty());
		Assert.assertFalse(StringUtils.hasText((String) result.getCredentials()));
	}
	
	@Test(expected=UsernameNotFoundException.class)
	public final void authenticateUserNotFound() {
		Mockito.when(userService.user(USER)).thenReturn(Optional.empty());
		 authenticationManager.authenticate(authentication);
	}
	
	@Test(expected=BadCredentialsException.class)
	public final void authenticateWrongPassword() {
		Mockito.when(user.checkPassword(PASSWD)).thenReturn(Boolean.FALSE);
		 authenticationManager.authenticate(authentication);
	}
	

}
