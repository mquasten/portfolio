package de.mq.portfolio.shareportfolio.support;

import java.util.Collection;

import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.shareportfolio.SharePortfolio;

interface SharePortfolioRetrospective {

	SharePortfolio committedSharePortfolio();

	SharePortfolio currentSharePortfolio();

	Data initialRateWithExchangeRate();
	Data endRateWithExchangeRate();

	Collection<TimeCourseRetrospective> timeCoursesWithExchangeRate();

	Double standardDeviation();

	Double totalRate(final ExchangeRateCalculator exchangeRateCalculator);

}