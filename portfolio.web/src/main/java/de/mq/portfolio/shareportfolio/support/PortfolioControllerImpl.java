package de.mq.portfolio.shareportfolio.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("portfolioController")
@Scope("singleton")
public class PortfolioControllerImpl {

	private final SharePortfolioService sharePortfolioService;

	@Autowired
	PortfolioControllerImpl(final SharePortfolioService sharePortfolioService) {
		this.sharePortfolioService = sharePortfolioService;
	}

	public void init(final PortfolioSearchAO portfolioSearchAO) {
		System.out.println(portfolioSearchAO);
	}

}
