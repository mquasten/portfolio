package de.mq.portfolio.shareportfolio.support;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.mq.portfolio.shareportfolio.PortfolioOptimisation;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Service("sharePortfolioService")
class SharePortfolioServiceImpl implements SharePortfolioService {
	
	private final SharePortfolioRepository sharePortfolioRepository;
	
	@Autowired
	SharePortfolioServiceImpl(SharePortfolioRepository sharePortfolioRepository) {
		this.sharePortfolioRepository = sharePortfolioRepository;
	}

	/* (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioService#committedPortfolio(java.lang.String)
	 */
	@Override
	public final SharePortfolio  committedPortfolio(final String name) {
		Assert.notNull(name);
		final SharePortfolio sharePortfolio = sharePortfolioRepository.portfolio(name);
		if( !sharePortfolio.isCommitted()) {
			sharePortfolio.commit();
			sharePortfolioRepository.save(sharePortfolio);
		}
		return sharePortfolio;
		
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioService#samples(de.mq.portfolio.shareportfolio.SharePortfolio)
	 */
	@Override
	public final Collection<double[]> samples(final SharePortfolio sharePortfolio, final Number size) {
		Assert.notNull(sharePortfolio);
		Assert.notNull(size);
		Assert.isTrue(size.intValue() > 1);
		final int n = sharePortfolio.timeCourses().size();
		return IntStream.range(0, size.intValue()).mapToObj(i -> sample(n)).collect(Collectors.toList());
	}

	private  double[] sample(final int n) {
		final double[] result = new double[n];
		final double sum[] = {0} ; 
		IntStream.range(0, n).forEach(i -> {
			final double x = Math.random();
			result[i]=x;
			sum[0]= sum[0]+ x;
			
		});
	
		IntStream.range(0, n).forEach(i -> {result[i]=result[i]/sum[0];});
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioService#risk(de.mq.portfolio.shareportfolio.SharePortfolio, double[])
	 */
	@Override
	public final PortfolioOptimisation risk(final SharePortfolio sharePortfolio, final double[] samples) {
		Assert.notNull(sharePortfolio);
		Assert.notNull(samples);
		final double risk =  sharePortfolio.risk(samples);
		return new PortfolioOptimisationImpl(sharePortfolio.name(), samples, risk);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioService#save(de.mq.portfolio.shareportfolio.PortfolioOptimisation)
	 */
	@Override
	public final void save(final PortfolioOptimisation portfolioOptimisation) {
		sharePortfolioRepository.save(portfolioOptimisation);
	}
	
	
	

}
 