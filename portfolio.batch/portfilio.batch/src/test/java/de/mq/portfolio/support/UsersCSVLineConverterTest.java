package de.mq.portfolio.support;

import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

import de.mq.portfolio.user.User;
import de.mq.portfolio.user.support.UsersCSVLineConverterImpl;
import junit.framework.Assert;

public class UsersCSVLineConverterTest {
	
	private static final String PASSWORD = "kinkyKylie";
	private static final String USER = "kminogue";
	private final Converter<String[], User> converter = new UsersCSVLineConverterImpl(); 
	
	@Test
	public final void convert() {
		final User user = converter.convert(new String[] {USER , PASSWORD});
		Assert.assertEquals(USER, user.login());
		Assert.assertEquals(PASSWORD, user.password());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void convertMissingColumns() {
		 converter.convert(new String[] {});
	}

}
