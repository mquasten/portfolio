package de.mq.portfolio.shareportfolio.support;

import java.util.Optional;

import java.util.stream.IntStream;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import de.mq.portfolio.shareportfolio.PortfolioOptimisation;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application.xml" })
public class SharePortfolioRepositoryIntegrationTest {

	@Autowired
	private SharePortfolioRepository sharePortfolioRepository;
	
	@Autowired
	private SharePortfolioService service;
	
	@Test
	public final void minRisk() {
		System.out.println(sharePortfolioRepository);
		Optional<PortfolioOptimisation> minVariance = sharePortfolioRepository.minRisk("mq-test");
		System.out.println("weights:");
		CollectionUtils.arrayToList(minVariance.get().weights()).forEach( s -> System.out.println(s));
		System.out.println("portfolio:");
		System.out.println(minVariance.get().portfolio());
		System.out.println("variance:");
		System.out.println(minVariance.get().variance() +"/" +Math.sqrt(minVariance.get().variance()));
		
	}
	
	@Test
	public final void variance() {
		SharePortfolio sharePortfolio =  service.committedPortfolio("mq-test");
		final double[] weights = new double[sharePortfolio.timeCourses().size()];
		IntStream.range(0, sharePortfolio.timeCourses().size()).forEach(i -> weights[i]=0.1);
		System.out.println(sharePortfolio.risk(weights) +"/" + Math.sqrt(sharePortfolio.risk(weights)));
	}
	
}
