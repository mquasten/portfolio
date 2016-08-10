package de.mq.portfolio.support;

import java.util.Map;

class ImportServiceRuleImpl<T> extends AbstractServiceRule<T> {
	

	ImportServiceRuleImpl(final T target, final String spEl) {
		super(target,  spEl);
	}
	
	


	@Override
	protected void action(final  Map<String, Object> parameters) {
		parameters.put(ITEMS_PARAMETER, executeEl(parameters));
	}









	




	



	




	
	

	


	
}
