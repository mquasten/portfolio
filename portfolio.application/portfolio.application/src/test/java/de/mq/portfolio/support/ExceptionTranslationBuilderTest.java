package de.mq.portfolio.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;


import com.mscharhag.et.ReturningTryBlock;
import com.mscharhag.et.TryBlock;

import de.mq.portfolio.support.ExceptionTranslationBuilderImpl.ReturningTryBlockWithResource;
import de.mq.portfolio.support.ExceptionTranslationBuilderImpl.TryBlockWithResource;
import junit.framework.Assert;

public class ExceptionTranslationBuilderTest {
	
	private static final byte[] TEXT_AS_BYTES = "Kylie is nice and ...".getBytes();
	private final ExceptionTranslationBuilder<Object,AutoCloseable> exceptionTranslationBuilder = new  ExceptionTranslationBuilderImpl<>();
	
	
	@SuppressWarnings({"unchecked" })
	private final Supplier<ByteArrayInputStream> supplier = Mockito.mock(Supplier.class);
	
	@SuppressWarnings({ "rawtypes" })
	private final Entry entry = Mockito.mock(Entry.class);
	
	@Before
	public final void setup(){
		 Mockito.when(supplier.get()).thenReturn(new ByteArrayInputStream(TEXT_AS_BYTES));
		 Mockito.when(entry.getKey()).thenReturn(IllegalStateException.class);
		 Mockito.when(entry.getValue()).thenReturn(new Class[] {IOException.class});
	}
	
	@Test
	public final void withStatementReturningTryBlockWithResource() {
		@SuppressWarnings("unchecked")
		final ReturningTryBlockWithResource<Object,AutoCloseable> returningWithResource = Mockito.mock(ReturningTryBlockWithResource.class);
		
		Assert.assertEquals(exceptionTranslationBuilder, exceptionTranslationBuilder.withStatement( returningWithResource));
		
		final Map<Class<?>, Object> results = fields2Map(exceptionTranslationBuilder);
		Assert.assertEquals(returningWithResource, results.get(ReturningTryBlockWithResource.class));
		Assert.assertEquals(ExceptionTranslationBuilderImpl.Type.ReturningWithResource, results.get(ExceptionTranslationBuilderImpl.Type.class));
		
		Arrays.asList(ReturningTryBlock.class, TryBlockWithResource.class, TryBlock.class).forEach(clazz -> Assert.assertNull(results.get(clazz)));
		
	}

	private final Map<Class<?>, Object>  fields2Map(ExceptionTranslationBuilder<Object,AutoCloseable> exceptionTranslationBuilder) {
		final Map<Class<?>, Object> results = new HashMap<>();
		ReflectionUtils.doWithFields(exceptionTranslationBuilder.getClass(), field -> results.put(field.getType(), ReflectionTestUtils.getField(exceptionTranslationBuilder, field.getName())), field -> ! Modifier.isStatic(field.getModifiers()));
		return results;
	}
	
	
	
	@Test
	public final void withStatementReturningTryBlock() {
		@SuppressWarnings("unchecked")
		final ReturningTryBlock<Object> returningWithoutResource = Mockito.mock(ReturningTryBlock.class);
		
		Assert.assertEquals(exceptionTranslationBuilder, exceptionTranslationBuilder.withStatement( returningWithoutResource));
		
		final Map<Class<?>, Object> results = fields2Map(exceptionTranslationBuilder);
		Assert.assertEquals(returningWithoutResource, results.get(ReturningTryBlock.class));
		Assert.assertEquals(ExceptionTranslationBuilderImpl.Type.ReturningWithoutResource, results.get(ExceptionTranslationBuilderImpl.Type.class));
		
		Arrays.asList(ReturningTryBlockWithResource.class, TryBlockWithResource.class, TryBlock.class).forEach(clazz -> Assert.assertNull(results.get(clazz)));
		
	}

