package de.mq.portfolio.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import de.mq.portfolio.batch.RulesEngine;
import de.mq.portfolio.support.SimpleCommandlineProcessorImpl.Main;

public class BatchProcessorImpl {

	private static Class<? extends BatchProcessorImpl> TARGET = BatchProcessorImpl.class;

	@Main
	void process(final List<String> arguments) {

		Assert.notEmpty(arguments, "At least the name of the ruleengine should be given as first parameter.");
		final String names = arguments.stream().findFirst().get();
		final Collection<String> params = IntStream.range(1, arguments.size()).mapToObj(i -> arguments.get(i)).collect(Collectors.toList());

		try (ConfigurableApplicationContext ctx = applicationContext()) {
			Arrays.asList(names.split("[,]")).forEach(name -> process(name, params, ctx));
			;
		}
	}

	AnnotationConfigApplicationContext applicationContext() {
		return new AnnotationConfigApplicationContext(RulesConfiguration.class);
	}

	private void process(final String name, final Collection<String> params, ApplicationContext ctx) {

		final RulesEngine rulesEngine = ctx.getBean(name, RulesEngine.class);

		final Map<String, Object> parameters = new HashMap<>();
		params.forEach(entry -> {
			final String[] values = entry.split("=");
			Assert.isTrue(values.length == 2, "Parameter should be given in format <key>=<value> as argument.");
			parameters.put(values[0].trim(), values[1].trim());
		});
		rulesEngine.fireRules(parameters);

		rulesEngine.failed().forEach(entry -> {
			System.err.println(String.format("Rule %s finished with exception:", entry.getKey()));
			System.err.println(entry.getValue());
			System.err.println();

		});

		Assert.isTrue(CollectionUtils.isEmpty(rulesEngine.failed()), "Rules processed with errors");
		Assert.isTrue(!CollectionUtils.isEmpty(rulesEngine.processed()), "At least one rule must be processed.");
		System.out.println(String.format("Sucessfully finished rules for %s:", name));
		rulesEngine.processed().forEach(rule -> System.out.println(String.format("\t%s", rule)));
	}

	public static void main(final String[] args) {
		BeanUtils.instantiateClass(TARGET).process(Arrays.asList(args));
	}

}
