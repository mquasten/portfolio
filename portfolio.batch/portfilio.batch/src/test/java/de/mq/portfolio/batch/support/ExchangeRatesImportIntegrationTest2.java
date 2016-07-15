package de.mq.portfolio.batch.support;

import org.easyrules.api.RulesEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.batch.JobEnvironment;
import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={RulesConfiguration.class})

public class ExchangeRatesImportIntegrationTest2 {
	
	@Autowired
	private JobEnvironment jobEnvironment;
	
	
	
	@Autowired
	private  RulesEngine rulesEngine;
	
	@Test
	public final void doImport() {
		Assert.assertNotNull(jobEnvironment);
		Assert.assertNotNull(rulesEngine);
		rulesEngine.fireRules();
	}
	
}
