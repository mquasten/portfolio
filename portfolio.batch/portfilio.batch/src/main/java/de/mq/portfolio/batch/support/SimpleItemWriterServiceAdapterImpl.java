package de.mq.portfolio.batch.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.item.ItemWriter;

import de.mq.portfolio.batch.JobContent;
import de.mq.portfolio.batch.MethodParameterInjection;



class SimpleItemWriterServiceAdapterImpl<T> extends AbstractServiceAdapter implements  ItemWriter<T>  {

	private List<T> items = new ArrayList<>();; 
	
	private  Optional<MethodParameterInjection<String>> preProcessor = Optional.empty();
	
	protected SimpleItemWriterServiceAdapterImpl(final MethodParameterInjection<String> methodParameterInjection) {
		super(methodParameterInjection);
		
	}
	
	protected SimpleItemWriterServiceAdapterImpl(final MethodParameterInjection<String> methodInvoker, final JobContent<String> jobContent) {
		super(methodInvoker, jobContent);
	}
	
	protected SimpleItemWriterServiceAdapterImpl(final MethodParameterInjection<String> methodParameterInjection, final MethodParameterInjection<String> preProcessor) {
		super(methodParameterInjection);
		this.preProcessor=Optional.of(preProcessor);
	}
	
	protected SimpleItemWriterServiceAdapterImpl(final MethodParameterInjection<String> methodInvoker, final JobContent<String> jobContent,final MethodParameterInjection<String> preProcessor) {
		super(methodInvoker, jobContent);
		this.preProcessor=Optional.of(preProcessor);
	}
	
	@Override
	public final void write(final List<? extends T> items) throws Exception {
		this.items.addAll(items);
	}

	@AfterStep
	void afterStep() {
		
		items().forEach(item -> {
			super.putItem(item);
			invokeMethod();
		}); 
		this.items.clear();
	}

	@SuppressWarnings("unchecked")
	private final Collection<T> items() {
		
		if( ! preProcessor.isPresent() ) {
			return items;
		}
		
		final Map<String,Object> params = new HashMap<>();
		params.putAll(params());
		params.put(null, items);		
		
		return (Collection<T>) preProcessor.get().invokeMethod(params);
	}
	


	

}
