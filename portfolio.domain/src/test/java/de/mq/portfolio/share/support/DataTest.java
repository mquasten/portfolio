package de.mq.portfolio.share.support;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;
import org.springframework.beans.BeanUtils;

import junit.framework.Assert;
import de.mq.portfolio.share.Data;

public class DataTest {
	
	private static final Double VALUE = 47.11d;
	private static final String DATE = "1968-05-28";
	private final Data data = new DataImpl(DATE, VALUE);
	
	@Test
	public final void date() throws ParseException {
		Assert.assertEquals(new SimpleDateFormat(DataImpl.DATE_PATTERN).parse(DATE), data.date());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void dateWrongFormat() {
		new DataImpl("x", VALUE).date();
	}
	
	@Test
	public final void value(){
		Assert.assertEquals(VALUE, data.value());
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public final void valueNull() {
		BeanUtils.instantiateClass(DataImpl.class).value();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void dateNull() {
		BeanUtils.instantiateClass(DataImpl.class).date();
	}
	
	@Test
	public final void createWithDate() throws ParseException{
		final SimpleDateFormat df = new SimpleDateFormat(DataImpl.DATE_PATTERN);
		final Data data = new DataImpl(df.parse(DATE), VALUE);
		Assert.assertEquals(DATE, df.format(data.date()));
	}

}
