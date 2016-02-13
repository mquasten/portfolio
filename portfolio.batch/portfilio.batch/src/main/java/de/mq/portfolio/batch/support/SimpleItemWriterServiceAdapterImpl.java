package de.mq.portfolio.batch.support;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.support.MethodInvoker;
import org.springframework.util.CollectionUtils;



public class SimpleItemWriterServiceAdapterImpl<T> extends AbstractServiceAdapter implements  ItemWriter<T>  {

	@SuppressWarnings("unchecked")
	protected SimpleItemWriterServiceAdapterImpl(final MethodInvoker methodInvoker) {
		super(methodInvoker, CollectionUtils.arrayToList(new Object[]{null}));
		
	}
	protected SimpleItemWriterServiceAdapterImpl(final MethodInvoker methodInvoker, final List<String> parameterNames) {
		super(methodInvoker, parameterNames);
		
	}
	@Override
	public final void write(final List<? extends T> items) throws Exception {
		items.forEach(item -> executeMethod(() -> item ));
	}
	

	

}
