package de.mq.portfolio.shareportfolio.support;

import java.util.Collection;

import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;

interface SharePortfolioRetrospectiveBuilder {

	SharePortfolioRetrospectiveBuilder withExchangeRateCalculator(final ExchangeRateCalculator exchangeRateCalculator);

	SharePortfolioRetrospectiveBuilder withCommitedSharePortfolio(final SharePortfolio committedSharePortfolio);

	SharePortfolioRetrospectiveBuilder withTimeCourse(final TimeCourse timeCourse);

	SharePortfolioRetrospective build();

	SharePortfolioRetrospectiveBuilder withTimeCourses(final Collection<TimeCourse> timeCourses);

}