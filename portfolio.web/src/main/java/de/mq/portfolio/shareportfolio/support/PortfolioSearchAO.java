package de.mq.portfolio.shareportfolio.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.share.support.TimeCourseCriteriaImpl;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Component("portfolioSearch")
@Scope("view")
public class PortfolioSearchAO  implements Serializable {
	

	private static final long serialVersionUID = 1L;

	private final List<SharePortfolio> sharePortfolios = new ArrayList<>();
	
	private Pageable pageable; 
	
	private String share;

	private String portfolioName;
	


	private Optional<ExchangeRateCalculator> exchangeRateCalculator = Optional.empty();


	






	private String name;
	
	private SharePortfolio selectedPortfolio;
	
	




	public SharePortfolio getSelectedPortfolio() {
		return selectedPortfolio;
	}
	

	public boolean isSelectedPortfolioReadonly() {
		if( selectedPortfolio == null){
			return true;
		}
		return selectedPortfolio.isCommitted();
	}


	public boolean isRetrospectiveAware() {
		if( selectedPortfolio == null){
			return false;
		}
		if ( !selectedPortfolio.isCommitted() ) {
			return false;
		}
		return true;
	}
	
	
	public void setSelectedPortfolio(SharePortfolio selectedPortfolio) {
		this.selectedPortfolio = selectedPortfolio;
	}
	




	public String getName() {
		return name;
	}
	



	public void setName(String name) {
		this.name = name;
	}
	



	public String getShare() {
		return share;
	}
	



	public void setShare(String share) {
		this.share = share;
	}
	


	

	public Pageable getPageable() {
		return pageable;
	}
	


	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}
	


	public Collection<SharePortfolio> getSharePortfolios() {
		return sharePortfolios;
	}
	
	
	
	

	public void setSharePortfolios(Collection<SharePortfolio> sharePortfolios) {
		this.sharePortfolios.clear();
		this.sharePortfolios.addAll(sharePortfolios);
	}
	
	
	SharePortfolio criteria() {
		return new SharePortfolioImpl(name, Arrays.asList(new TimeCourseCriteriaImpl(share)));	
	}
	
	public String getPortfolioName() {
		return portfolioName;
	}
	





	public void setPortfolioName(String portfolioName) {
		this.portfolioName = portfolioName;
	}

	
	public ExchangeRateCalculator getExchangeRateCalculator() {
		Assert.isTrue(exchangeRateCalculator.isPresent(), "ExchangeRateCalculator is missing.");
		return exchangeRateCalculator.get();
	}


	public void setExchangeRateCalculator(ExchangeRateCalculator exchangeRateCalculator) {
		this.exchangeRateCalculator = Optional.of(exchangeRateCalculator);
	}
	

}
