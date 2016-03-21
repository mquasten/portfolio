package de.mq.portfolio.shareportfolio.support;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import de.mq.portfolio.shareportfolio.SharePortfolio;
import de.mq.portfolio.support.UserModel;

@Component("portfolioController")
@Scope("singleton")
public class PortfolioControllerImpl {

	private static final String REDIRECT_TO_PORTFOLIOS_PAGE = "portfolios?faces-redirect=true";
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
	
	
	public String save(final SharePortfolio sharePortfolio, final FacesContext facesContext, final String existsMessage) {
		try {
			sharePortfolioService.save(sharePortfolio);
			return REDIRECT_TO_PORTFOLIOS_PAGE;
		} catch(final DuplicateKeyException de){
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, existsMessage, null));
		   return null; 
		}
	}
	
}
