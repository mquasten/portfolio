package de.mq.portfolio.share.support;

import java.util.Arrays;
import java.util.Collection;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("exchangeRatetController")
@Scope("singleton")
public class ExchangeRateController {
	
	
	public Collection<String> exchangeRates() {
		return Arrays.asList("EUR-US$");
	}

}
