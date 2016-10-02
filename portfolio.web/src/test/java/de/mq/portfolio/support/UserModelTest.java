package de.mq.portfolio.support;

import java.util.Locale;

import org.junit.Test;

import junit.framework.Assert;

public class UserModelTest {
	
	private static final String ID = "19680528";
	private static final String NAME = "kylie";
	private final UserModel userModel = new UserModelImpl(NAME);
	
	@Test
	public final void getName() {
		Assert.assertEquals(NAME, userModel.getName());
	}
	
	@Test
	public final void portfolioId() {
		Assert.assertNull(userModel.getPortfolioId());
		userModel.setPortfolioId(ID);
		Assert.assertEquals(ID, userModel.getPortfolioId());
	}
	
	
	@Test
	public final void locale() {
		Assert.assertEquals(Locale.GERMAN, userModel.getLocale());
		userModel.setLocale(Locale.ENGLISH);
		Assert.assertEquals(Locale.ENGLISH, userModel.getLocale());
	}

}
