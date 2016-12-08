package de.mq.portfolio.support;

import java.util.Collection;
import java.util.Map;

public interface SerialisationUtil {

	<T> Map<String, Object> toMap(T bean, Collection<String> properties, final Collection<String> mapping);

	String serialize(Map<String, Object> values);

	Map<String, Object> deSerialize(String value);

	<T> void toBean(Map<String, Object> map, T target, final Collection<String> mapping);

	

	

}