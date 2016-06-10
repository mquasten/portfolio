package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.shareportfolio.SharePortfolio;

class SharePortfolioRetrospectiveImpl implements SharePortfolioRetrospective {
	
	private final SharePortfolio committedSharePortfolio;
	private final SharePortfolio currentSharePortfolio;
	private final Data initialRateWithExchangeRate;
	private final Data endRateWithExchangeRate;
	
	private final Double standardDeviation;
	
	private final Double totalRate;
	
	private final Double totalRateDividends;
	
	private final Collection<TimeCourseRetrospective> timeCoursesWithExchangeRate=new ArrayList<>();;

	
	SharePortfolioRetrospectiveImpl(final SharePortfolio committedSharePortfolio, final SharePortfolio currentSharePortfolio, final Collection<TimeCourseRetrospective> timeCoursesWithExchangeRate, final Data initialRateWithExchangeRate, final Data endRateWithExchangeRate, final Double standardDeviation, final Double totalRate, final Double totalRateDividends) {
		this.committedSharePortfolio = committedSharePortfolio;
		this.currentSharePortfolio = currentSharePortfolio;
		this.timeCoursesWithExchangeRate.clear();
		this.timeCoursesWithExchangeRate.addAll(timeCoursesWithExchangeRate);
		this.initialRateWithExchangeRate=initialRateWithExchangeRate;
		this.endRateWithExchangeRate=endRateWithExchangeRate;
		this.standardDeviation=standardDeviation;
		this.totalRate=totalRate;
		this.totalRateDividends=totalRateDividends;
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
		return standardDeviation;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRetrospective#totalRate(de.mq.portfolio.exchangerate.ExchangeRateCalculator)
	 */
	@Override
	public final Double totalRate(final ExchangeRateCalculator exchangeRateCalculator) {
		return totalRate;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRetrospective#totalRateDividends(de.mq.portfolio.exchangerate.ExchangeRateCalculator)
	 */
	@Override
	public final Double totalRateDividends(final ExchangeRateCalculator exchangeRateCalculator) {
		return totalRateDividends;
	}
}
