package de.mq.portfolio.support;

import de.mq.portfolio.batch.JobEnvironment;


//must be apublic class for easy rules ...
class ImportServiceRuleImpl<T> extends AbstractServiceRule<T> {
	
	
	static final int DEFAULT_PRIORITY = 1;



	ImportServiceRuleImpl(final T target, final String spEl, final JobEnvironment jobEnvironment) {
		super(target,  spEl, jobEnvironment, DEFAULT_PRIORITY);


	}
	
	


	@Override
	protected void action(final JobEnvironment jobEnvironment) {

		jobEnvironment.assign(ITEMS_PARAMETER, executeEl());
	}









	




	



	




	
	

	


	
}
