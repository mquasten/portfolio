package de.mq.portfolio.share.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.exchangerate.support.ExchangeRateCalculatorBuilder;
import de.mq.portfolio.exchangerate.support.ExchangeRateDatebaseRepository;
import de.mq.portfolio.exchangerate.support.ExchangeRateImpl;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.TimeCourse;

@Component
abstract class TimeCourseDividendsCurrencyConverterImpl implements TimeCourseConverter {

	static final String CURRENCY_EUR = "EUR";
	private final ExchangeRateDatebaseRepository exchangeRateDatebaseRepository;
	
	TimeCourseDividendsCurrencyConverterImpl(final ExchangeRateDatebaseRepository exchangeRateDatebaseRepository) {
		this.exchangeRateDatebaseRepository=exchangeRateDatebaseRepository;
	}
	
	@Override
	public TimeCourse convert(final TimeCourse source) {
		final ExchangeRate exchangerate = new ExchangeRateImpl(CURRENCY_EUR, source.share().currency() );
		final Collection<ExchangeRate> exchangeRates = exchangeRateDatebaseRepository.exchangerates(Arrays.asList(exchangerate));
		
		final ExchangeRateCalculator exchangeRateCalculator = exchangeRateCalculatorBuilder().withExchangeRates(exchangeRates).build();
		
		final Collection<Data> dividends  =  source.dividends().stream().map(data -> new DataImpl(data.date(), data.value() * exchangeRateCalculator.factor(exchangerate, data.date()))).collect(Collectors.toList());
		return new TimeCourseImpl(source.share(), source.rates(), dividends);
	}

	@Override
	public TimeCourseConverterType timeCourseConverterType() {
		return TimeCourseConverter.TimeCourseConverterType.EurDividendsCurrency;
	}
	
	@Lookup
	abstract ExchangeRateCalculatorBuilder exchangeRateCalculatorBuilder(); 

}
