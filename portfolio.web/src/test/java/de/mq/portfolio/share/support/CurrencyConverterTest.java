package de.mq.portfolio.share.support;

import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

import org.junit.Assert;

public class CurrencyConverterTest {
	
	private static final String GBP_SYMBOL = "£";
	private static final String EUR_SYMBOL = "€";
	private static final String US$_SYMBOL = "US$";
	private final Converter<String,String> converter = new CurrencyConverter(); 
	
	
	@Test
	public final void convert() {
		Assert.assertEquals(US$_SYMBOL, converter.convert("USD"));
		Assert.assertEquals(EUR_SYMBOL, converter.convert("EUR"));
		Assert.assertEquals(GBP_SYMBOL, converter.convert("GBP"));
		
		
	}

}
