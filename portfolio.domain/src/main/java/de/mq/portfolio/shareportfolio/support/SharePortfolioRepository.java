package de.mq.portfolio.shareportfolio.support;

import java.util.Collection;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import de.mq.portfolio.shareportfolio.SharePortfolio;

interface SharePortfolioRepository {

	SharePortfolio portfolio(final String name);

	void save(final SharePortfolio sharePortfolio);
	
	

	Collection<SharePortfolio> portfolios(final Pageable pageable, final SharePortfolio criteria);

	Pageable pageable(final SharePortfolio criteria, final Sort sort, final Number pageSize);

	SharePortfolio sharePortfolio(final String id);

	void delete(final SharePortfolio sharePortfolio);

	void save(final String json);

}