package de.mq.portfolio.shareportfolio.support;

import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.junit.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application.xml" })
@Ignore
public class RetrospectiveIntegrationTest {
	
	@Autowired
	private SharePortfolioService sharePortfolioService;
	
	@Test
	@Ignore
	public final void retrospective() {
		SharePortfolioRetrospective sharePortfolioRetrospective = sharePortfolioService.retrospective("56e812181deb96d9634f8e7b");
		final Optional<TimeCourseRetrospective>  timeCoursePortfolio  = sharePortfolioRetrospective.timeCoursesWithExchangeRate().stream().filter(tc -> tc.timeCourse().share().index()==null &&  tc.timeCourse().share().wkn()==null ).findAny();
		
		Assert.assertTrue(timeCoursePortfolio.isPresent());
		timeCoursePortfolio.get().timeCourse().rates().stream().forEach(result -> {
			System.out.println(result.date() + ":"+ result.value());
			
		});
		
		
	}
	
}
 