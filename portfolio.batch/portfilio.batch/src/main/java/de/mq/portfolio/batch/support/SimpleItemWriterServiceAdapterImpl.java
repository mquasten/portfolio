package de.mq.portfolio.batch.support;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.util.CollectionUtils;



public class SimpleItemWriterServiceAdapterImpl<T> extends AbstractServiceAdapter implements  ItemWriter<T>  {

	@SuppressWarnings("unchecked")
	protected SimpleItemWriterServiceAdapterImpl(Object service, String methodName) {
		super(service, methodName, CollectionUtils.arrayToList(new Object[]{null}));
		
	}
	protected SimpleItemWriterServiceAdapterImpl(Object service, String methodName, final List<String> parameterNames) {
		super(service, methodName, parameterNames);
		
	}
	@Override
	public final void write(final List<? extends T> items) throws Exception {
		items.forEach(item -> executeMethod(() -> item ));
	}
	

	

}
