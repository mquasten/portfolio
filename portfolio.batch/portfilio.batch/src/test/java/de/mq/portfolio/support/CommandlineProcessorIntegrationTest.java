package de.mq.portfolio.support;


import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={RulesConfiguration.class})
public class CommandlineProcessorIntegrationTest {

	
	@Test
	public final void main()  {
		
		SimpleCommandlineProcessorImpl.main(new String[] {"Kylie", "is" , "nice"});
		
	}
	
}
