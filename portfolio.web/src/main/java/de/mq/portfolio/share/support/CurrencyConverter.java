package de.mq.portfolio.share.support;

import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component("currencyConverter")
@Scope("singleton")
public class CurrencyConverter implements Converter<String, String> {

	private final Map<String,String> currencies = new HashMap<>();
	
	CurrencyConverter() {
	     Arrays.asList(Locale.getAvailableLocales()).stream().filter(locale -> StringUtils.hasText(locale.getCountry())).forEach(locale -> currencies.put(Currency.getInstance(locale).getCurrencyCode(), Currency.getInstance(locale).getSymbol(locale)));
	    
	}

	
	


	@Override
	public String convert(final String currencyCode) {
		return currencies.get(currencyCode);
	}
}
