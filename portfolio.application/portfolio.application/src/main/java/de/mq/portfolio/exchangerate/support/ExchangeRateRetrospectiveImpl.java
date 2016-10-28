package de.mq.portfolio.exchangerate.support;

import java.util.Date;

import org.springframework.util.Assert;





class ExchangeRateRetrospectiveImpl implements ExchangeRateRetrospective {
	
	
	private Date startDate;
	
	
	private Date endDate;
	
	private final String name;
	private Double startValue;
	
	private Double endValue;
	
	private Double rate;
	

	ExchangeRateRetrospectiveImpl(String name) {
		this.name = name;
	}
	
	ExchangeRateRetrospectiveImpl(final String name, final Date startDate, final Date endDate, final Double startValue, final Double endValue) {
		this(name);
		Assert.notNull(startDate);
		Assert.notNull(startDate);
		Assert.notNull(startValue);
		Assert.notNull(endValue);
		this.startDate = startDate;
		this.endDate = endDate;
		this.startValue = startValue;
		this.endValue = endValue;
		this.rate=(endValue-startValue)/startValue;
	}

	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateRetrospective#startDate()
	 */
	@Override
	public Date startDate() {
		return startDate;
	}

	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateRetrospective#endDate()
	 */
	@Override
	public Date endDate() {
		return endDate;
	}


	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateRetrospective#startValue()
	 */
	@Override
	public Double startValue() {
		return startValue;
	}

	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateRetrospective#endValue()
	 */
	@Override
	public Double endValue() {
		return endValue;
	}

	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateRetrospective#name()
	 */
	@Override
	public String name() {
		return name;
	}

	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateRetrospective#rate()
	 */
	@Override
	public Double rate() {
		return rate;
	}

}
