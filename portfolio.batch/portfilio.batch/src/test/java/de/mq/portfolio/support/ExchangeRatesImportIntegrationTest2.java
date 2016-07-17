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
import de.mq.portfolio.support.RulesConfiguration;
import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={RulesConfiguration.class})
@Ignore
public class ExchangeRatesImportIntegrationTest2 {
	
	@Autowired
	private JobEnvironment jobEnvironment;
	
	
	
	@Autowired
	@Qualifier("importExchangeRates")
	private  RulesEngine rulesEngine;
	
	@Test
	public final void doImport() {
		Assert.assertNotNull(jobEnvironment);
		Assert.assertNotNull(rulesEngine);
		jobEnvironment.assign("filename", "data/exchange.csv");
		rulesEngine.fireRules();
		
		System.out.println("Failed: " + jobEnvironment.failed());
		System.out.println("Processed: " + jobEnvironment.processed());
		Collection<ExchangeRate> results =  jobEnvironment.parameters(AbstractServiceRule.ITEMS_PARAMETER);
		System.out.println(results.stream().findFirst().get().rates().size());
		
	}
	
}
