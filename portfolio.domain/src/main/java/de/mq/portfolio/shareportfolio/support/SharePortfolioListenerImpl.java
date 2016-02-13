package de.mq.portfolio.shareportfolio.support;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.stereotype.Component;

import com.mongodb.DBObject;


@Component
public class SharePortfolioListenerImpl extends AbstractMongoEventListener<SharePortfolioImpl> {

	@Override
	public void onBeforeSave(final SharePortfolioImpl sharePortfolio, final DBObject dbo) {
		if( sharePortfolio.isCommitted()) {
			return;
		}
		
		
		
		if( ! sharePortfolio.onBeforeSave() ) {
			return;
		}
		
		dbo.put("variances", sharePortfolio.variances());
		dbo.put("covariances", sharePortfolio.covariances());
		dbo.put("correlations", sharePortfolio.correlations());
		
	}
	
}
