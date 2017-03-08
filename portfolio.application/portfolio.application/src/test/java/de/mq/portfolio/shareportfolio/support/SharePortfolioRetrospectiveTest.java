package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import org.junit.Assert;

public class SharePortfolioRetrospectiveTest {
	
	private final SharePortfolio committedSharePortfolio = Mockito.mock(SharePortfolio.class);

	private final SharePortfolio currentSharePortfolio = Mockito.mock(SharePortfolio.class);
	
	private final Collection<TimeCourseRetrospective> timeCoursesWithExchangeRate = new ArrayList<>();
	private final Data initialRateWithExchangeRate = Mockito.mock(Data.class);
	private final Data endRateWithExchangeRate = Mockito.mock(Data.class);
	private final TimeCourseRetrospective timeCourseRetrospective = Mockito.mock(TimeCourseRetrospective.class);
	
	final Double standardDeviation = 42e-6;

	final Double totalRate = 10d;
	final Double totalRateDividends = 2d;
	
	private  SharePortfolioRetrospective sharePortfolioRetrospective;

	@Before
	public final void setup() {
		timeCoursesWithExchangeRate.add(timeCourseRetrospective);
		sharePortfolioRetrospective = new SharePortfolioRetrospectiveImpl(committedSharePortfolio , currentSharePortfolio, timeCoursesWithExchangeRate, initialRateWithExchangeRate, endRateWithExchangeRate, standardDeviation, totalRate, totalRateDividends);
	}
	
	@Test
	public final void committedSharePortfolio() {
		Assert.assertEquals(committedSharePortfolio, sharePortfolioRetrospective.committedSharePortfolio());
	}
	
	@Test
	public final void currentSharePortfolio() {
		Assert.assertEquals(currentSharePortfolio, sharePortfolioRetrospective.currentSharePortfolio());
	}
	
	@Test
	public final void  initialRateWithExchangeRate() {
		Assert.assertEquals(initialRateWithExchangeRate, sharePortfolioRetrospective.initialRateWithExchangeRate());
	}
	
	@Test
	public final void endRateWithExchangeRate() {
		Assert.assertEquals(endRateWithExchangeRate, sharePortfolioRetrospective.endRateWithExchangeRate());
	}
	
	@Test
	public final void timeCoursesWithExchangeRate() {
		Assert.assertEquals(timeCoursesWithExchangeRate.size(), sharePortfolioRetrospective.timeCoursesWithExchangeRate().size());
		Assert.assertTrue(sharePortfolioRetrospective.timeCoursesWithExchangeRate().stream().findAny().isPresent());
		Assert.assertEquals(timeCourseRetrospective, sharePortfolioRetrospective.timeCoursesWithExchangeRate().stream().findAny().get());
	}
	
	@Test
	public final void standardDeviation() {
		Assert.assertEquals(standardDeviation, sharePortfolioRetrospective.standardDeviation());
	}
	
	@Test
	public final void totalRate() {
		Assert.assertEquals(totalRate, sharePortfolioRetrospective.totalRate());
	}
	
	@Test
	public final void totalRateDividends() {
		Assert.assertEquals(totalRateDividends, sharePortfolioRetrospective.totalRateDividends());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void timeCorsesMissing() {
	
		new SharePortfolioRetrospectiveImpl(committedSharePortfolio , currentSharePortfolio, new ArrayList<>(), initialRateWithExchangeRate, endRateWithExchangeRate, standardDeviation, totalRate, totalRateDividends);
	}
}