	@Test
	public final void withStatementTryBlockWithResource() {
		@SuppressWarnings("unchecked")
		final TryBlockWithResource<AutoCloseable> tryBlockWithResource = Mockito.mock(TryBlockWithResource.class);
		
		Assert.assertEquals(exceptionTranslationBuilder, exceptionTranslationBuilder.withStatement( tryBlockWithResource));
		
		final Map<Class<?>, Object> results = fields2Map(exceptionTranslationBuilder);
		Assert.assertEquals(tryBlockWithResource, results.get(TryBlockWithResource.class));
		Assert.assertEquals(ExceptionTranslationBuilderImpl.Type.VoidWithResource, results.get(ExceptionTranslationBuilderImpl.Type.class));
		
		Arrays.asList(ReturningTryBlockWithResource.class, ReturningTryBlock.class, TryBlock.class).forEach(clazz -> Assert.assertNull(results.get(clazz)));
		
	}
	
	@Test
	public final void withStatementTryBlock() {
	
		final TryBlock tryBlock = Mockito.mock(TryBlock.class);
		
		Assert.assertEquals(exceptionTranslationBuilder, exceptionTranslationBuilder.withStatement( tryBlock));
		
		final Map<Class<?>, Object> results = fields2Map(exceptionTranslationBuilder);
		Assert.assertEquals(tryBlock, results.get(TryBlock.class));
		Assert.assertEquals(ExceptionTranslationBuilderImpl.Type.VoidWithoutResource, results.get(ExceptionTranslationBuilderImpl.Type.class));
		
		Arrays.asList(ReturningTryBlockWithResource.class, ReturningTryBlock.class, TryBlockWithResource.class).forEach(clazz -> Assert.assertNull(results.get(clazz)));
		
	}
	
	 

	@Test
	public final void  withResource() {
		@SuppressWarnings("unchecked")
		final Supplier<AutoCloseable> supplier = Mockito.mock(Supplier.class);
		Assert.assertEquals(exceptionTranslationBuilder, exceptionTranslationBuilder.withResource(supplier));
		final Map<Class<?>, Object> results = fields2Map(exceptionTranslationBuilder);
		Assert.assertEquals(supplier, results.get(Supplier.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public final void  withTranslation() {
		Assert.assertEquals(exceptionTranslationBuilder, exceptionTranslationBuilder.withTranslation(IllegalStateException.class, new Class[]{IOException.class}));
		final Map<Class<?>, Object> results = fields2Map(exceptionTranslationBuilder);
		Assert.assertEquals(1, ((Collection<?>)results.get(Collection.class)).size());
		final Optional<Entry<Class<?>, Class<?>[]>> result = (Optional<Entry<Class<?>, Class<?>[]>>) ((Collection<?>)results.get(Collection.class)).stream().findAny();
		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(IllegalStateException.class, result.get().getKey());
		Assert.assertEquals(1,  result.get().getValue().length);
		Assert.assertEquals(IOException.class,  result.get().getValue()[0]);
		
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=IllegalArgumentException.class)
	public final void  withTranslationEmptySourceArray() {
		exceptionTranslationBuilder.withTranslation(IllegalStateException.class, new Class[]{});
	}
	
	@Test
	public final void translateReturningTryBlockWithResource() {
		 final Map<Class<?>,Field> fields = fields();
		 ReturningTryBlockWithResource<Object,ByteArrayInputStream> returningTryBlockWithResource = bis -> {
			 final byte[] buffer = new byte[TEXT_AS_BYTES.length];
			 bis.read(buffer);
			 return new String(buffer);
		 };
		 
		
		
				
		 ReflectionUtils.setField(fields.get(Supplier.class), exceptionTranslationBuilder, supplier);
		 ReflectionUtils.setField(fields.get(ReturningTryBlockWithResource.class), exceptionTranslationBuilder,returningTryBlockWithResource);
		 ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.ReturningWithResource);
		 Assert.assertEquals(new String(TEXT_AS_BYTES), exceptionTranslationBuilder.translate());
		 
	}

	private  Map<Class<?>,Field> fields() {
		final Map<Class<?>,Field> fields = new HashMap<>();
		ReflectionUtils.doWithFields(exceptionTranslationBuilder.getClass(), field -> {field.setAccessible(true);fields.put(field.getType(), field);}, field -> !Modifier.isStatic(field.getModifiers()));
	    return fields;
	}
	
	@Test(expected=IllegalStateException.class)
	public final void translateReturningTryBlockWithResourceSupplierSucks() {
		
		
		final ReturningTryBlockWithResource<?,?> returningTryBlockWithResource = Mockito.mock(ReturningTryBlockWithResource.class);
		
		final Supplier<?> supplier = Mockito.mock(Supplier.class);
		 Mockito.doThrow(IOException.class).when(supplier).get();
		 final Map<Class<?>,Field> fields = fields();
		 ReflectionUtils.setField(fields.get(Supplier.class), exceptionTranslationBuilder, supplier);
		 ReflectionUtils.setField(fields.get(ReturningTryBlockWithResource.class), exceptionTranslationBuilder,returningTryBlockWithResource);
		 ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.ReturningWithResource);
		 ReflectionUtils.setField(fields.get(Collection.class), exceptionTranslationBuilder, Arrays.asList(entry));
		 exceptionTranslationBuilder.translate();	
	
	} 
	
	@Test(expected=IllegalStateException.class)
	public final void translateReturningTryBlockWithResourceBlockSucks() {
		
		 ReturningTryBlockWithResource<Object,ByteArrayInputStream> returningTryBlockWithResource = bis -> {throw new IOException("Don't worry, only for test"); };
		
		 final Map<Class<?>,Field> fields = fields();
		 ReflectionUtils.setField(fields.get(Supplier.class), exceptionTranslationBuilder, supplier);
		 ReflectionUtils.setField(fields.get(ReturningTryBlockWithResource.class), exceptionTranslationBuilder,returningTryBlockWithResource);
		 ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.ReturningWithResource);
		 ReflectionUtils.setField(fields.get(Collection.class), exceptionTranslationBuilder, Arrays.asList(entry));
		 exceptionTranslationBuilder.translate();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void translateResourceStatementMissmatch() {
		ReturningTryBlockWithResource<Object,ByteArrayInputStream> returningTryBlockWithResource = bis -> new String(TEXT_AS_BYTES);
		 final Map<Class<?>,Field> fields = fields();
		 ReflectionUtils.setField(fields.get(ReturningTryBlockWithResource.class), exceptionTranslationBuilder,returningTryBlockWithResource);
		 ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.ReturningWithResource);
		 ReflectionUtils.setField(fields.get(Collection.class), exceptionTranslationBuilder, Arrays.asList(entry));
		 exceptionTranslationBuilder.translate();
	}
	
