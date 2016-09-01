package de.mq.portfolio.spring.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.stereotype.Service;

import de.mq.portfolio.spring.support.mock.AbstractServiceWithLookup;
import junit.framework.Assert;

public class BeanDefinitionRegistryPostProcessorTest {
	
	private static final String MOCK_PACKAGE_TEST = ".mock";
	private  final Class<?> ABSTRACT_PORTFOLIO_SERVICE = classOf("de.mq.portfolio.shareportfolio.support.AbstractSharePortfolioService");
	private  final Class<?> ABSTRACT_EXCHANGE_RATE_SERVICE = classOf("de.mq.portfolio.exchangerate.support.AbstractExchangeRateService");

	private final static Class<?> classOf(final String name)  {
		try {
			return Class.forName(name);
		} catch (final ClassNotFoundException ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	private static final String PACKAGES = "de.mq.portfolio.share.support,de.mq.portfolio.shareportfolio.support,de.mq.portfolio.user.support,de.mq.portfolio.exchangerate.support";

	private final  BeanDefinitionRegistryPostProcessor beanDefinitionRegistryPostProcessor = new  SimpleLookupRegistryPostProcessor(PACKAGES);

	private final BeanDefinitionRegistry beanDefinitionRegistry = Mockito.mock(BeanDefinitionRegistry.class);
	
	
	private final Collection<BeanDefinition> existingBeanDefinitions = new HashSet<>() ; 
	private final Collection<String> beanNames = new HashSet<>() ; 
	
	private ArgumentCaptor<String> beanNameCaptor = ArgumentCaptor.forClass(String.class);
	private ArgumentCaptor<BeanDefinition> beanDefinitionCaptor = ArgumentCaptor.forClass(BeanDefinition.class);
	
	
	private final void prepare() {
		final ClassPathScanningCandidateComponentProvider buggyProvider = new ClassPathScanningCandidateComponentProvider(true);
		Arrays.asList(PACKAGES.split(",")).stream().forEach(pack -> {
			existingBeanDefinitions.addAll(buggyProvider.findCandidateComponents(pack)); 
			
		});
		
		existingBeanDefinitions.forEach(bd -> Mockito.when(beanDefinitionRegistry.getBeanDefinition(bd.getBeanClassName())).thenReturn(bd));
		
		beanNames.addAll(existingBeanDefinitions.stream().map(bd -> bd.getBeanClassName()).collect(Collectors.toList()));
		Mockito.when(beanDefinitionRegistry.getBeanDefinitionNames()).thenReturn(beanNames.toArray(new String[beanNames.size()]));
	}
	
	
	@Test
	public final void postProcessBeanDefinitionRegistry()  {
		
		prepare();
		
		
		beanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry(beanDefinitionRegistry); 
		Mockito.verify(beanDefinitionRegistry, Mockito.times(2)).registerBeanDefinition(beanNameCaptor.capture(),beanDefinitionCaptor.capture());
		
		Assert.assertEquals(beanNameCaptor.getAllValues().size(), 2);
		Assert.assertEquals(beanDefinitionCaptor.getAllValues().size(), 2);
		
		
		Assert.assertTrue(beanNameCaptor.getAllValues().contains(ABSTRACT_PORTFOLIO_SERVICE.getAnnotation(Service.class).value()));
		Assert.assertTrue(beanNameCaptor.getAllValues().contains(ABSTRACT_EXCHANGE_RATE_SERVICE.getAnnotation(Service.class).value()));
		
		
		IntStream.range(0, 2).forEach( i -> {
			Assert.assertTrue(beanDefinitionCaptor.getAllValues().get(i).isSingleton());
			Assert.assertEquals(beanNameCaptor.getAllValues().get(i), classOf(beanDefinitionCaptor.getAllValues().get(i).getBeanClassName()).getAnnotation(Service.class).value());
		});
	}
	
	
	@Test
	public final void postProcessBeanDefinitionRegistryLookup() {
		
		Mockito.when(beanDefinitionRegistry.getBeanDefinitionNames()).thenReturn(new String[]{});
		
		final  BeanDefinitionRegistryPostProcessor beanDefinitionRegistryPostProcessor = new  SimpleLookupRegistryPostProcessor(getClass().getPackage() +MOCK_PACKAGE_TEST);
		beanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry(beanDefinitionRegistry);
		
		Mockito.verify(beanDefinitionRegistry).registerBeanDefinition(beanNameCaptor.capture(),beanDefinitionCaptor.capture());
		System.out.println(beanNameCaptor.getValue());
		Assert.assertFalse(beanDefinitionCaptor.getValue().isSingleton());
		Assert.assertTrue(beanDefinitionCaptor.getValue().isPrototype());
		Assert.assertEquals(AbstractServiceWithLookup.class.getName(), beanDefinitionCaptor.getValue().getBeanClassName());
		Assert.assertEquals(AbstractServiceWithLookup.class.getName(), beanNameCaptor.getValue());
		
		
		
	}
	
}
