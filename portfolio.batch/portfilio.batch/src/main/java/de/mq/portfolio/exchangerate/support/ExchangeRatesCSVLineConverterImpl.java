package de.mq.portfolio.exchangerate.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import de.mq.portfolio.exchangerate.ExchangeRate;


public class ExchangeRatesCSVLineConverterImpl implements Converter<String[], ExchangeRate>{

	@Override
	public ExchangeRate  convert(final String[] source) {
		
		Assert.notNull(source);
		Assert.isTrue(source.length==3);
		return new ExchangeRateImpl(source[0], source[1], source[2]);
	}

}
