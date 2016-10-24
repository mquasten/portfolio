package de.mq.portfolio.shareportfolio.support;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Component("retrospectiveController")
@Scope("singleton")
public class RetrospectiveControllerImpl {

	static final String REDIRECT_PATTERN = "retrospective?portfolioId=%s&filter=%s&faces-redirect=true";
	private final SharePortfolioService sharePortfolioService;
	private final Converter<String, String> currencyConverter;

	private final ExchangeRateService exchangeRateService;
	

	@Autowired
	RetrospectiveControllerImpl(final SharePortfolioService sharePortfolioService, final ExchangeRateService exchangeRateService, @Qualifier("currencyConverter") final Converter<String, String> currencyConverter) {
		this.sharePortfolioService = sharePortfolioService;
		this.exchangeRateService = exchangeRateService;
		this.currencyConverter = currencyConverter;
	}

	public void init(final RetrospectiveAO retrospectiveAO) {
		final SharePortfolio sharePortfolio = sharePortfolioService.sharePortfolio(retrospectiveAO.getPortfolioId());
		final SharePortfolioRetrospective sharePortfolioRetrospective = sharePortfolioService.retrospective(retrospectiveAO.getPortfolioId());

		retrospectiveAO.assign(sharePortfolioRetrospective, currencyConverter, Optional.of(exchangeRateService.exchangeRateCalculator(sharePortfolio.exchangeRateTranslations())));
	}

	public String show(final RetrospectiveAO retrospectiveAO) {

		return String.format(String.format(REDIRECT_PATTERN, retrospectiveAO.getPortfolioId(), retrospectiveAO.getFilter()));
	}
}
