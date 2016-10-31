package de.mq.portfolio.exchangerate.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.springframework.util.Assert;

import de.mq.portfolio.share.Data;





class ExchangeRateRetrospectiveImpl implements ExchangeRateRetrospective {
	
	
	private Date startDate;
	
	
	private Date endDate;
	
	private final String name;
	private  Double startValue;
	
	private Double endValue;
	
	private Double rate;
	
	private final Collection<Data> exchangeRates = new ArrayList<>();
	

	ExchangeRateRetrospectiveImpl(String name) {
		Assert.notNull(name);
		this.name = name;
	}
	
	ExchangeRateRetrospectiveImpl(final String name, final Data start, final Data end, Collection<Data> exchangeRates) {
		this(name);
		Assert.notEmpty(exchangeRates);
		Assert.notNull(start);
		Assert.notNull(start.date());
		Assert.notNull(end);
		Assert.notNull(end.date());
		this.startDate = start.date();
		this.endDate = end.date();
		this.startValue = start.value();
		this.endValue = end.value();
		this.rate=(endValue-startValue)/startValue;
		this.exchangeRates.addAll(exchangeRates);
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
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateRetrospective#exchangeRates()
	 */
	@Override
	public Collection<Data> exchangeRates() {
		return Collections.unmodifiableCollection(exchangeRates);
	}

}
