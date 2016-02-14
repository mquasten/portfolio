package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
	public final Collection<double[]> samples(final SharePortfolio sharePortfolio) {
		Assert.notNull(sharePortfolio);
		 final Collection<double[]> results = new ArrayList<>();
		 results.add(new double[]{0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1});
		 return results;
	}
	
	public final double risk(final SharePortfolio sharePortfolio, final double[] samples) {
		Assert.notNull(sharePortfolio);
		Assert.notNull(samples);
		return sharePortfolio.risk(samples);
	}

}
 