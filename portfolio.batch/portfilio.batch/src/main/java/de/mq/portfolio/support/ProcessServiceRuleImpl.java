package de.mq.portfolio.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;



//must be apublic class for easy rules ...
class ProcessServiceRuleImpl<T,R> extends AbstractServiceRule<T> {

	
	
	ProcessServiceRuleImpl(final T target, final String spEl) {
		super(target, spEl);
		
	}

	@Override
	protected void action(final Map<String, Object> parameters) {
		@SuppressWarnings("unchecked")
		final Collection<R> items = (Collection<R>) parameters.get(ITEMS_PARAMETER);
		final Collection<R> results = new ArrayList<>();
		items.forEach(item -> {
			parameters.put("item", item);
			final R result = executeEl(parameters);
			results.add( (result !=null) ? result :item);
		});
		parameters.put(ITEMS_PARAMETER, results);
		
	
		
	}



	

	

	


	

	

	
}
