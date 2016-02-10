package de.mq.portfolio.batch.support;


import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.CollectionUtils;



public class SimpleItemProcessorServiceAdapterImpl<T,R> extends AbstractServiceAdapter implements ItemProcessor<T,R>{

	@SuppressWarnings("unchecked")
	protected SimpleItemProcessorServiceAdapterImpl(final Object service, final String methodName) {
		super(service, methodName, CollectionUtils.arrayToList(new Object[]{null}));
	}
	
	protected SimpleItemProcessorServiceAdapterImpl(final Object service, final String methodName,final List<String> parameterNames) {
		super(service, methodName,parameterNames);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final R process(final T item) throws Exception {
		return (R) executeMethod(() -> item);
	}

}
