package de.mq.portfolio.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.ResourceAccessException;

public class SimpleSerialisationUtilImpl {
	
	final ExceptionTranslationBuilder<? extends Object, ? extends AutoCloseable> builder = new ExceptionTranslationBuilderImpl<>();
	
	public final <T> Map<String,Object> toMap(final T  bean, final Collection<?>   properties) {
		final Map<String,Object> results = new HashMap<>();
		ReflectionUtils.doWithFields(bean.getClass(), field -> {field.setAccessible(true); results.put(field.getName(), ReflectionUtils.getField(field, bean));}, field -> properties.contains(field.getName()));
		return results;
		
	}
	
	@SuppressWarnings("unchecked")
	public final String serialize(final Map<String,Object> values)  {
	
		return
				((ExceptionTranslationBuilder<String, ByteArrayOutputStream>)	
				builder).withResource(() -> new ByteArrayOutputStream()).withTranslation(ResourceAccessException.class, Arrays.asList(IOException.class)).withStatement((os) -> {
			final ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(values);
			return Base64.getEncoder().encodeToString(os.toByteArray());
		}).translate();
		
		
		
	}
	
	
	@SuppressWarnings("unchecked")
	public final  Map<String,Object>  deSerialize(final String value) {
		
		final byte[] bytes = Base64.getDecoder().decode(value.getBytes());
		return ((ExceptionTranslationBuilder<Map<String,Object>, ByteArrayInputStream>)builder).withResource(() -> new ByteArrayInputStream(bytes)).withTranslation(ResourceAccessException.class, Arrays.asList(IOException.class)).withStatement(is -> {
			final ObjectInputStream ois = new ObjectInputStream(is);
			return (Map<String, Object>) ois.readObject();
		}).translate(); 
		
		
	}
	
	public final <T>  void toBean(final Map<String,Object> map, T target) {
		ReflectionUtils.doWithFields(target.getClass(), field -> {
			field.setAccessible(true);
			ReflectionUtils.setField(field, target, map.get(field.getName()));
		}, field -> map.containsKey(field.getName()));
	}

}
