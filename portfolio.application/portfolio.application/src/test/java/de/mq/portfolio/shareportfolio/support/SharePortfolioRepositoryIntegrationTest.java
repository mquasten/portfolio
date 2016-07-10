package de.mq.portfolio.shareportfolio.support;

import java.util.stream.IntStream;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.shareportfolio.SharePortfolio;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application.xml" })
@Ignore
public class SharePortfolioRepositoryIntegrationTest {

	
	@Autowired
	private SharePortfolioService service;
	
	
	
	@Test
	public final void variance() {
		final SharePortfolio sharePortfolio =  service.committedPortfolio("mq-minRisk");
		final double[] weights = new double[sharePortfolio.timeCourses().size()];
		IntStream.range(0, sharePortfolio.timeCourses().size()).forEach(i -> weights[i]=0.1);
		System.out.println(sharePortfolio.risk(weights) +"/" + Math.sqrt(sharePortfolio.risk(weights)));
	}
	
}
