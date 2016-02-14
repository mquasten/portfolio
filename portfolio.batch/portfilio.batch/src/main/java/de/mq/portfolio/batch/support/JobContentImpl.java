package de.mq.portfolio.batch.support;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



import de.mq.portfolio.batch.JobContent;


class JobContentImpl<T> implements JobContent<T>   {

	
	private final Map<T,Object> content =  new HashMap<>(); 
	
	JobContentImpl() {
		
	}
	
	JobContentImpl(final Map<T,Object> content) {
		content.clear();
		this.content.putAll(content);
	}

	/* (non-Javadoc)
	 * @see de.mq.portfolio.batch.support.JobContent#addBean(T, java.lang.Object)
	 */
	@Override
	public final void putContent(final T key , final Object value) {
		content.put(key, value);
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.batch.support.JobContent#content()
	 */
	@Override
	public  Map<T,Object> content() {
	    return Collections.unmodifiableMap(content);
	}

}
