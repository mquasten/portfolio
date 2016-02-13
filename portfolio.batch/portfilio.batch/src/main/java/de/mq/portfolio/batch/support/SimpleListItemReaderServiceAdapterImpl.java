package de.mq.portfolio.batch.support;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.support.MethodInvoker;
import org.springframework.util.CollectionUtils;

class SimpleListItemReaderServiceAdapterImpl<T> extends AbstractServiceAdapter implements ItemReader<T>  {

	

	
	private List<T> items = null;
	
	
	SimpleListItemReaderServiceAdapterImpl(final MethodInvoker methodInvoker) {
		 super(methodInvoker);
	}
	
	SimpleListItemReaderServiceAdapterImpl(final MethodInvoker methodInvoker, final List<String> parameterNames) {
		 super(methodInvoker, parameterNames);
		 nullArgumentGuard(parameterNames);
	}
	
	
	SimpleListItemReaderServiceAdapterImpl(final MethodInvoker methodInvoker, final List<String> parameterNames, final Map<String, MethodInvoker> parameterEnrichers) {
		 this(methodInvoker, parameterNames);
		
	}

	private void nullArgumentGuard(final List<String> parameterNames) {
		if( parameterNames.stream().filter(name -> name==null).findAny().isPresent() ) {
				throwDomainObjectNotSupportedException();
		 }
	}

	
	
	@Override
	public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if( items==null){
			exeduteService();
		}
		
		if (!items.isEmpty()) {
			return items.remove(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void exeduteService() {
		
		
		
		
		 final Object result = executeMethod(() -> {throwDomainObjectNotSupportedException(); return null;});
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

	
	



	private void throwDomainObjectNotSupportedException() {
		throw new IllegalArgumentException("Domain object is not Supported as an argument for a reader");
	}



	
}


