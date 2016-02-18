package de.mq.portfolio.shareportfolio.support;

import java.util.Collection;

import de.mq.portfolio.shareportfolio.PortfolioOptimisation;
import de.mq.portfolio.shareportfolio.SharePortfolio;

interface SharePortfolioService {

	SharePortfolio committedPortfolio(String name);


	Collection<double[]> samples(SharePortfolio sharePortfolio, Number size);


	PortfolioOptimisation variance(final SharePortfolio sharePortfolio, final double[] samples);


	


	void create(final PortfolioOptimisation portfolioOptimisation);


	SharePortfolio assign(final PortfolioOptimisation portfolioOptimisation);


	void save(final SharePortfolio sharePortfolio);


	PortfolioOptimisation minVariance(final String portfolioName);

}