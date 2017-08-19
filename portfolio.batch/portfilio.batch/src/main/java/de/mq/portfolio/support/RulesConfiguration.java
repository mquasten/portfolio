package de.mq.portfolio.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.ResourceAccessException;

import de.mq.portfolio.batch.RulesEngine;
import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.exchangerate.support.ExchangeRatesCSVLineConverterImpl;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.ShareGatewayParameterService;
import de.mq.portfolio.gateway.support.GatewayParameterCSVLineConverterImpl;
import de.mq.portfolio.gateway.support.GatewayParameterRepository;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.support.SharesCSVLineConverterImpl;
import de.mq.portfolio.shareportfolio.support.SharePortfolioService;
import de.mq.portfolio.user.User;
import de.mq.portfolio.user.UserService;
import de.mq.portfolio.user.support.UsersCSVLineConverterImpl;

@Configuration
@ImportResource("classpath*:application.xml")
@Import({ AbstractJsonInputService.class })
class RulesConfiguration {

	static final String QUERY_PARAMETER_NAME = "query";
	static final String SHARE_NAME_PARAMETER_NAME = "shareName";
	static final String STOCK_EXCHANGE_ID_PARAMETER_NAME = "stockExchangeId";
	static final String SHARE_ID_PARAMETER_NAME = "shareId";
	static final String IMPORT_PORTFOLIOS_RULE_ENGINE_NAME = "importPortfolios";
	static final String SPEL_CONVERT_USER_ITEM = "user(#item)";
	static final String IMPORT_USERS_RULE_ENGINE_NAME = "importUsers";
	static final String IMPORT_GATEWAY_PARAMETER_RULE_ENGINE_NAME = "importGatewayParameters";

	static final String IMPORT_SHARES_RULE_ENGINE_NAME = "importShares";
	static final String IMPORT_TIME_COURSES_RULE_ENGINE_NAME = "importTimeCourses";
	static final String IMPORT_EXCHANGE_RATES_RULE_ENGINE_NAME = "importExchangeRates";
	static final String IMPORT_EXCHANGE_RATES_2_RULE_ENGINE_NAME = "importExchangeRates2";
	static final String SPEL_PROCESS_EXCHANGE_RATE_ITEM = "exchangeRate(#item)";
	static final String SPEL_REPLACE_TIME_COURSE_ITEM = "replaceTimeCourse(#item)";
	static final String SPEL_PROCESS_TIME_COURSE_ITEM = "timeCourse(#item)";
	static final String SPEL_INPUT_SHARES = "shares()";
	static final String SPEL_SAVE_ITEM = "save(#item)";
	static final String SPEL_READ_FILENAME = "read(#filename)";

	static final String SPEL_READ_GATEWAY_PARMETERS = "gatewayParameters(T(de.mq.portfolio.gateway.Gateway).CentralBankExchangeRates)";

	@Bean
	@Scope("prototype")
	RulesEngine importExchangeRates(final ExchangeRateService exchangeRateService, final RulesEngineBuilder rulesEngineBuilder, final ExceptionTranslationBuilder<Collection<ExchangeRate>, BufferedReader> exceptionTranslationBuilder) {

		return rulesEngineBuilder.withName(IMPORT_EXCHANGE_RATES_RULE_ENGINE_NAME).withRule(new ImportServiceRuleImpl<>(new SimpleCSVInputServiceImpl<>(new ExchangeRatesCSVLineConverterImpl(), exceptionTranslationBuilder), SPEL_READ_FILENAME))
				.withRule(new ProcessServiceRuleImpl<>(exchangeRateService, SPEL_PROCESS_EXCHANGE_RATE_ITEM)).withRule(new ProcessServiceRuleImpl<>(exchangeRateService, SPEL_SAVE_ITEM)).build();
	}

	@Bean
	@Scope("prototype")
	RulesEngine importExchangeRates2(final ExchangeRateService exchangeRateService, final GatewayParameterRepository gatewayParameterRepository, final RulesEngineBuilder rulesEngineBuilder) {
		return rulesEngineBuilder.withName(IMPORT_EXCHANGE_RATES_2_RULE_ENGINE_NAME).withRule(new ImportServiceRuleImpl<>(gatewayParameterRepository, SPEL_READ_GATEWAY_PARMETERS)).withRule(new ProcessServiceRuleImpl<>(new GatewayParameter2ExchangeRateConverterImpl(), "convert(#item)"))
				.withRule(new ProcessServiceRuleImpl<>(exchangeRateService, SPEL_PROCESS_EXCHANGE_RATE_ITEM)).withRule(new ProcessServiceRuleImpl<>(exchangeRateService, SPEL_SAVE_ITEM)).build();
	}

