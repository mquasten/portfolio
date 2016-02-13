package de.mq.portfolio.shareportfolio.support;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/calculatePortfolio.xml" })
public class CalculatePortfolio {

	@Test
	public final void runJob() {
		
	}
	
}
