package de.mq.portfolio.support;

import java.util.Collection;

import org.easyrules.api.RulesEngine;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.batch.JobEnvironment;
import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.support.AbstractServiceRule;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/easy-rules.xml" })
@Ignore
public class ExchangeRatesImportIntegrationTest {

	@Autowired
	@Qualifier("importExchangeRates")
	private RulesEngine rulesEngine;
	
	@Autowired
	private JobEnvironment jobEnvironment;
	
	
	
	@Test
	public final void doImport() {
		
		
		jobEnvironment.assign("filename", "data/exchange.csv");
		System.out.println("get the party started");
		System.out.println(rulesEngine);
		
		System.out.println(rulesEngine.getRules().size());
		
		rulesEngine.fireRules();
		

		System.out.println("Failed: " + jobEnvironment.failed());
		System.out.println("Processed: " + jobEnvironment.processed());
		final Collection<ExchangeRate> results =  jobEnvironment.parameters(AbstractServiceRule.ITEMS_PARAMETER);
		System.out.println(results.stream().findFirst().get().rates().size());
	

		
	}
}
