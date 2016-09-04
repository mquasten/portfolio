package de.mq.portfolio.share.support;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import de.mq.portfolio.support.UserModel;
import junit.framework.Assert;

public class MessageTest {
	
	private static final String UNDEFINED_KEY = "undefinedKey";

	private static final String MESSAGE_TEXT_GERMAN = "messageTextGerman";

	private static final String MESSAGE_TEXT_ENGLISH = "messageTextEnglish";

	private static final String MESSAGE_KEY = "messageKey";

	private final MessageSource  messageSource = Mockito.mock(MessageSource.class);
	
	private final MessageImpl message = new MessageImpl(messageSource);
	
	private final Authentication authentication = Mockito.mock(Authentication.class);
	
	private Locale locale = Locale.ENGLISH;
	
	
	
	private final  UserModel userModel = Mockito.mock(UserModel.class);
	
	@Before
	public final void setup() {
		Mockito.when(userModel.getLocale()).thenReturn(locale);
		Mockito.when(authentication.getPrincipal()).thenReturn(userModel);
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		Mockito.when(messageSource.getMessage(MESSAGE_KEY, null, Locale.ENGLISH)).thenReturn(MESSAGE_TEXT_ENGLISH);
		Mockito.when(messageSource.getMessage(MESSAGE_KEY, null, Locale.GERMAN)).thenReturn(MESSAGE_TEXT_GERMAN);
		Mockito.doThrow(NoSuchMessageException.class).when(messageSource).getMessage(UNDEFINED_KEY, null,  Locale.ENGLISH);
		
	}
	
	
	@Test
	public final void get() {
		Assert.assertEquals(MESSAGE_TEXT_ENGLISH, message.get(MESSAGE_KEY));
	}
	
	@Test
	public final void getWrrongPrinzipal() {
		Mockito.when(authentication.getPrincipal()).thenReturn(null);
		Assert.assertEquals(MESSAGE_TEXT_GERMAN, message.get(MESSAGE_KEY));
	}
	
	
	@Test
	public final void getWrongKey() {
		Assert.assertEquals(String.format(MessageImpl.UNDEFINED_MESSAGE_PATTERN,UNDEFINED_KEY) ,message.get(UNDEFINED_KEY));
	}
	
}