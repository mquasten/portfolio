package de.mq.portfolio.spring.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

public class BeanDefinitionRegistryPostProcessorTest {
	
	private static final String PACKAGES = "de.mq.portfolio.share.support,de.mq.portfolio.shareportfolio.support,de.mq.portfolio.user.support,de.mq.portfolio.exchangerate.support";

	private final  BeanDefinitionRegistryPostProcessor beanDefinitionRegistryPostProcessor = new  SimpleLookupRegistryPostProcessor(PACKAGES);

	private final BeanDefinitionRegistry beanDefinitionRegistry = Mockito.mock(BeanDefinitionRegistry.class);
	
	
	private final Collection<BeanDefinition> existingBeanDefinitions = new HashSet<>() ; 
	private final Collection<String> beanNames = new HashSet<>() ; 
	
	@Before
	public final void setup() {
		final ClassPathScanningCandidateComponentProvider buggyProvider = new ClassPathScanningCandidateComponentProvider(true);
		Arrays.asList(PACKAGES.split(",")).stream().forEach(pack -> {
			existingBeanDefinitions.addAll(buggyProvider.findCandidateComponents(pack)); 
			
		});
		
		existingBeanDefinitions.forEach(bd -> Mockito.when(beanDefinitionRegistry.getBeanDefinition(bd.getBeanClassName())).thenReturn(bd));
		
		beanNames.addAll(existingBeanDefinitions.stream().map(bd -> bd.getBeanClassName()).collect(Collectors.toList()));
		Mockito.when(beanDefinitionRegistry.getBeanDefinitionNames()).thenReturn(beanNames.toArray(new String[beanNames.size()]));
	}
	
	
	@Test
	public final void postProcessBeanDefinitionRegistry() {
		
		beanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry(beanDefinitionRegistry); 
	}
	
}
