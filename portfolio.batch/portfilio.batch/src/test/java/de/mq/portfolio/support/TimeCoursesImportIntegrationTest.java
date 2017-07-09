package de.mq.portfolio.support;

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
import de.mq.portfolio.support.RulesConfiguration;
import org.junit.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={RulesConfiguration.class})
@ActiveProfiles("ariva")
@Ignore
public class TimeCoursesImportIntegrationTest {
	
	
	
	
	@Autowired
	@Qualifier("importTimeCourses")
	private  RulesEngine rulesEngine;
	
	
	
	@Test
	@Ignore
	public final void doImport() {
		
		Assert.assertNotNull(rulesEngine);
		final Map<String, Object> parameters = new HashMap<>();
		rulesEngine.fireRules(parameters);
		
		System.out.println("Failed: " + rulesEngine.failed());
		System.out.println("Processed: " + rulesEngine.processed());
		
		
		
		
		
	}
	
	

}
