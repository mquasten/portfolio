package de.mq.portfolio.shareportfolio.support;

import java.util.Collection;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;

interface SharePortfolioRetrospectiveBuilder {

	SharePortfolioRetrospectiveBuilder withExchangeRate(final ExchangeRate exchangeRate);

	SharePortfolioRetrospectiveBuilder withCommitedSharePortfolio(final SharePortfolio committedSharePortfolio);

	SharePortfolioRetrospectiveBuilder withTimeCourse(final TimeCourse timeCourse);

	SharePortfolioRetrospective build();

	SharePortfolioRetrospectiveBuilder withExchangeRates(final Collection<ExchangeRate> exchangeRates);

	SharePortfolioRetrospectiveBuilder withTimeCourses(final Collection<TimeCourse> timeCourses);

}