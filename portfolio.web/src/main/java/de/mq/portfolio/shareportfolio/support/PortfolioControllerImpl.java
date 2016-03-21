package de.mq.portfolio.shareportfolio.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import de.mq.portfolio.shareportfolio.SharePortfolio;
import de.mq.portfolio.support.UserModel;

@Component("portfolioController")
@Scope("singleton")
public class PortfolioControllerImpl {

	private final SharePortfolioService sharePortfolioService;

	@Autowired
	PortfolioControllerImpl(final SharePortfolioService sharePortfolioService) {
		this.sharePortfolioService = sharePortfolioService;
	}

	public void init(final PortfolioSearchAO portfolioSearchAO, final UserModel userModel) {
		page(portfolioSearchAO);
		if( userModel.getPortfolioId() != null ){
			portfolioSearchAO.setPortfolioName(sharePortfolioService.sharePortfolio(userModel.getPortfolioId()).name());
		}
		
	}

	public void page(final PortfolioSearchAO portfolioSearchAO) {
		portfolioSearchAO.setPageable( sharePortfolioService.pageable(portfolioSearchAO.criteria(),new Sort("name"), 10));
		
		portfolioSearchAO.setSharePortfolios(sharePortfolioService.portfolios(portfolioSearchAO.getPageable(), portfolioSearchAO.criteria()));
	}

	
	public void activate(final PortfolioSearchAO portfolioSearchAO, final UserModel userModel) {
		userModel.setPortfolioId(portfolioSearchAO.getSelectedPortfolio().id());
		portfolioSearchAO.setPortfolioName(portfolioSearchAO.getSelectedPortfolio().name());
	}
	
	
	public void save(final SharePortfolio sharePortfolio) {
		System.out.println(sharePortfolio.name());
		System.out.println("save");
	}
	
}
