package de.mq.portfolio.exchangerate.support;

import org.easyrules.api.RulesEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.share.support.JobEnvironment;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/easy-rules.xml" })
public class ExchangeRatesImportIntegrationTest {

	@Autowired
	private RulesEngine rulesEngine;
	
	@Autowired
	private JobEnvironment jobEnvironment;
	
	
	
	@Test
	public final void doImport() {
		
		jobEnvironment.assign("filename", "data/exchange.csv");
		System.out.println("get the party started");
		System.out.println(rulesEngine);
		
		
		rulesEngine.fireRules();
		
		System.out.println(jobEnvironment.exceptions());
		System.out.println(jobEnvironment.processed());
	}
}
