package de.mq.portfolio.support;


import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;

import de.mq.portfolio.batch.RulesEngine;
import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.exchangerate.support.ExchangeRatesCSVLineConverterImpl;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.support.SharesCSVLineConverterImpl;

@Configuration
@ImportResource("classpath*:application.xml")
class RulesConfiguration {
	
	

	
	static final String SPEL_PROCESS_EXCHANGE_RATE_ITEM = "exchangeRate(#item)";
	static final String SPEL_REPLACE_TIME_COURSE_ITEM = "replaceTimeCourse(#item)";
	static final String SPEL_PROCESS_TIME_COURSE_ITEM = "timeCourse(#item)";
	static final String SPEL_INPUT_SHARES = "shares()";
	static final String SPEL_SAVE_ITEM = "save(#item)";
	static final String SPEL_READ_FILENAME = "read(#filename)";

	@Bean
	@Scope("prototype")
	RulesEngine importExchangeRates(final ExchangeRateService exchangeRateService, final RulesEngineBuilder rulesEngineBuilder) {
		return rulesEngineBuilder.withRule(new ImportServiceRuleImpl<>(new SimpleCSVInputServiceImpl<>(new ExchangeRatesCSVLineConverterImpl()), SPEL_READ_FILENAME)).withRule(new ProcessServiceRuleImpl<>(exchangeRateService,  SPEL_PROCESS_EXCHANGE_RATE_ITEM)).withRule(new ProcessServiceRuleImpl<>(exchangeRateService,  SPEL_SAVE_ITEM)).build();
	}

	
	@Bean
	@Scope("prototype")
	RulesEngine  importTimeCourses(final ShareService shareService, final RulesEngineBuilder rulesEngineBuilder ) {
		return rulesEngineBuilder.withRule(new ImportServiceRuleImpl<>( shareService ,  SPEL_INPUT_SHARES)).withRule(new ProcessServiceRuleImpl<>(shareService,  SPEL_PROCESS_TIME_COURSE_ITEM)).withRule(new ProcessServiceRuleImpl<>(shareService,  SPEL_REPLACE_TIME_COURSE_ITEM)).build();
	}

	
	@Bean
	@Scope("prototype")
	RulesEngine  importShares(final ShareService shareService, final RulesEngineBuilder rulesEngineBuilder ) {
		return rulesEngineBuilder.withRule(new ImportServiceRuleImpl<>(new SimpleCSVInputServiceImpl<>(new SharesCSVLineConverterImpl()), SPEL_READ_FILENAME)).withRule(new ProcessServiceRuleImpl<>(shareService, SPEL_SAVE_ITEM)).build();
	} 
	
	@Bean
	@Scope("prototype")
	RulesEngineBuilder rulesEngineBuilder() {
		return new SimpleRuleEngineBuilderImpl(); 
	}
	
	@Bean
	@Scope("singleton")
	static BeanFactoryPostProcessor commandLineProcessor(){
		return new SimpleCommandlineProcessorImpl(DummyBean.class);
		
	}
	
	@Bean
	@Scope("singleton")
	DummyBean mainDelegate() {
		return new DummyBean();
	}
	
}
