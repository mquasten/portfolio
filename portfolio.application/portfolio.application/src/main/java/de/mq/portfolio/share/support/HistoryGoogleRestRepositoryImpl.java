package de.mq.portfolio.share.support;

import java.io.BufferedReader;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.web.util.UriTemplate;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.gateway.support.GatewayHistoryRepository;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.support.ExceptionTranslationBuilder;

@Repository()
@Profile("google")
abstract class HistoryGoogleRestRepositoryImpl implements HistoryRepository {
	private final String datePattern = "[0-9]{1,2}-[A-z]{3,3}-[0-9]{2,2}";
	
	private final DateFormat dateFormat;
	
	private final  GatewayHistoryRepository gatewayHistoryRepository;

	@Autowired
	HistoryGoogleRestRepositoryImpl(GatewayHistoryRepository gatewayHistoryRepository, final HistoryDateUtil historyDateUtil) {
		this.gatewayHistoryRepository = gatewayHistoryRepository;
		this.dateFormat = historyDateUtil.getGoogleDateFormat();
	}

	@Override
	public TimeCourse history(final GatewayParameterAggregation<Share> gatewayParameterAggregation) {
		Assert.notNull(gatewayParameterAggregation, "GatewayParameterAggregation is mandatory.");
		Assert.notNull(gatewayParameterAggregation.domain(), "Share is mandatory.");

		final ConfigurableConversionService configurableConversionService = configurableConversionService();

		configurableConversionService.addConverter(String.class, Date.class, dateString -> exceptionTranslationBuilder().withStatement(() -> dateFormat.parse(dateString)).translate());

		gatewayParameterAggregation.gatewayParameter(Gateway.GoogleRateHistory);

		final GatewayParameter gatewayParameter = gatewayParameterAggregation.gatewayParameter(Gateway.GoogleRateHistory);

		System.out.println(new UriTemplate(gatewayParameter.urlTemplate()).expand(gatewayParameter.parameters()));

		final String result = gatewayHistoryRepository.history(gatewayParameter).getBody();

		Assert.hasText(result, "ResponseBody is mandatory.");

		final List<Data> rates = Arrays.asList(result.split("[\n]")).stream().map(line -> line.split("[,]")).filter(cols -> cols.length >= 5).filter(cols -> cols[0].matches(datePattern)).map(cols -> toData(cols, configurableConversionService)).collect(Collectors.toList());
		rates.sort((d1, d2) -> Long.valueOf(d1.date().getTime() - d2.date().getTime()).intValue());

		return new TimeCourseImpl(gatewayParameterAggregation.domain(), rates, Arrays.asList());
	}

	private Data toData(final String[] cols, final ConfigurableConversionService configurableConversionService) {
		return new DataImpl(configurableConversionService.convert(cols[0], Date.class), configurableConversionService.convert(cols[4], Number.class).doubleValue());
	}

	@Override
	public Collection<Gateway> supports(final Share share) {
		return share.isIndex() ? Arrays.asList() : Arrays.asList(Gateway.GoogleRateHistory);
	}

	@Lookup
	abstract ConfigurableConversionService configurableConversionService();

	@Lookup
	abstract ExceptionTranslationBuilder<Date, BufferedReader> exceptionTranslationBuilder();

}
