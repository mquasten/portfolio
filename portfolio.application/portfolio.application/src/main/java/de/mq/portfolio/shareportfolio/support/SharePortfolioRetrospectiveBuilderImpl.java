package de.mq.portfolio.shareportfolio.support;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.support.ExchangeRateImpl;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.share.support.DataImpl;
import de.mq.portfolio.shareportfolio.SharePortfolio;


class SharePortfolioRetrospectiveBuilderImpl  implements SharePortfolioRetrospectiveBuilder {
	
	
private final Map<ExchangeRate,Map<Date,Double>>  exchangeRates = new HashMap<>();

private  SharePortfolio committedSharePortfolio;
	
private final Map<String,TimeCourse> timeCourses = new HashMap<>(); 	
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRetrospectiveBuilder#withExchangeRate(de.mq.portfolio.exchangerate.ExchangeRate)
	 */
	@Override
	public   SharePortfolioRetrospectiveBuilder withExchangeRate(final ExchangeRate exchangeRate) {
		Assert.notNull(exchangeRate, "ExchangeRate is mandatory.");
		if( CollectionUtils.isEmpty(exchangeRate.rates())) {
			return this;
		}		
		Assert.isTrue(!exchangeRates.containsKey(exchangeRate), String.format("ExchangeRates already assigned for %s", exchangeRate));
		final Map<Date,Double> results = new HashMap<>();
		exchangeRate.rates().forEach(rate -> results.put(rate.date(), rate.value()));
		exchangeRates.put(exchangeRate, results);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRetrospectiveBuilder#withExchangeRates(java.util.Collection)
	 */
	@Override
	public   SharePortfolioRetrospectiveBuilder withExchangeRates(final Collection<ExchangeRate> exchangeRates) {
		Assert.isTrue( !CollectionUtils.isEmpty(exchangeRates), "At least one ExchangeRate should be given.");
		exchangeRates.forEach(er -> withExchangeRate(er));
		return this;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRetrospectiveBuilder#withCommitedSharePortfolio(de.mq.portfolio.shareportfolio.SharePortfolio)
	 */
	@Override
	public   SharePortfolioRetrospectiveBuilder withCommitedSharePortfolio(final SharePortfolio committedSharePortfolio) {
		Assert.notNull(committedSharePortfolio, "CommitedSharePortfolio is mandatory.");
		Assert.isTrue(committedSharePortfolio.isCommitted(), "CommitedSharePortfolio should be committed");
		Assert.isNull(this.committedSharePortfolio, "CommitedSharePortfolio already assigned");
		this.committedSharePortfolio=committedSharePortfolio;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRetrospectiveBuilder#withTimeCourse(de.mq.portfolio.share.TimeCourse)
	 */
	@Override
	public   SharePortfolioRetrospectiveBuilder withTimeCourse(final TimeCourse timeCourse) {
		Assert.notNull(timeCourse, "TimeCourse is mandatory.");
		Assert.notNull(timeCourse.share(), "TimeCourse should be assigned to a share.");
		Assert.isTrue(StringUtils.hasText(timeCourse.share().code()), "ShareCode is mandatory");
		Assert.isTrue(!timeCourses.containsKey(timeCourse.share().code()), "TimeCourse already assigned.");
		timeCourses.put(timeCourse.share().code(), timeCourse);
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRetrospectiveBuilder#withTimeCourses(java.util.Collection)
	 */
	@Override
	public   SharePortfolioRetrospectiveBuilder withTimeCourses(final Collection<TimeCourse> timeCourses) {
		Assert.isTrue( !CollectionUtils.isEmpty(timeCourses), "TimeCourses should not be empty.");
		timeCourses.forEach(tc -> withTimeCourse(tc));
		return this;
	}
	
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRetrospectiveBuilder#build()
	 */
	@Override
	public   SharePortfolioRetrospective build() {
		Assert.notNull(committedSharePortfolio, "CommittedSharePortfolio is mandatory.");
		Assert.isTrue( !CollectionUtils.isEmpty(timeCourses), "At least 2 TimeCourses expected.");
		Assert.isTrue( timeCourses.size() > 1, "At least 2 TimeCourses expected.");
	
		
		
		final Map<Date,List<Double>> rates = new HashMap<>();	
		final Map<TimeCourse, Double> min = committedSharePortfolio.min();
		
		final Data initialRateWithExchangeRate = committedSharePortfolio.timeCourses().stream().map(tc -> new AbstractMap.SimpleImmutableEntry<>(tc, factor(committedSharePortfolio.exchangeRate(tc), tc.end())* min.get(tc) *tc.rates().get(tc.rates().size() -1).value())).map(e -> new DataImpl(e.getKey().end(), e.getValue())).reduce((a,b) -> new DataImpl(a.date(), a.value() + b.value())).orElse(new DataImpl(new Date(), 0d));

		min.entrySet().forEach(e -> timeCourses.get(e.getKey().code()).rates().forEach(r -> addRate(rates, r, e.getValue(), factor(committedSharePortfolio.exchangeRate(e.getKey()), r.date()))));
		final List<Data> portfolioRatesWithExchangeRates = rates.entrySet().stream().filter(e -> e.getValue().size()== min.size()).map(e -> new DataImpl(e.getKey(), e.getValue().stream().reduce((a, b) ->  a+b).orElse(0d))).filter(isNewSample(initialRateWithExchangeRate)).sorted(sortdataByTime()).collect(Collectors.toList());
		final List<TimeCourse> timeCoursesWithExchangeRate = new ArrayList<>();
		timeCoursesWithExchangeRate.add(newTimeCourse( newShare(committedSharePortfolio.name(), committedSharePortfolio.currency()), portfolioRatesWithExchangeRates, new ArrayList<>() ));
		
		committedSharePortfolio.timeCourses().forEach(tc -> {
			final Collection<Data> shareRatesWithExchangeRate = timeCourses.get(tc.code()).rates().stream().filter(isNewSample(initialRateWithExchangeRate) ).map(data -> new DataImpl(data.date(), factor(committedSharePortfolio.exchangeRate(tc), data.date())  *  min.get(tc) * data.value())).collect(Collectors.toList());
			timeCoursesWithExchangeRate.add(newTimeCourse(  newShare(tc.share().name(), committedSharePortfolio.currency()), shareRatesWithExchangeRate, new ArrayList<>())); 
		});
		
		
		
		return new SharePortfolioRetrospectiveImpl(committedSharePortfolio, null, timeCoursesWithExchangeRate , initialRateWithExchangeRate, portfolioRatesWithExchangeRates.get(portfolioRatesWithExchangeRates.size()-1));
		
	}

	private Predicate<? super Data> isNewSample(final Data initialRateWithExchangeRate) {
		return d -> ! d.date().before(initialRateWithExchangeRate.date());
	}

	private Comparator<? super Data> sortdataByTime() {
		return (c1,c2) -> (int) Math.signum(c1.date().getTime() - c2.date().getTime());
	}

	private TimeCourse newTimeCourse(final Share share, final Collection<Data> rates, final Collection<Data> dividends) {
		
		try {
			return (TimeCourse) BeanUtils.instantiateClass(Class.forName("de.mq.portfolio.share.support.TimeCourseImpl").getDeclaredConstructor(Share.class, Collection.class, Collection.class), share,rates, dividends );
		} catch (final Exception ex) {
			 throw new IllegalStateException("Unable to create TimeCourse", ex);
		}
		
	}
	
	private Share newShare(final String name, final String currency) {
		
		try {
			return (Share) BeanUtils.instantiateClass(Class.forName("de.mq.portfolio.share.support.ShareImpl").getDeclaredConstructor(String.class, String.class, String.class, String.class, String.class), name,name, null, null, currency );
		} catch (final Exception ex) {
			 throw new IllegalStateException("Unable to create Share.", ex);
		}
		
	}
	
	
	private void addRate(final Map<Date, List<Double>> rates, final Data r, double k, final double exchangeRate) {
	
		
		if( ! rates.containsKey(r.date())) {
			rates.put(r.date(), new ArrayList<>());
		}
		rates.get(r.date()).add(exchangeRate*k*r.value());
	}
	
	private double factor(final ExchangeRate exchangeRate, final Date date) {
		 if( exchangeRate.source().equals(exchangeRate.target())){
			 return 1d;
		 }
		 if( exchangeRates.containsKey(exchangeRate)){
			 Assert.isTrue(exchangeRates.get(exchangeRate).containsKey(date), String.format("Exchangerate not found %s for date %s.", exchangeRate , date));
			 return  BigDecimal.ONE.divide(BigDecimal.valueOf(exchangeRates.get(exchangeRate).get(date)),MathContext.DECIMAL64).doubleValue();
		 }
		 
		 if( exchangeRates.containsKey(new ExchangeRateImpl(exchangeRate.target(), exchangeRate.source()))){
			 Assert.isTrue(exchangeRates.get(new ExchangeRateImpl(exchangeRate.target(), exchangeRate.source())).containsKey(date), String.format("Exchangerate (inverse) not found %s for date %s.", exchangeRate , date));
			 return exchangeRates.get(new ExchangeRateImpl(exchangeRate.target(), exchangeRate.source())).get(date).doubleValue();
		 }
		
		 throw new IllegalArgumentException(String.format("ExchangeRate not found %s." , exchangeRate));
		 
	 }


	
	

}
