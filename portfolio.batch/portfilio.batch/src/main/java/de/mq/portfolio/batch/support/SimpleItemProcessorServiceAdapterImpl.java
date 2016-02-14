package de.mq.portfolio.batch.support;


import org.springframework.batch.item.ItemProcessor;

import de.mq.portfolio.batch.MethodParameterInjection;



public class SimpleItemProcessorServiceAdapterImpl<T,R> extends AbstractServiceAdapter implements ItemProcessor<T,R>{

	
	protected SimpleItemProcessorServiceAdapterImpl(final MethodParameterInjection<String> methodInvoker) {
		super(methodInvoker);
	}
	
	

	@SuppressWarnings("unchecked")
	@Override
	public final R process(final T item) throws Exception {
		super.putItem(item);
		return (R) invokeMethod();
	}



	

}
