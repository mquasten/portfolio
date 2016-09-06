package de.mq.portfolio.share.support;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;

import junit.framework.Assert;

@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {
	
	private static final String PASSWORD = "kinkyKylie";

	private static final String USER = "km";

	@Mock
	AuthenticationManager authenticationManager;
	
	@InjectMocks
	private final LoginControllerImpl loginController = new LoginControllerImpl();
	
	private final ArgumentCaptor<Authentication> authenticationCaptor = ArgumentCaptor.forClass(Authentication.class);
	
	private Authentication authentication = Mockito.mock(Authentication.class);
	

	
	private LoginAO loginAO = Mockito.mock(LoginAO.class);
	
	@Before
	public void setup() {
		Mockito.when(loginAO.getName()).thenReturn(USER);
		Mockito.when(loginAO.getPassword()).thenReturn(PASSWORD);
		Mockito.when(authentication.getPrincipal()).thenReturn(USER);
		Mockito.when(authenticationManager.authenticate(authenticationCaptor.capture())).thenReturn(authentication);
	}
	
	
	
	@Test
	public final void login() {
		Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
	
		Assert.assertEquals(LoginControllerImpl.SUCCESS, loginController.login(loginAO));
		
		Assert.assertEquals(USER, authenticationCaptor.getValue().getName());
		Assert.assertTrue(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
	}

	
	
	@Test
	public final void loginSucks() {
		Mockito.doThrow(new UsernameNotFoundException(USER)).when(authenticationManager).authenticate(authenticationCaptor.capture());
	
		Assert.assertEquals(String.format(LoginControllerImpl.ERROR_PATTERN, USER , StringUtils.uncapitalize(UsernameNotFoundException.class.getSimpleName().replaceFirst("Exception", ""))), loginController.login(loginAO));
	}

}
