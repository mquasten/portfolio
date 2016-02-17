package de.mq.portfolio.shareportfolio.support;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import de.mq.portfolio.shareportfolio.PortfolioOptimisation;

@Document(collection="PortfolioOptimisation")
class PortfolioOptimisationImpl implements PortfolioOptimisation {
	
	@Id
	private String id;
	
	private final double variance;

	private final double[] weights;
	
	private final String portfolio;
	
	
	PortfolioOptimisationImpl() {
		this(null, new double[]{},0);
	}
	
	PortfolioOptimisationImpl(final String portfolio, final double[] weights, final double variance) {
		this.variance = variance;
		this.weights = weights;
		this.portfolio = portfolio;
	}
	
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.PortfolioOptimisation#risk()
	 */
	@Override
	public double variance() {
		return variance;
	}


	/* (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.PortfolioOptimisation#weights()
	 */
	@Override
	public double[] weights() {
		return weights;
	}


	/* (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.PortfolioOptimisation#portfolio()
	 */
	@Override
	public String portfolio() {
		return portfolio;
	}


}
