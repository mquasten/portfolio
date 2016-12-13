package de.mq.portfolio.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.data.annotation.Version;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;

@Service
abstract class AbstractSerialisationUtil implements SerialisationUtil {
	
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.support.SerialisationUtil#toMap(java.lang.Object, java.util.Collection, java.util.Collection)
	 */
	@Override
	public final <T> Map<String,Object> toMap(final T  bean, final Collection<String>   properties, final Collection<String> mapping) {
		final Map<String,String> mappings = new HashMap<>();
		mapping.stream().filter(m -> m.split("[=]").length==2).forEach(m ->mappings.put( m.split("[=]")[0].trim(),  m.split("[=]")[1].trim()));
		
		
		final Map<String,Object> results = new HashMap<>();
		//ReflectionUtils.doWithFields(bean.getClass(), field -> {field.setAccessible(true); results.put(field.getName(), ReflectionUtils.getField(field, bean));}, field -> properties.contains(field.getName()));
		
		properties.forEach(property -> results.put( mappings.containsKey(property)? mappings.get(property) : property, this.value(bean, property)));
		
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
	public final <T>  void toBean(final Map<String,Object> map, final T target, final Collection<String> mapping ) {
		final Map<String,String> mappings = new HashMap<>();
		mapping.stream().filter(m -> m.split("[=]").length==2).forEach(m ->mappings.put( m.split("[=]")[0].trim(),  m.split("[=]")[1].trim()));
		
		final Collection<String> blackList =  mapping.stream().filter(m -> m.split("[=]").length==1 || (m.split("[=]").length==2&&!StringUtils.hasText(m.split("[=]")[1]))).map(m -> m.split("[=]")[0].trim()).collect(Collectors.toSet());
			
		map.keySet().stream().filter(key -> ! blackList.contains(key)).forEach(key -> {
		final String fieldName = mappings.containsKey(key) ? mappings.get(key) : key;
		
			assign(target, fieldName, map.get(key));
		
		});
		
	
		
	
	}
	
	
	
	@Lookup
	abstract ExceptionTranslationBuilder<? extends Object, ? extends AutoCloseable> builder() ; 
	
	final <T> Object value(final T  bean, final String   property){
		Assert.notNull(bean);
		StringUtils.hasText(property);
		final List<String> values =new ArrayList<>(Arrays.asList(property.split("[.]")));
	
		final Field field = ReflectionUtils.findField(bean.getClass(), values.get(0));
		Assert.notNull(field, "Field not found class:  "+  bean.getClass().getName() + " field:" + values.get(0));
		field.setAccessible(true);
	
		final Object result = ReflectionUtils.getField(field,bean);
		values.remove(0);
		if( result == null){
			return null;
		}
		
		if( ! values.isEmpty()){
			return value(result, StringUtils.collectionToDelimitedString(values, "."));
		}
		
		return result;
		
	}
	
	final <T> void assign(final T  bean, final String   property, final Object value){
		Assert.notNull(bean);
		StringUtils.hasText(property);
		final List<String> values =new ArrayList<>(Arrays.asList(property.split("[.]")));
	
		final Field field = ReflectionUtils.findField(bean.getClass(), values.get(0));
		Assert.notNull(field, "Field not found class:  "+  bean.getClass().getName() + " field:" + values.get(0));
		field.setAccessible(true);
	
		values.remove(0);
		
		
		if( values.isEmpty()){
			ReflectionUtils.setField(field, bean, value);
			return;
			
		}
		
		
		final Object result = ReflectionUtils.getField(field,bean);
		if( result == null){
			return;
		} 
		assign(result, StringUtils.collectionToDelimitedString(values, "."), value);
		
		
		
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.support.SerialisationUtil#execute(java.lang.Object, java.lang.String, java.util.Map)
	 */
	@Override
	public final void execute(final Object controller, final String regex, final Map<String,Object> params) {
		
		ReflectionUtils.doWithMethods(controller.getClass(), m -> {
			m.setAccessible(true);
			ReflectionUtils.invokeMethod(m, controller, arguments(m).stream().map(a -> params.get(a)).collect(Collectors.toList()).toArray());
		},  m -> m.getName().matches(regex) && arguments(m).size() == m.getParameterCount() &&  m.getParameterCount() > 0  );
		
	}

	private Collection<String> arguments(final Method m) {
		final Collection<String> results = new ArrayList<>();
		Arrays.asList(m.getParameterAnnotations()).forEach(  array -> Arrays.asList(array).stream().filter(a -> a.annotationType().equals(Parameter.class)).map(a -> ((Parameter) a).value() ).filter(name -> ! results.contains(name)).forEach(name -> results.add(name)));
	    return Collections.unmodifiableCollection(results);
	}

	@Override
	public final <T> long getAndIncVersion(final T  bean) {
		
		final Optional<Field> versionField = Arrays.asList(bean.getClass().getDeclaredFields()).stream().filter(field -> field.isAnnotationPresent(Version.class)&&(field.getType().equals(Long.class)||field.getType().equals(long.class))).findAny();
		if( ! versionField.isPresent()) {
			return 0;
		}
		versionField.get().setAccessible(true);
		Long result = (Long) ReflectionUtils.getField(versionField.get(), bean);
	    if( result == null){
	    	result = 0L;
	    }
		ReflectionUtils.setField(versionField.get(), bean, result+1);
		return result;
	}
	
}
