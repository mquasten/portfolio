package de.mq.portfolio.batch.support;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;



















import java.util.Map;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.util.CollectionUtils;

import de.mq.portfolio.batch.JobContent;
import de.mq.portfolio.batch.MethodParameterInjection;

class SimpleListItemReaderServiceAdapterImpl<T> extends AbstractServiceAdapter implements ItemReader<T>  {

	

	
	private List<T> items = null;
	
	
	SimpleListItemReaderServiceAdapterImpl(final MethodParameterInjection<String> methodParameterInjection) {
		 super(methodParameterInjection);
	}
	
	
	SimpleListItemReaderServiceAdapterImpl(final MethodParameterInjection<String> methodParameterInjection, JobContent<String> jobContent, final Map<String, MethodParameterInjection<String>> enrichers) {
		 super(methodParameterInjection, jobContent, enrichers);
	}
	

	SimpleListItemReaderServiceAdapterImpl(final MethodParameterInjection<String> methodParameterInjection, JobContent<String> jobContent) {
		 super(methodParameterInjection, jobContent, new HashMap<>());
	}
	

	
	
	@Override
	public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if( items==null){
			exeduteService();
		}
		
		if (!items.isEmpty()) {
			return items.remove(0);
		} 
		items=null;
		return null;
	}

	@SuppressWarnings("unchecked")
	private void exeduteService() {
		
		
		
		
		 final Object result = invokeMethod();
		  items=new ArrayList<>();
		  if (result instanceof Collection<?>) {
			
			   items.addAll((Collection<? extends T>) result);
			   return;
		  }
		
		  if (result.getClass().isArray()) {
			  items.addAll(CollectionUtils.arrayToList(result));
			  return;
		  }
		  items.add((T) result);
	}

	
	



	
}


