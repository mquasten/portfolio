package de.mq.portfolio.spring.support;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

class SimpleLookupRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	private static final String ANNOTATION_VALUE_METHOD = "value";
	private final Collection<Class<? extends Annotation>> beanDefinitionAnnotations = new HashSet<>();

	private final Collection<String> packages = new HashSet<>();

	SimpleLookupRegistryPostProcessor(final String path) {
		beanDefinitionAnnotations.add(Service.class);
		beanDefinitionAnnotations.add(Repository.class);
		beanDefinitionAnnotations.add(Component.class);
		packages.addAll(Arrays.asList(path.split("[;,|: ]")).stream().map(packageName -> packageName.trim()).filter(packageName -> StringUtils.hasText(packageName)).collect(Collectors.toSet()));
	}

	@Override
	public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) throws BeansException {

		final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true) {
			@Override
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {

				final boolean result = super.isCandidateComponent(beanDefinition);
				if (result) {
					return true;
				}
				if (!beanDefinition.getMetadata().isAbstract()) {
					return false;
				}

				return !beanDefinition.getMetadata().getAnnotatedMethods(Lookup.class.getName()).isEmpty();

			}

		};

		final Collection<String> existing = Arrays.asList(registry.getBeanDefinitionNames()).stream().map(name -> registry.getBeanDefinition(name).getBeanClassName()).collect(Collectors.toSet());

		final Collection<Class<?>> toBeCreated = new HashSet<>();
		packages.forEach(packageName -> provider.findCandidateComponents(packageName).stream().map(bd -> bd.getBeanClassName()).filter(className -> !existing.contains(className)).map(className -> forName(className)).forEach(beanClass -> toBeCreated.add(beanClass)));

		toBeCreated.forEach(clazz -> {
			final BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
			if (clazz.isAnnotationPresent(Scope.class)) {
				definitionBuilder.setScope(clazz.getDeclaredAnnotation(Scope.class).value());
			}

			final String name = name(clazz, definitionBuilder.getBeanDefinition().getBeanClassName());
			registry.registerBeanDefinition(name, definitionBuilder.getBeanDefinition());

			System.out.println(String.format("*** Register Bean %s:  %s ***", name, definitionBuilder.getBeanDefinition().getBeanClassName()));

		});

	}

	private String name(final Class<?> targetClass, final String name) {
		for (final Class<? extends Annotation> annotationClass : beanDefinitionAnnotations) {
			final Annotation declaredAnnotation = targetClass.getDeclaredAnnotation(annotationClass);

			if (!targetClass.isAnnotationPresent(annotationClass)) {
				continue;
			}

			final String beanName = (String) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(annotationClass, ANNOTATION_VALUE_METHOD), declaredAnnotation);
			if (!StringUtils.hasText(beanName)) {
				continue;
			}
			return beanName;
		}
		return name;
	}

	private Class<?> forName(final String path) {
		try {
			return Class.forName(path);
		} catch (final ClassNotFoundException e) {
			throw new BeanDefinitionValidationException(String.format("Invalid className: %s", path), e);
		}
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

}