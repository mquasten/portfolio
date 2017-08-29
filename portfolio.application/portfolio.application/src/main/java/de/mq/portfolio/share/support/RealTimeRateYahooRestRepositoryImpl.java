package de.mq.portfolio.share.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Repository;

import org.springframework.web.client.RestOperations;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.support.ExceptionTranslationBuilder;

@Repository
public abstract class RealTimeRateYahooRestRepositoryImpl implements RealTimeRateRepository {

	private final RestOperations restOperations;

	@Autowired
	RealTimeRateYahooRestRepositoryImpl(final RestOperations restOperations) {
		this.restOperations = restOperations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.share.support.RealTimeRateRestRepository#rates(java.util.
	 * Collection)
	 */
	@Override
	public Collection<TimeCourse> rates(final GatewayParameterAggregation<Collection<Share>> gatewayParameterAggregation) {
		final Map<String, Share> sharesMap = new HashMap<>();
		gatewayParameterAggregation.domain().forEach(share -> sharesMap.put(share.code(), share));
		final GatewayParameter gatewayParameter = gatewayParameterAggregation.gatewayParameter(Gateway.YahooRealtimeRate);
		return exceptionTranslationBuilder().withResource(() -> new BufferedReader(new StringReader(restOperations.getForObject(gatewayParameter.urlTemplate(), String.class, gatewayParameter.parameters())))).withTranslation(IllegalStateException.class, Arrays.asList(IOException.class))
				.withStatement(bufferedReader -> {
					return toTimeCourses(sharesMap, bufferedReader);
				}).translate();
	}

	private Collection<TimeCourse> toTimeCourses(final Map<String, Share> sharesMap, BufferedReader bufferedReader) throws IOException {
		final List<TimeCourse> results = new ArrayList<>();
		for (String line = ""; line != null; line = bufferedReader.readLine()) {
			line = line.replaceAll("[\"]", "");
			final String[] cols = line.split("[,;]");
			if (cols.length < 7) {
				continue;
			}

			if (!validDoubles(cols[5], cols[6])) {
				continue;
			}

			final TimeCourseImpl timeCourse = new TimeCourseImpl(sharesMap.get(cols[0]), Arrays.asList(new DataImpl(dateForDaysBefore(1), Double.valueOf(cols[5])), new DataImpl(dateForDaysBefore(0), Double.valueOf(cols[6]))), Arrays.asList());
			timeCourse.onBeforeSave();
			results.add(timeCourse);
		}
		return Collections.unmodifiableList(results);
	}

	private boolean validDoubles(String... values) {

		for (final String value : values) {
			try {
				Double.parseDouble(value);
			} catch (final NumberFormatException nf) {
				return false;
			}
		}

		return true;

	}

	private Date dateForDaysBefore(final int daysBack) {
		return Date.from(LocalDateTime.now().plusDays(-daysBack).truncatedTo(ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).toInstant());
	}

	@Override
	public Gateway supports(Collection<Share> shares) {
		return Gateway.YahooRealtimeRate;
	}

	@Lookup
	abstract ExceptionTranslationBuilder<Collection<TimeCourse>, BufferedReader> exceptionTranslationBuilder();

}
