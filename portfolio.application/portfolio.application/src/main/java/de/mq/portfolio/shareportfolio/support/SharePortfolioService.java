package de.mq.portfolio.shareportfolio.support;

import java.util.Collection;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.PortfolioOptimisation;
import de.mq.portfolio.shareportfolio.SharePortfolio;

public interface SharePortfolioService {

	SharePortfolio committedPortfolio(String name);


	Collection<double[]> samples(SharePortfolio sharePortfolio, Number size);


	PortfolioOptimisation variance(final SharePortfolio sharePortfolio, final double[] samples);


	


	void create(final PortfolioOptimisation portfolioOptimisation);


	SharePortfolio assign(final PortfolioOptimisation portfolioOptimisation);


	void save(final SharePortfolio sharePortfolio);


	PortfolioOptimisation minVariance(final String portfolioName);


	Collection<SharePortfolio> portfolios(final Pageable pageable, final SharePortfolio share);


	Pageable pageable(final SharePortfolio sharePortfolio, final Sort sort, final Number size);


	SharePortfolio sharePortfolio(final String id);


	void assign(final SharePortfolio sharePortfolio, final Collection<TimeCourse> timeCourses);


	void delete(final String sharePortfolioId);

}