package de.mq.portfolio.support;

import static org.easyrules.core.RulesEngineBuilder.aNewRulesEngine;

import java.util.HashMap;
import java.util.Map;

import org.easyrules.api.RulesEngine;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.SimpleThreadScope;

import de.mq.portfolio.batch.JobEnvironment;
import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.exchangerate.support.ExchangeRatesCSVLineConverterImpl;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.support.SharesCSVLineConverterImpl;
import de.mq.portfolio.share.support.SimpleCSVInputServiceImpl;

@Configuration
@ImportResource("classpath*:application.xml")
class RulesConfiguration {
	
	@Bean
	@Scope("thread")
	JobEnvironment jobEnvironment() {
		return new JobEnvironmentImpl();
	}

	@Bean
	@Scope("singleton")
	static BeanFactoryPostProcessor customScopeConfigurer() {
		final Map<String, Object> scopes = new HashMap<>();
		scopes.put("thread", new SimpleThreadScope());
		CustomScopeConfigurer  customScopeConfigurer = new CustomScopeConfigurer();
		customScopeConfigurer.setScopes(scopes);
		return customScopeConfigurer;
	}
	@Bean
	@Scope("thread")
	RulesEngine importExchangeRates(final ExchangeRateService exchangeRateService, final JobEnvironment jobEnvironment) {
		final RulesEngine rulesEngine = newRulesEngine(jobEnvironment);

		rulesEngine.registerRule(new ImportServiceRuleImpl<>(new SimpleCSVInputServiceImpl<>(new ExchangeRatesCSVLineConverterImpl()), "read(#filename)", jobEnvironment));
		
		rulesEngine.registerRule(new ProcessServiceRuleImpl<>(exchangeRateService,  "exchangeRate(#item)", jobEnvironment));
		rulesEngine.registerRule(new ProcessServiceRuleImpl<>(exchangeRateService,  "save(#item)",jobEnvironment, 3));
		return rulesEngine;

	}

	private RulesEngine newRulesEngine(final JobEnvironment jobEnvironment) {
		return  aNewRulesEngine().withSkipOnFirstAppliedRule(false).withSkipOnFirstFailedRule(true).withRulePriorityThreshold(Integer.MAX_VALUE).withSilentMode(true).withRuleListener(new SimpleRuleListenerImpl(jobEnvironment)).build();
	}
	
	@Bean
	@Scope("thread")
	RulesEngine  importTimeCourses(final ShareService shareService , final JobEnvironment jobEnvironment) {
		final RulesEngine rulesEngine = newRulesEngine(jobEnvironment);
		
		rulesEngine.registerRule(new ImportServiceRuleImpl<>( shareService ,  "shares()", jobEnvironment));
		rulesEngine.registerRule(new ProcessServiceRuleImpl<>(shareService,  "timeCourse(#item)", jobEnvironment));
		rulesEngine.registerRule(new ProcessServiceRuleImpl<>(shareService,  "replacetTimeCourse(#item)",jobEnvironment, 3));
		return rulesEngine;
		
	}
	
	@Bean
	@Scope("thread")
	RulesEngine  importShares(final ShareService shareService , final JobEnvironment jobEnvironment) {
		final RulesEngine rulesEngine = newRulesEngine(jobEnvironment);
		rulesEngine.registerRule(new ImportServiceRuleImpl<>(new SimpleCSVInputServiceImpl<>(new SharesCSVLineConverterImpl()), "read(#filename)", jobEnvironment));
		rulesEngine.registerRule(new ProcessServiceRuleImpl<>(shareService, "save(#item)", jobEnvironment));
		return rulesEngine;
		
	}
}
