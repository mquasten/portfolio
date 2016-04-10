package de.mq.portfolio.shareportfolio.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

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

	

}