	@Test
	public final void  returningWithoutResource() {
		
		 final ReturningTryBlock<String> returningTryBlock =  () -> new String(TEXT_AS_BYTES);
		 final Map<Class<?>,Field> fields = fields();
		 ReflectionUtils.setField(fields.get(ReturningTryBlock.class), exceptionTranslationBuilder,returningTryBlock);
		 ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.ReturningWithoutResource);
		 Assert.assertEquals(new String(TEXT_AS_BYTES),exceptionTranslationBuilder.translate());
		
	}
	
	@Test(expected=IllegalStateException.class)
	public final void  returningWithoutResourceSucks() {
		 final ReturningTryBlock<String> returningTryBlock =  () ->  {throw new IOException("Don't worry, only for test"); };
		 final Map<Class<?>,Field> fields = fields();
		 ReflectionUtils.setField(fields.get(ReturningTryBlock.class), exceptionTranslationBuilder,returningTryBlock);
		 ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.ReturningWithoutResource);
		 ReflectionUtils.setField(fields.get(Collection.class), exceptionTranslationBuilder, Arrays.asList(entry));
		 Assert.assertEquals(new String(TEXT_AS_BYTES),exceptionTranslationBuilder.translate());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void  returningWithoutResourceStatementMissmatch() {
		
		 final ReturningTryBlock<?> returningTryBlock =  Mockito.mock(ReturningTryBlock.class);
		
		 final Supplier<?> supplier = Mockito.mock(Supplier.class);
		 final Map<Class<?>,Field> fields = fields();
		 ReflectionUtils.setField(fields.get(ReturningTryBlock.class), exceptionTranslationBuilder,returningTryBlock);
		 ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.ReturningWithoutResource);
		 ReflectionUtils.setField(fields.get(Supplier.class), exceptionTranslationBuilder, supplier);
		 exceptionTranslationBuilder.translate();	
	}
		
	
	
	
}
