package de.mq.portfolio.batch.support;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import de.mq.portfolio.batch.JobContent;
import de.mq.portfolio.batch.MethodParameterInjection;



public class SimpleItemWriterServiceAdapterImpl<T> extends AbstractServiceAdapter implements  ItemWriter<T>  {

	protected SimpleItemWriterServiceAdapterImpl(final MethodParameterInjection<String> methodParameterInjection) {
		super(methodParameterInjection);
		
	}
	
	protected SimpleItemWriterServiceAdapterImpl(final MethodParameterInjection<String> methodInvoker, final JobContent<String> jobContent) {
		super(methodInvoker, jobContent);
	}
	
	@Override
	public final void write(final List<? extends T> items) throws Exception {
		items.forEach(item -> {
			super.putItem(item);
			invokeMethod();
		});
	}
	

	

}
