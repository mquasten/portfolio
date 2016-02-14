package de.mq.portfolio.batch;

import java.util.Map;

public interface JobContent<T> {

	void putContent(T key, Object value);

	Map<T, Object> content();

	

}