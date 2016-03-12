package de.mq.portfolio.shareportfolio.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
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
		page(portfolioSearchAO);
		
	}

	public void page(final PortfolioSearchAO portfolioSearchAO) {
		portfolioSearchAO.setPageable( sharePortfolioService.pageable(portfolioSearchAO.criteria(),new Sort("name"), 10));
		
		portfolioSearchAO.setSharePortfolios(sharePortfolioService.portfolios(portfolioSearchAO.getPageable(), portfolioSearchAO.criteria()));
	}

}
