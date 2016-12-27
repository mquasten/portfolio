package de.mq.portfolio.shareportfolio.support;


import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;

import org.springframework.stereotype.Component;

import com.mongodb.DBObject;

import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;

@Component
public class SharePortfolioListenerImpl extends AbstractMongoEventListener<SharePortfolioImpl> {

	static final String CORRELATIONS = "correlations";
	static final String COVARIANCES = "covariances";
	static final String VARIANCES = "variances";
	
	
	private  Collection<OptimisationAlgorithm> algorithms = new ArrayList<>();
	
	
	
	@Autowired
	SharePortfolioListenerImpl(final Collection<OptimisationAlgorithm> algorithms) {
		
		this.algorithms.addAll(algorithms);
	}



	@Override
	public void onBeforeSave(final SharePortfolioImpl sharePortfolio, final DBObject dbo) {

		if (sharePortfolio.isCommitted()) {
			return;
		}

		if (!sharePortfolio.onBeforeSave()) {
			return;
		}

		dbo.put(VARIANCES, sharePortfolio.variances());
		dbo.put(COVARIANCES, sharePortfolio.covariances());
		dbo.put(CORRELATIONS, sharePortfolio.correlations());

	}
	
	
	
	@Override
	public void onAfterLoad(final DBObject  entity) {
		System.out.println("*" + algorithms.size() +"*");
		// DI, touched for the very first time ...
	}

}
