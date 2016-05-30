package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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

	@Override
	public Data endRateWithExchangeRate() {
		return endRateWithExchangeRate;
	}
}
