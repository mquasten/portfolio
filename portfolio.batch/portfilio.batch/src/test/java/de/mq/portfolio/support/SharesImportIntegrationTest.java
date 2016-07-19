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
import de.mq.portfolio.share.Share;
import de.mq.portfolio.support.AbstractServiceRule;
import de.mq.portfolio.support.RulesConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={RulesConfiguration.class})
@Ignore
public class SharesImportIntegrationTest {

	@Autowired
	@Qualifier("importShares")
	private RulesEngine rulesEngine;
	
	
	
	
	
	@Test
	public final void doImport() {
		
		final Map<String,Object> params = new HashMap<>();
		params.put("filename", "data/stocks.csv");
		System.out.println("get the party started");
		System.out.println(rulesEngine);
		
		rulesEngine.fireRules(params);
		

		System.out.println("Failed: " + rulesEngine.failed());
		System.out.println("Processed: " + rulesEngine.processed());
		@SuppressWarnings("unchecked")
		final Collection<Share> results =  (Collection<Share>) params.get(AbstractServiceRule.ITEMS_PARAMETER);
		System.out.println(results.size());
	

		
	}
}
