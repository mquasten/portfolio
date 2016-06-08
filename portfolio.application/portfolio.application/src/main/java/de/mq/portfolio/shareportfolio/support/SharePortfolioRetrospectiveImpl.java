package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.util.Assert;

import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.shareportfolio.SharePortfolio;

class SharePortfolioRetrospectiveImpl implements SharePortfolioRetrospective {
	
	private final SharePortfolio committedSharePortfolio;
	private final SharePortfolio currentSharePortfolio;
	private final Data initialRateWithExchangeRate;
	private final Data endRateWithExchangeRate;
	private final Collection<TimeCourseRetrospective> timeCoursesWithExchangeRate=new ArrayList<>();;

	
	SharePortfolioRetrospectiveImpl(final SharePortfolio committedSharePortfolio, final SharePortfolio currentSharePortfolio, final Collection<TimeCourseRetrospective> timeCoursesWithExchangeRate, final Data initialRateWithExchangeRate, final Data endRateWithExchangeRate) {
		this.committedSharePortfolio = committedSharePortfolio;
		this.currentSharePortfolio = currentSharePortfolio;
		this.timeCoursesWithExchangeRate.clear();
		this.timeCoursesWithExchangeRate.addAll(timeCoursesWithExchangeRate);
		this.initialRateWithExchangeRate=initialRateWithExchangeRate;
		this.endRateWithExchangeRate=endRateWithExchangeRate;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRetrospective#committedSharePortfolio()
	 */
	@Override
	public final SharePortfolio committedSharePortfolio() {
		return committedSharePortfolio;
	}
	
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRetrospective#currentSharePortfolio()
	 */
	@Override
	public final SharePortfolio currentSharePortfolio() {
		return currentSharePortfolio;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRetrospective#initialRateWithExchangeRate()
	 */
	@Override
	public final Data initialRateWithExchangeRate() {
		return initialRateWithExchangeRate;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRetrospective#timeCoursesWithExchangeRate()
	 */
	@Override
	public final Collection<TimeCourseRetrospective> timeCoursesWithExchangeRate() {
		return Collections.unmodifiableCollection(timeCoursesWithExchangeRate);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRetrospective#endRateWithExchangeRate()
	 */
	@Override
	public Data endRateWithExchangeRate() {
		return endRateWithExchangeRate;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRetrospective#standardDeviation()
	 */
	@Override
	public Double standardDeviation() {
		portfoliosExistsGuard();
		return currentSharePortfolio.standardDeviation(committedSharePortfolio.minWeights());
	}

	private void portfoliosExistsGuard() {
		Assert.notNull(currentSharePortfolio, "CurrentSharePortfolio is mandatory.");
		Assert.notNull(currentSharePortfolio, "committedSharePortfolio is mandatory.");
	}
	
	@Override
	public final Double totalRate(final ExchangeRateCalculator exchangeRateCalculator) {
		portfoliosExistsGuard();
		return currentSharePortfolio.totalRate(committedSharePortfolio.minWeights(), exchangeRateCalculator);
	}
}
