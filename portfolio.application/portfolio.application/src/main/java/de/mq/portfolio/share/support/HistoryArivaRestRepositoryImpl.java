package de.mq.portfolio.share.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriTemplate;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.support.ExceptionTranslationBuilder;

@Repository()
@Profile("ariva")
abstract class HistoryArivaRestRepositoryImpl implements HistoryRepository {

	enum Imports {
		Rates, Dividends;
	}

	private final String delimiter = "|";
	private final DateFormat dateFormatRates = new SimpleDateFormat("yyyy-MM-dd");
	private final DateFormat dateFormatDividends = new SimpleDateFormat("dd.MM.yy");
	private final RestOperations restOperations;

	private final boolean wknCheck; 
	private final Collection<Imports> imports = new ArrayList<>();



	

	@Autowired
	HistoryArivaRestRepositoryImpl(final RestOperations restOperations, @Value("${history.ariva.wkncheck}") final boolean wknCheck, @Value("${history.ariva.imports?:Rates,Dividends}") final String imports) {
		
		this.restOperations = restOperations;

		

		this.wknCheck = wknCheck;
		this.imports.addAll(Arrays.asList(imports.split("[,]")).stream().map(value -> Imports.valueOf(StringUtils.capitalize(StringUtils.trimWhitespace(value).toLowerCase()))).collect(Collectors.toList()));

	}
	

	@Override
	public TimeCourse history(GatewayParameterAggregation<Share> gatewayParameterAggregation) {

		Assert.notNull(gatewayParameterAggregation.domain(), "Share is mandatory.");
		
		final Collection<Data> rates = new ArrayList<>();
		final Collection<Data> dividends = new ArrayList<>();
		final Map<Imports, Consumer<Share>> importsMap = new HashMap<>();
		importsMap.put(Imports.Rates, aShare -> rates.addAll(importRates(gatewayParameterAggregation)));

		importsMap.put(Imports.Dividends, aShare -> dividends.addAll(importDividends(gatewayParameterAggregation)));

		imports.forEach(value -> importsMap.get(value).accept(gatewayParameterAggregation.domain()));

		return new TimeCourseImpl(gatewayParameterAggregation.domain(), rates, dividends);

	}

	private List<Data> importDividends(final GatewayParameterAggregation<Share> gatewayParameterAggregation) {

		if (gatewayParameterAggregation.domain().isIndex()) {
			return Arrays.asList();
		}

		
		final GatewayParameter gatewayParameter = gatewayParameterAggregation.gatewayParameter(Gateway.ArivaDividendHistory);
				
		System.out.println(gatewayParameter);

		System.out.println(new UriTemplate(gatewayParameter.urlTemplate()).expand(gatewayParameter.parameters()));
		final String html = restOperations.getForObject(gatewayParameter.urlTemplate(), String.class, gatewayParameter.parameters());

		final Document doc = Jsoup.parse(html);

		final List<Data> dividends = new ArrayList<>();
		final List<Element> results = doc.getElementsByTag("tr").stream().filter(line -> line.getElementsMatchingText("Dividende").size() > 0).collect(Collectors.toList());

		final ConfigurableConversionService configurableConversionService = preparedConversionService(dateFormatDividends);

	

		

		for (final Element result : results) {
			final List<Element> tds = result.getElementsByTag("td");

			final Date date = configurableConversionService.convert(tds.get(0).text(), Date.class);
			
			
			
			final String cols[] = tds.get(3).text().split("[ ]");

			final Double rate = configurableConversionService.convert(cols[0], Number.class).doubleValue() ;

			Assert.isTrue(tds.get(1).text().equals("Dividende"));

			dividends.add(new DataImpl(date, Math.round(100 * rate) / 100d));

		}

		Collections.sort(dividends, (data1, data2) -> Double.valueOf(Math.signum(Long.valueOf(data1.date().getTime() - data2.date().getTime()).doubleValue())).intValue());

		return dividends;
	}

	private List<Data> importRates(final GatewayParameterAggregation<Share> gatewayParameterAggregation) {
		final LocalDate date = LocalDate.now();
		final Map<String, Object> params = new HashMap<>();
		final GatewayParameter gatewayParameter = gatewayParameterAggregation.gatewayParameter(Gateway.ArivaRateHistory);
		

		params.putAll(gatewayParameter.parameters());

		params.put("startDate", dateString(date, HistoryRepository.OFFSET_DAYS_ONE_YEAR_BACK));
		params.put("endDate", dateString(date, HistoryRepository.OFFSET_DAYS_ONE_DAY_BACK));
		params.put("delimiter", delimiter);

		System.out.println(new UriTemplate(gatewayParameter.urlTemplate()).expand(params));
		final ResponseEntity<String> responseEntity = restOperations.getForEntity(gatewayParameter.urlTemplate(), String.class, params);

		attachementHeaderWknGuard(gatewayParameterAggregation.domain(), responseEntity.getHeaders());
		return exceptionTranslationBuilderResult().withResource(() -> {
			return new BufferedReader(new StringReader(responseEntity.getBody()));

		}).withStatement(bufferedReader -> {
			return read(bufferedReader, gatewayParameterAggregation.domain().isIndex());
		}).translate();
	}

