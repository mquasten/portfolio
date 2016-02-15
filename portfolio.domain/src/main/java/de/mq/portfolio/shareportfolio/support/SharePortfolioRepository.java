package de.mq.portfolio.shareportfolio.support;

import de.mq.portfolio.shareportfolio.PortfolioOptimisation;
import de.mq.portfolio.shareportfolio.SharePortfolio;

interface SharePortfolioRepository {

	SharePortfolio portfolio(String name);

	void save(SharePortfolio sharePortfolio);
	
	void save(final PortfolioOptimisation sharePortfolio);

	void minRisk(String name);

}