package de.mq.portfolio.batch.support;
<<<<<<< HEAD:portfolio.batch/portfilio.batch/src/test/java/de/mq/portfolio/batch/support/ExchangeRatesImportIntegrationTest.java

import java.util.Collection;
=======
>>>>>>> 5968e539d5a8fd9d4158676393fd00821240e789:portfolio.batch/portfilio.batch/src/test/java/de/mq/portfolio/batch/support/ExchangeRatesImportIntegrationTest.java

import org.easyrules.api.RulesEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.batch.JobEnvironment;
<<<<<<< HEAD:portfolio.batch/portfilio.batch/src/test/java/de/mq/portfolio/batch/support/ExchangeRatesImportIntegrationTest.java
import de.mq.portfolio.exchangerate.ExchangeRate;
=======
>>>>>>> 5968e539d5a8fd9d4158676393fd00821240e789:portfolio.batch/portfilio.batch/src/test/java/de/mq/portfolio/batch/support/ExchangeRatesImportIntegrationTest.java

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
		
		System.out.println(rulesEngine.getRules().size());
		
		rulesEngine.fireRules();
		
<<<<<<< HEAD:portfolio.batch/portfilio.batch/src/test/java/de/mq/portfolio/batch/support/ExchangeRatesImportIntegrationTest.java
		System.out.println("Failed: " + jobEnvironment.failed());
		System.out.println("Processed: " + jobEnvironment.processed());
		Collection<ExchangeRate> results =  jobEnvironment.parameters(AbstractServiceRule.ITEMS_PARAMETER);
		System.out.println(results.stream().findFirst().get().rates().size());
	
=======
		System.out.println(jobEnvironment.exceptions());
		System.out.println(jobEnvironment.processed());
		System.out.println((Object) jobEnvironment.parameter(AbstractServiceRule.ITEMS_PARAMETER));
>>>>>>> 5968e539d5a8fd9d4158676393fd00821240e789:portfolio.batch/portfilio.batch/src/test/java/de/mq/portfolio/batch/support/ExchangeRatesImportIntegrationTest.java
	}
}
