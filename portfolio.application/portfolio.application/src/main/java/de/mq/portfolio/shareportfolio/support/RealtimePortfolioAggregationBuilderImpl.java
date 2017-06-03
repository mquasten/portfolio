package de.mq.portfolio.shareportfolio.support;

import java.lang.reflect.Constructor;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import de.mq.portfolio.support.ExceptionTranslationBuilderImpl;

@Component
@Scope(scopeName = "prototype")
abstract class RealtimePortfolioAggregationBuilderImpl implements RealtimePortfolioAggregationBuilder {

	private SharePortfolio sharePortfolio;

	private final Collection<Entry<TimeCourse, List<Data>>> realtimeCourses = new ArrayList<>();

	private final Map<String, Data[]> exchangeRates = new HashMap<>();

	@Override
	public RealtimePortfolioAggregationBuilder withSharePortfolio(SharePortfolio sharePortfolio) {
		Assert.notNull(sharePortfolio, "Shareportfolio is mandatory.");
		Assert.isNull(this.sharePortfolio, "Shareportfolio already assigned.");
		this.sharePortfolio = sharePortfolio;
		return this;
	}

	@Override
	public RealtimePortfolioAggregationBuilder withRealtimeCourses(Collection<Entry<TimeCourse, List<Data>>> realtimeCourses) {
		Assert.notEmpty(realtimeCourses, "At least one TimeCourse must be given");
		Assert.isTrue(this.realtimeCourses.isEmpty(), "TimeCourseEntries already assigned.");
		this.realtimeCourses.clear();
		this.realtimeCourses.addAll(realtimeCourses);
		return this;
	}

	@Override
	public RealtimePortfolioAggregationBuilder withRealtimeExchangeRates(final Collection<ExchangeRate> realtimeExhangeRates) {
		Assert.notEmpty(realtimeExhangeRates, "At least one ExhangeRate must be given");
		Assert.isTrue(this.exchangeRates.isEmpty(), "ExhangeRates already assigned.");

		realtimeExhangeRates.stream().map(exchangeRate -> exchangeRate.rates().size()).forEach(size -> Assert.isTrue(size == 2, "2 TimeCourses expected, (last and realtime)."));
		exchangeRates.clear();
		exchangeRates.putAll(realtimeExhangeRates.stream().map(exchangeRate -> new AbstractMap.SimpleImmutableEntry<>(exchangeRate.source(), new Data[] { exchangeRate.rates().get(0), exchangeRate.rates().get(1) })).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));

		return this;
	}

	@Override
	public RealtimePortfolioAggregation build() {

		Assert.notNull(sharePortfolio.name(), "PortfolioName is mandatory.");
		Assert.notNull(sharePortfolio.currency(), "PortfolioCurrency is mandatory.");

		realtimeCourses.stream().map(entry -> entry.getKey()).forEach(tc -> assertTimeCourse(tc));
		realtimeCourses.stream().map(entry -> entry.getValue().size()).forEach(size -> Assert.isTrue(size == 2, "2 TimeCourses expected, last and realtime."));
		return BeanUtils.instantiateClass(contructor(), sharePortfolio, realtimeCourses, exchangeRates);

	}

	private Object assertTimeCourse(TimeCourse timeCourse) {
		Assert.notNull(timeCourse.share(), "Share is Mandatory.");
		Assert.hasText(timeCourse.share().currency(), "Currency is Mandatory.");
		Assert.hasText(timeCourse.code(), "Code is Mandatory.");
		return null;
	}

	private Constructor<? extends RealtimePortfolioAggregation> contructor() {
		return exceptionTranslator().withStatement(() -> {
			return target().getDeclaredConstructor(SharePortfolio.class, Collection.class, Map.class);
		}).withTranslation(IllegalStateException.class, Arrays.asList(NoSuchMethodException.class, SecurityException.class)).translate();

	}

	@Lookup
	abstract ExceptionTranslationBuilderImpl<Constructor<? extends RealtimePortfolioAggregation>, AutoCloseable> exceptionTranslator();

	Class<? extends RealtimePortfolioAggregation> target() {
		return RealtimePortfolioAggregationImpl.class;
	}

}
