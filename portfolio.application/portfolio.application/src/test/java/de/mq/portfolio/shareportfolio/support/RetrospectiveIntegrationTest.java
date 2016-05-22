package de.mq.portfolio.shareportfolio.support;

import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.share.Data;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application.xml" })
@Ignore
public class RetrospectiveIntegrationTest {
	
	@Autowired
	private SharePortfolioService sharePortfolioService;
	
	@Test
	public final void retrospective() {
		final Collection<Data> results = sharePortfolioService.retrospective("56e812181deb96d9634f8e7b");
		
		results.stream().forEach(result -> {
			System.out.println(result.date() + ":"+ result.value());
			
		});
		
		results.size();
	}
}
 