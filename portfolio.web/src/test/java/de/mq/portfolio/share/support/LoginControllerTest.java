package de.mq.portfolio.share.support;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;

import de.mq.portfolio.support.UserModel;
import org.junit.Assert;

@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {
	
	private static final String LANGUAGE = "de";

	private static final String VIEW_LODIN= "login.xhtml";

	private static final String PASSWORD = "kinkyKylie";

	private static final String USER = "km";

	@Mock
	AuthenticationManager authenticationManager;
	
	@InjectMocks
	private final AbstractLoginController loginController =  Mockito.mock(AbstractLoginController.class, Mockito.CALLS_REAL_METHODS);
	
	private final ArgumentCaptor<Authentication> authenticationCaptorManager = ArgumentCaptor.forClass(Authentication.class);
	private final ArgumentCaptor<Authentication> authenticationCaptorContext = ArgumentCaptor.forClass(Authentication.class);
	
	private Authentication authentication = Mockito.mock(Authentication.class);
	

	private final FacesContext facesContext = Mockito.mock(FacesContext.class);
	
	private LoginAO loginAO = Mockito.mock(LoginAO.class);
	
	ExternalContext externalContext = Mockito.mock(ExternalContext.class);
	
	final HttpSession session = Mockito.mock(HttpSession.class);
	
	final UIViewRoot viewRoot = Mockito.mock(UIViewRoot.class);
	
	@Mock
	private SecurityContext securityContext;
	
	
	@Before
	public void setup() {
		Mockito.when(loginAO.getName()).thenReturn(USER);
		Mockito.when(loginAO.getPassword()).thenReturn(PASSWORD);
		Mockito.when(authentication.getPrincipal()).thenReturn(USER);
		Mockito.when(authenticationManager.authenticate(authenticationCaptorManager.capture())).thenReturn(authentication);
		
		Mockito.when(loginController.securityContext()).thenReturn(securityContext);
		Mockito.when(loginController.facesContext()).thenReturn(facesContext);
		
		Mockito.when(facesContext.getExternalContext()).thenReturn(externalContext);
		Mockito.when(externalContext.getSession(Mockito.anyBoolean())).thenReturn(session);
		
		Mockito.when(facesContext.getViewRoot()).thenReturn(viewRoot);
		Mockito.when(viewRoot.getViewId()).thenReturn(VIEW_LODIN);
	}
	
	
	
	@Test
	public final void login() {
	
		Assert.assertEquals(AbstractLoginController.SUCCESS, loginController.login(loginAO));
		
		Assert.assertEquals(USER, authenticationCaptorManager.getValue().getName());
		
		Mockito.verify(securityContext).setAuthentication(authenticationCaptorContext.capture());
		
		final UserModel userModel = (UserModel) authenticationCaptorContext.getValue().getPrincipal();
		Assert.assertEquals(USER, userModel.getName());
		Assert.assertTrue(authenticationCaptorContext.getValue().getAuthorities().isEmpty());
		
	}

	
	
	@Test
	public final void loginSucks() {
		Mockito.doThrow(new UsernameNotFoundException(USER)).when(authenticationManager).authenticate(authenticationCaptorManager.capture());
	
		Assert.assertEquals(String.format(AbstractLoginController.ERROR_PATTERN, USER , StringUtils.uncapitalize(UsernameNotFoundException.class.getSimpleName().replaceFirst("Exception", ""))), loginController.login(loginAO));
	}

	
	@Test
	public final void create() {
		Assert.assertTrue(Mockito.spy(AbstractLoginController.class) instanceof AbstractLoginController);
	}	
	
	@Test
	public final void logout() {
		
		//Mockito.when(facesContext.getExternalContext()
		Assert.assertEquals(VIEW_LODIN + AbstractLoginController.FACES_REDIRECT, loginController.logout());
		
		Mockito.verify(securityContext).setAuthentication(null);
		Mockito.verify(session).invalidate();
	}
	
	@Test
	public final void language() {
		Assert.assertEquals(VIEW_LODIN+AbstractLoginController.FACES_REDIRECT+AbstractLoginController.LANGUAGE_PARAM + LANGUAGE, loginController.language(LANGUAGE));
	}
	
}
