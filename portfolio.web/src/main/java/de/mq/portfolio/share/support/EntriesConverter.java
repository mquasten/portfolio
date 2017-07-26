package de.mq.portfolio.share.support;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component("entriesConverter")
@Scope("singleton")
public class EntriesConverter implements Converter<Map<Object,Object>, Collection<Entry<Object,Object>>>{

	@Override
	public final Collection<Entry<Object, Object>> convert(Map<Object, Object> map) {
		return map.entrySet();
	}



}
