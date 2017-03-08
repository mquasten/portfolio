package de.mq.portfolio.support;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import org.junit.Assert;

public class BeanConfigurationTest {
	
	private final  SecurityContext  securityContext = Mockito.mock(SecurityContext.class);
	
	private final BeanConfigurationImpl beanConfiguration = new BeanConfigurationImpl();
	
	
	private final Authentication authentication = Mockito.mock(Authentication.class);
	
	private final UserModel userModel = Mockito.mock(UserModel.class);
	
	@Before
	public final void setup() {
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		Mockito.when(authentication.getPrincipal()).thenReturn(userModel);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
	@Test
	public final void userModelNoPrincipal() {
		Mockito.when(securityContext.getAuthentication()).thenReturn(null);
		Assert.assertNull(beanConfiguration.userModel(securityContext));
	}
	
	
	@Test
	public final void userModel() {
		Assert.assertEquals(userModel, beanConfiguration.userModel(securityContext));
	}
	
	@Test
	public final void securityContext() {
	
		
		Assert.assertEquals(authentication, beanConfiguration.securityContext().getAuthentication());
	}
	
	@Test
	public final void securityContextNull() {
		
		Mockito.when(authentication.getPrincipal()).thenReturn(null);
		UserModel userModel =  (UserModel) beanConfiguration.securityContext().getAuthentication().getPrincipal();
		Assert.assertFalse(StringUtils.hasText(userModel.getName()));
		Assert.assertFalse(StringUtils.hasText((String) beanConfiguration.securityContext().getAuthentication().getCredentials()));
		
		
	}

}
