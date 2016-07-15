package de.mq.portfolio.batch.support;

import static org.easyrules.core.RulesEngineBuilder.aNewRulesEngine;

import java.util.HashMap;
import java.util.Map;

import org.easyrules.api.RulesEngine;
import org.easyrules.core.RulesEngineBuilder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.SimpleThreadScope;

import de.mq.portfolio.batch.JobEnvironment;
import de.mq.portfolio.exchangerate.support.ExchangeRateService;

@Configuration
@ImportResource("classpath*:application.xml")
public class RulesConfiguration {
	
	@Bean
	@Scope("thread")
	JobEnvironment jobEnvironment() {
		return new JobEnvironmentImpl();
	}

	@Bean
	@Scope("singleton")
	public static BeanFactoryPostProcessor customScopeConfigurer() {
		final Map<String, Object> scopes = new HashMap<>();
		scopes.put("thread", new SimpleThreadScope());
		CustomScopeConfigurer  customScopeConfigurer = new CustomScopeConfigurer();
		customScopeConfigurer.setScopes(scopes);
		return customScopeConfigurer;
	}
	@Bean
	@Scope("thread")
	public RulesEngine importExchangeRates(final ExchangeRateService exchangeRateService, final JobEnvironment jobEnvironment) {
		
		    
		        RulesEngineBuilder rulesEngineBuilder = aNewRulesEngine()
		                .withSkipOnFirstAppliedRule(false)
		                .withSkipOnFirstFailedRule(true)
		                .withRulePriorityThreshold(Integer.MAX_VALUE)
		                .withSilentMode(true)
		        .withRuleListener(new SimpleRuleListenerImpl(jobEnvironment));
		        final RulesEngine rulesEngine = rulesEngineBuilder.build();
		        
		        return rulesEngine;
		   
	}
}
