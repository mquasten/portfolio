package de.mq.portfolio.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.batch.RulesEngine;
import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.support.AbstractServiceRule;
import de.mq.portfolio.support.RulesConfiguration;
import org.junit.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={RulesConfiguration.class})
@Ignore
@ActiveProfiles("google")
public class ExchangeRatesImportIntegrationTest2 {
	
	
	
	
	
	@Autowired
	@Qualifier("importExchangeRates2")
	private  RulesEngine rulesEngine;
	
	
	@Test
	public final void doImport() {
		
		Assert.assertNotNull(rulesEngine);
		final Map<String,Object> params = new HashMap<>();
		params.put("filename", "data/exchange.csv");
		rulesEngine.fireRules(params);
		
		System.out.println("Processed: " + rulesEngine.processed());
		System.out.println("Failed: " + rulesEngine.failed());
		
	
		@SuppressWarnings("unchecked")
		Collection<ExchangeRate> results =  (Collection<ExchangeRate>) params.get(AbstractServiceRule.ITEMS_PARAMETER);
	
		
		
		
		System.out.println(results.stream().findFirst().get().rates().size());
		
		
	}
	
}
