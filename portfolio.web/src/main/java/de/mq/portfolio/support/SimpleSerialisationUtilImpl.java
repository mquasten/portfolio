package de.mq.portfolio.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.ResourceAccessException;

public class SimpleSerialisationUtilImpl {
	
	final ExceptionTranslationBuilder<String, ByteArrayOutputStream> builder = new ExceptionTranslationBuilderImpl<>();
	
	public final Map<String,Object> toMap(final Object  bean, final Collection<?>   properties) {
		final Map<String,Object> results = new HashMap<>();
		ReflectionUtils.doWithFields(bean.getClass(), field -> {field.setAccessible(true); results.put(field.getName(), ReflectionUtils.getField(field, bean));}, field -> properties.contains(field.getName()));
		return results;
		
	}
	
	public final String serialize(final Map<String,Object> values)  {
	
		return builder.withResource(() -> new ByteArrayOutputStream()).withTranslation(ResourceAccessException.class, Arrays.asList(IOException.class)).withStatement((os) -> {
			final ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(values);
			return Base64.getEncoder().encodeToString(os.toByteArray());
		}).translate();
		
		
		
	}

}
