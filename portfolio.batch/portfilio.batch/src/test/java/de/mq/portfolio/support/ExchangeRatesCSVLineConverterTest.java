package de.mq.portfolio.support;

import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.support.ExchangeRatesCSVLineConverterImpl;
import org.junit.Assert;

public class ExchangeRatesCSVLineConverterTest {
	
	
	private static final String LINK = "http://www.bundesbank.de/cae/servlet/StatisticDownload?tsId=BBEX3.D.USD.EUR.BB.AC.000&its_csvFormat=de&its_fileFormat=csv&mode=its";


	private static final String TARGET = "USD";


	private static final String SOURCE = "EUR";


	private final static String[] COLS = {SOURCE, TARGET ,LINK };
	
	
	private final Converter<String[],ExchangeRate> converter = new ExchangeRatesCSVLineConverterImpl(); 
	@Test
	public final void convert() {
		final ExchangeRate result = converter.convert(COLS);
		Assert.assertEquals(SOURCE, result.source());
		Assert.assertEquals(TARGET, result.target());
		Assert.assertEquals(LINK, result.link());
	}

	@Test(expected=IllegalArgumentException.class)
	public final void convertWrongSize() {
		converter.convert(new String[]{});
	}
}
