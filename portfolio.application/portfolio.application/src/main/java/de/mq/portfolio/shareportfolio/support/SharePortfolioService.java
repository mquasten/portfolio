package de.mq.portfolio.shareportfolio.support;

import java.util.Collection;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;

public interface SharePortfolioService {

	SharePortfolio committedPortfolio(String name);

	void save(final SharePortfolio sharePortfolio);

	Collection<SharePortfolio> portfolios(final Pageable pageable, final SharePortfolio share);


	Pageable pageable(final SharePortfolio sharePortfolio, final Sort sort, final Number size);


	SharePortfolio sharePortfolio(final String id);


	void assign(final SharePortfolio sharePortfolio, final Collection<TimeCourse> timeCourses);


	void delete(final String sharePortfolioId);


	SharePortfolioRetrospective retrospective(final String id);

	Map<ExchangeRate, Collection<Data>> realtimeExchangeRates(final String sharePortfolioId);

}