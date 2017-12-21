package de.mq.portfolio.exchangerate.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.mq.portfolio.share.Data;



@Component()
@Scope(scopeName = "prototype")
class ExchangeRateRetrospectiveBuilderImpl implements ExchangeRateRetrospectiveBuilder {
	
	private String name;
	private String target;
	
	private Date startDate;
	
	private final Collection<Data> exchangeRates = new ArrayList<>();
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.xxx#withName(java.lang.String)
	 */
	@Override
	public ExchangeRateRetrospectiveBuilder withName(final String name) {
		Assert.hasText(name, "Name is mandatory.");
		Assert.isNull(this.name, "Name already assigned." );
		this.name=name;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.xxx#withStartDate(java.util.Date)
	 */
	@Override
	public ExchangeRateRetrospectiveBuilder withStartDate(final Date startDate) {
		Assert.notNull(startDate, "StartDate is mandatory.");
		Assert.isNull(this.startDate, "Date already assigned.");
		this.startDate=startDate;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.xxx#withExchangeRates(java.util.Collection)
	 */
	@Override
	public ExchangeRateRetrospectiveBuilder withExchangeRates(final Collection<Data> exchangeRates) {
		Assert.isTrue(this.exchangeRates.isEmpty(), "exchangeRates already assigned." );
		this.exchangeRates.addAll(exchangeRates);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateRetrospectiveBuilder#withTarget(java.lang.String)
	 */
	@Override
	public ExchangeRateRetrospectiveBuilder withTarget(final String target) {
		Assert.hasText(target, "Target is mandatory.");
		Assert.isNull(this.target, "Target already assigned." );
		this.target=target;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.xxx#build()
	 */
	@Override
	public ExchangeRateRetrospective build() {
		final List<Data> ratesSince = exchangeRates.stream().filter(data -> ! data.date().before(startDate)).collect(Collectors.toList());
		if(ratesSince.size()<1){
			return new ExchangeRateRetrospectiveImpl(name, target);
		}
		
		return new ExchangeRateRetrospectiveImpl(name, target,  ratesSince.get(0),ratesSince.get(ratesSince.size()-1), ratesSince);
		
	}

	

}