	@Bean
	@Scope("prototype")
	RulesEngine importTimeCourses(final ShareService shareService, final RulesEngineBuilder rulesEngineBuilder) {
		return rulesEngineBuilder.withName(IMPORT_TIME_COURSES_RULE_ENGINE_NAME).withRule(new ImportServiceRuleImpl<>(shareService, SPEL_INPUT_SHARES)).withRule(new ProcessServiceRuleImpl<>(shareService, SPEL_PROCESS_TIME_COURSE_ITEM))
				.withRule(new ProcessServiceRuleImpl<>(shareService, SPEL_REPLACE_TIME_COURSE_ITEM)).build();
	}

	@Bean
	@Scope("prototype")
	RulesEngine importShares(final ShareService shareService, final RulesEngineBuilder rulesEngineBuilder, final ExceptionTranslationBuilder<Collection<Share>, BufferedReader> exceptionTranslationBuilder) {
		return rulesEngineBuilder.withName(IMPORT_SHARES_RULE_ENGINE_NAME).withRule(new ImportServiceRuleImpl<>(new SimpleCSVInputServiceImpl<>(new SharesCSVLineConverterImpl(), exceptionTranslationBuilder), SPEL_READ_FILENAME)).withRule(new ProcessServiceRuleImpl<>(shareService, SPEL_SAVE_ITEM))
				.build();
	}

	@Bean
	@Scope("prototype")
	RulesEngine importUsers(final RulesEngineBuilder rulesEngineBuilder, final ExceptionTranslationBuilder<Collection<User>, BufferedReader> exceptionTranslationBuilder, final UserService userService) {
		return rulesEngineBuilder.withName(IMPORT_USERS_RULE_ENGINE_NAME).withRule(new ImportServiceRuleImpl<>(new SimpleCSVInputServiceImpl<>(new UsersCSVLineConverterImpl(), exceptionTranslationBuilder), SPEL_READ_FILENAME))
				.withRule(new ProcessServiceRuleImpl<>(userService, SPEL_CONVERT_USER_ITEM)).withRule(new ProcessServiceRuleImpl<>(userService, SPEL_SAVE_ITEM)).build();
	}

	@Bean
	@Scope("prototype")
	RulesEngine importGatewayParameters(final ShareGatewayParameterService shareGatewayParameterService, final RulesEngineBuilder rulesEngineBuilder, final ExceptionTranslationBuilder<Collection<GatewayParameter>, BufferedReader> exceptionTranslationBuilder) {
		return rulesEngineBuilder.withName(IMPORT_GATEWAY_PARAMETER_RULE_ENGINE_NAME).withRule(new ImportServiceRuleImpl<>(new SimpleCSVInputServiceImpl<>(new GatewayParameterCSVLineConverterImpl(), exceptionTranslationBuilder), SPEL_READ_FILENAME))
				.withRule(new ProcessServiceRuleImpl<>(shareGatewayParameterService, SPEL_SAVE_ITEM)).build();
	}

	@Bean
	@Scope("prototype")
	RulesEngine importPortfolios(final RulesEngineBuilder rulesEngineBuilder, final AbstractJsonInputService importService, final SharePortfolioService sharePortfolioService) {
		return rulesEngineBuilder.withName(IMPORT_PORTFOLIOS_RULE_ENGINE_NAME).withRule(new ImportServiceRuleImpl<>(importService, SPEL_READ_FILENAME)).withRule(new ProcessServiceRuleImpl<>(sharePortfolioService, SPEL_SAVE_ITEM)).build();
	}

	@Bean
	@Scope("prototype")
	RulesEngineBuilder rulesEngineBuilder() {
		return new SimpleRuleEngineBuilderImpl();
	}

	@Bean
	@Scope("singleton")
	BatchProcessorImpl batchProcessor(Collection<RulesEngine> rulesEngines) {
		return new BatchProcessorImpl(rulesEngines);

	}

	@Bean
	@Scope("singleton")
	ApplicationContextAware commandlineProcessor() {
		return new SimpleCommandlineProcessorImpl(BatchProcessorImpl.class);
	}

	@Bean
	@Scope("prototype")
	ExceptionTranslationBuilder<?, ?> exceptionTranslationBuilder() {
		return new ExceptionTranslationBuilderImpl<>().withTranslation(ResourceAccessException.class, Arrays.asList(IOException.class));
	}

}
