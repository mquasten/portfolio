package de.mq.portfolio.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.batch.RulesEngine;
import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={RulesConfiguration.class})
@Ignore
public class PortfoliosImportIntegrationTest {
	
	
	
	
	
	@Autowired
	@Qualifier("importPortfolios")
	private  RulesEngine rulesEngine;
	
	
	@Test
	public final void doImport() {
		
		Assert.assertNotNull(rulesEngine);
		final Map<String,Object> params = new HashMap<>();
		params.put("filename", "data/mq-minRisk.json");
		rulesEngine.fireRules(params);
		
		System.out.println("Processed: " + rulesEngine.processed());
		System.out.println("Failed: " + rulesEngine.failed());
		
	
		@SuppressWarnings("unchecked")
		final Collection<String> results =  (Collection<String>) params.get(AbstractServiceRule.ITEMS_PARAMETER);
	
		System.out.println(results.size());
		
		
	}
	
}
