package de.mq.portfolio.shareportfolio.support;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.stereotype.Component;

import com.mongodb.DBObject;

@Component
public class SharePortfolioListenerImpl extends AbstractMongoEventListener<SharePortfolioImpl> {

	static final String CORRELATIONS = "correlations";
	static final String COVARIANCES = "covariances";
	static final String VARIANCES = "variances";

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

}
