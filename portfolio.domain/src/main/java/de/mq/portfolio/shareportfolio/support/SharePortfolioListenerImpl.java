package de.mq.portfolio.shareportfolio.support;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.mongodb.DBObject;

import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;

@Component
public class SharePortfolioListenerImpl extends AbstractMongoEventListener<SharePortfolioImpl> {

	static final String CORRELATIONS = "correlations";
	static final String COVARIANCES = "covariances";
	static final String VARIANCES = "variances";
	
	
	private  Map<OptimisationAlgorithm.AlgorithmType, OptimisationAlgorithm> algorithms = new HashMap<>();
	
	
	
	@Autowired
	SharePortfolioListenerImpl(final Collection<OptimisationAlgorithm> algorithms) {
		algorithms.forEach(a -> this.algorithms.put(a.algorithmType(), a));
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
	public void onAfterConvert(final DBObject dbo, final SharePortfolioImpl sharePortfolio) {
		ReflectionUtils.doWithFields(sharePortfolio.getClass(), field ->{ field.setAccessible(true);ReflectionUtils.setField(field, sharePortfolio, algorithms.get(sharePortfolio.algorithmType())); }, field -> field.getType().equals(OptimisationAlgorithm.class) );
	}

}
