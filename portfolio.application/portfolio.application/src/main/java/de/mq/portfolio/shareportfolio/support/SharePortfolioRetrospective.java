package de.mq.portfolio.shareportfolio.support;

import java.util.Collection;


import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;

interface SharePortfolioRetrospective {

	SharePortfolio committedSharePortfolio();

	SharePortfolio currentSharePortfolio();

	Data initialRateWithExchangeRate();
	Data endRateWithExchangeRate();

	Collection<TimeCourse> timeCoursesWithExchangeRate();

}