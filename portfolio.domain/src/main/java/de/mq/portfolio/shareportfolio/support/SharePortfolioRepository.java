package de.mq.portfolio.shareportfolio.support;

import java.util.Optional;

import de.mq.portfolio.shareportfolio.PortfolioOptimisation;
import de.mq.portfolio.shareportfolio.SharePortfolio;

interface SharePortfolioRepository {

	SharePortfolio portfolio(String name);

	void save(final SharePortfolio sharePortfolio);
	
	void save(final PortfolioOptimisation sharePortfolio);

	Optional<PortfolioOptimisation> minVariance(final String name);

}