package de.mq.portfolio.support;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;

import de.mq.portfolio.batch.RulesEngine;
import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.exchangerate.support.ExchangeRatesCSVLineConverterImpl;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.support.SharesCSVLineConverterImpl;
import de.mq.portfolio.share.support.SimpleCSVInputServiceImpl;

@Configuration
@ImportResource("classpath*:application.xml")
class RulesConfiguration {
	
	

	
	@Bean
	@Scope("prototype")
	RulesEngine importExchangeRates(final ExchangeRateService exchangeRateService, final RulesEngineBuilder rulesEngineBuilder) {
		return rulesEngineBuilder.withRule(new ImportServiceRuleImpl<>(new SimpleCSVInputServiceImpl<>(new ExchangeRatesCSVLineConverterImpl()), "read(#filename)")).withRule(new ProcessServiceRuleImpl<>(exchangeRateService,  "exchangeRate(#item)")).withRule(new ProcessServiceRuleImpl<>(exchangeRateService,  "save(#item)")).build();
	}

	
	@Bean
	@Scope("prototype")
	RulesEngine  importTimeCourses(final ShareService shareService, final RulesEngineBuilder rulesEngineBuilder ) {
		return rulesEngineBuilder.withRule(new ImportServiceRuleImpl<>( shareService ,  "shares()")).withRule(new ProcessServiceRuleImpl<>(shareService,  "timeCourse(#item)")).withRule(new ProcessServiceRuleImpl<>(shareService,  "replaceTimeCourse(#item)")).build();
	}

	
	@Bean
	@Scope("prototype")
	RulesEngine  importShares(final ShareService shareService, final RulesEngineBuilder rulesEngineBuilder ) {
		return rulesEngineBuilder.withRule(new ImportServiceRuleImpl<>(new SimpleCSVInputServiceImpl<>(new SharesCSVLineConverterImpl()), "read(#filename)")).withRule(new ProcessServiceRuleImpl<>(shareService, "save(#item)")).build();
	} 
	
	@Bean
	@Scope("prototype")
	RulesEngineBuilder rulesEngineBuilder() {
		return new SimpleRuleEngineBuilderImpl(); 
	}
	
}
