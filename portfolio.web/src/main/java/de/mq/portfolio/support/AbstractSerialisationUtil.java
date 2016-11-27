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

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.ResourceAccessException;

@Service
abstract class AbstractSerialisationUtil implements SerialisationUtil {
	
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.support.SerialisationUtil#toMap(T, java.util.Collection)
	 */
	@Override
	public final <T> Map<String,Object> toMap(final T  bean, final Collection<?>   properties) {
		final Map<String,Object> results = new HashMap<>();
		ReflectionUtils.doWithFields(bean.getClass(), field -> {field.setAccessible(true); results.put(field.getName(), ReflectionUtils.getField(field, bean));}, field -> properties.contains(field.getName()));
		return results;
		
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.support.SerialisationUtil#serialize(java.util.Map)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final String serialize(final Map<String,Object> values)  {
	
		return
				((ExceptionTranslationBuilder<String, ByteArrayOutputStream>)	
				builder()).withResource(() -> new ByteArrayOutputStream()).withTranslation(ResourceAccessException.class, Arrays.asList(IOException.class)).withStatement((os) -> {
			final ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(values);
			return Base64.getEncoder().encodeToString(os.toByteArray());
		}).translate();
		
		
		
	}
	
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.support.SerialisationUtil#deSerialize(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final  Map<String,Object>  deSerialize(final String value) {
		
		final byte[] bytes = Base64.getDecoder().decode(value.getBytes());
		return ((ExceptionTranslationBuilder<Map<String,Object>, ByteArrayInputStream>)builder()).withResource(() -> new ByteArrayInputStream(bytes)).withTranslation(ResourceAccessException.class, Arrays.asList(IOException.class)).withStatement(is -> {
			final ObjectInputStream ois = new ObjectInputStream(is);
			return (Map<String, Object>) ois.readObject();
		}).translate(); 
		
		
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.support.SerialisationUtil#toBean(java.util.Map, T)
	 */
	@Override
	public final <T>  void toBean(final Map<String,Object> map, T target) {
		ReflectionUtils.doWithFields(target.getClass(), field -> {
			field.setAccessible(true);
			ReflectionUtils.setField(field, target, map.get(field.getName()));
		}, field -> map.containsKey(field.getName()));
	}
	
	@Lookup
	abstract ExceptionTranslationBuilder<? extends Object, ? extends AutoCloseable> builder() ; 

}
