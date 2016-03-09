package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mq.portfolio.shareportfolio.SharePortfolio;

@Component("portfolioSearch")
@Scope("view")
public class PortfolioSearchAO {
	
	private final List<SharePortfolio> sharePortfolios = new ArrayList<>();

	public List<SharePortfolio> getSharePortfolios() {
		return sharePortfolios;
	}
	

	public void setSharePortfolios(List<SharePortfolio> sharePortfolios) {
		this.sharePortfolios.clear();
		this.sharePortfolios.addAll(sharePortfolios);
	}
	

}
