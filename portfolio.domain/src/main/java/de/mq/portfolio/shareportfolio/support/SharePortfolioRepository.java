package de.mq.portfolio.shareportfolio.support;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import de.mq.portfolio.shareportfolio.PortfolioOptimisation;
import de.mq.portfolio.shareportfolio.SharePortfolio;

interface SharePortfolioRepository {

	SharePortfolio portfolio(String name);

	void save(final SharePortfolio sharePortfolio);
	
	void save(final PortfolioOptimisation sharePortfolio);

	Optional<PortfolioOptimisation> minVariance(final String name);

	Collection<SharePortfolio> portfolios(final Pageable pageable, final SharePortfolio criteria);

	Pageable pageable(final SharePortfolio criteria, final Sort sort, final Number pageSize);

	SharePortfolio sharePortfolio(final String id);

}