	void attachementHeaderWknGuard(final Share share, final HttpHeaders httpHeaders) {

		if (!wknCheck) {
			return;
		}

		Assert.hasText(share.wkn(), "WKN is mandatory in share if wknCheck is used.");

		final Map<String, String> headers = httpHeaders.toSingleValueMap();

		final String attachement = headers.get("Content-Disposition");
		Assert.hasText(attachement, "Content-Disposition should not  empty");
		final String[] cols = attachement.split("[_]");
		Assert.isTrue(cols.length == 3, " Wrong Content-Disposition Header");

		Assert.hasText(cols[1], "WKN not found in Content-Disposition Header");

		Assert.isTrue(StringUtils.trimWhitespace(share.wkn()).equals(StringUtils.trimWhitespace(cols[1])), "WKN didn't match with attachement.");
	}

	private List<Data> read(final BufferedReader bufferedReader, final boolean isIndex) throws IOException, ParseException {

		final ConfigurableConversionService configurableConversionService = preparedConversionService(dateFormatRates);
		final List<Data> results = new ArrayList<>();
		for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
			// System.out.println(line);

			if (line.startsWith("Datum")) {

				continue;
			}

			final String[] cols = line.split(String.format("[%s]", delimiter));

			if (isIndex ? cols.length < 5 : cols.length != 7) {
				continue;
			}
			// System.out.println(line);

			results.add(new DataImpl(configurableConversionService.convert(cols[0], Date.class), configurableConversionService.convert(cols[4], Number.class).doubleValue()));

		}
		Collections.sort(results, (data1, data2) -> Double.valueOf(Math.signum(Long.valueOf(data1.date().getTime() - data2.date().getTime()).doubleValue())).intValue());
		return results;

	}

	private ConfigurableConversionService preparedConversionService(final DateFormat dateFormat) {
		final ConfigurableConversionService configurableConversionService = configurableConversionService();
		final DecimalFormat numberFormat = new DecimalFormat();
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator(',');
		otherSymbols.setGroupingSeparator('.');
		numberFormat.setDecimalFormatSymbols(otherSymbols);
		configurableConversionService.addConverter(String.class, Date.class, dateString -> exceptionTranslationBuilderConversionServiceDate().withStatement(() -> dateFormat.parse(dateString)).translate());

		configurableConversionService.addConverter(String.class, Number.class, doubleString -> exceptionTranslationBuilderConversionServiceDouble().withStatement(() -> numberFormat.parse(doubleString)).translate());
		return configurableConversionService;
	}

	private String dateString(final LocalDate date, final long daysBack) {
		return dateFormatRates.format(date(date, daysBack));
	}

	Date date(final LocalDate date, final long daysBack) {
		return Date.from(date.minusDays(daysBack).atStartOfDay(ZoneId.systemDefault()).toInstant());
	} 
	
	
	@Override
	public Collection<Gateway> supports(final Share share) {
		return share.isIndex() ?  Arrays.asList(Gateway.ArivaRateHistory) : Arrays.asList(Gateway.ArivaRateHistory, Gateway.ArivaDividendHistory);
	}

	@SuppressWarnings("unchecked")
	private ExceptionTranslationBuilder<Date, BufferedReader> exceptionTranslationBuilderConversionServiceDate() {
		return (ExceptionTranslationBuilder<Date, BufferedReader>) exceptionTranslationBuilder();
	}

	@SuppressWarnings("unchecked")
	private ExceptionTranslationBuilder<Number, BufferedReader> exceptionTranslationBuilderConversionServiceDouble() {
		return (ExceptionTranslationBuilder<Number, BufferedReader>) exceptionTranslationBuilder();
	}

	@SuppressWarnings("unchecked")
	private ExceptionTranslationBuilder<List<Data>, BufferedReader> exceptionTranslationBuilderResult() {
		return (ExceptionTranslationBuilder<List<Data>, BufferedReader>) exceptionTranslationBuilder();
	}

	@Lookup
	abstract ExceptionTranslationBuilder<?, BufferedReader> exceptionTranslationBuilder();

	@Lookup
	abstract ConfigurableConversionService configurableConversionService();

	

}
