package de.mq.portfolio.share.support;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

import de.mq.portfolio.gateway.Gateway;


public class EntriesConverterTest {
	
	private  final Converter<Map<Object,Object>, Collection<Entry<Object,Object>>> converter = new EntriesConverter();
	
	@Test
	public final void convert() {
		final Map<Object,Object> map = new HashMap<>();
		map.put(Gateway.GoogleRateHistory, new Date());
		
		final Collection<Entry<Object,Object>> entries = converter.convert(map);
		
		Assert.assertEquals(map.entrySet(), entries);
	}

}
