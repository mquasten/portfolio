package de.mq.portfolio.share.support;

import org.junit.Test;

import junit.framework.Assert;

public class LoginAOTest {
	
	private static final String PASSWORD = "passwd";
	private static final String NAME = "kminogue";
	private static final String MESSAGE = "Invalid user name";
	private final LoginAO loginAO = new LoginAO();
	
	@Test
	public final void message() {
		Assert.assertNull(loginAO.getMessage());
		loginAO.setMessage(MESSAGE);
		Assert.assertEquals(MESSAGE, loginAO.getMessage());
	}
	
	@Test
	public final void name() {
		Assert.assertNull(loginAO.getName());
		loginAO.setName(NAME);
		Assert.assertEquals(NAME, loginAO.getName());
	}
	
	
	@Test
	public final void password() {
		Assert.assertNull(loginAO.getPassword());
		loginAO.setPassword(PASSWORD);
		Assert.assertEquals(PASSWORD, loginAO.getPassword());
	}

}
