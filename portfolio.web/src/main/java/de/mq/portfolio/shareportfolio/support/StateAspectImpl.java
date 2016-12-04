package de.mq.portfolio.shareportfolio.support;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import de.mq.portfolio.support.SerialisationUtil;

@Component
@Aspect
class StateAspectImpl {
	
	@Autowired
	private SerialisationUtil serialisationUtil;
	
	 @After("execution(* de.mq.portfolio.shareportfolio.support.AbstractPortfolioController.page(..)) && @annotation(de.mq.portfolio.support.Serialize) && args(portfolioSearchAO,..))")
	 void serialize(final PortfolioSearchAO portfolioSearchAO)  {
		 
		 System.out.println("get the party started");
	 }

}
