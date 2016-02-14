package de.mq.portfolio.shareportfolio.support;

import java.util.Collection;

import de.mq.portfolio.shareportfolio.SharePortfolio;

interface SharePortfolioService {

	public abstract SharePortfolio committedPortfolio(String name);

	public abstract Collection<double[]> samples(SharePortfolio sharePortfolio);

}