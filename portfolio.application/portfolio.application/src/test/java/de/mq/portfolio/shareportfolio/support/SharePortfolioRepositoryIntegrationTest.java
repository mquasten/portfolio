package de.mq.portfolio.shareportfolio.support;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application.xml" })
public class SharePortfolioRepositoryIntegrationTest {

	@Autowired
	private SharePortfolioRepository sharePortfolioRepository;
	
	@Test
	public final void minRisk() {
		System.out.println(sharePortfolioRepository);
		sharePortfolioRepository.minRisk("mq-test");
		
	}
	
}
