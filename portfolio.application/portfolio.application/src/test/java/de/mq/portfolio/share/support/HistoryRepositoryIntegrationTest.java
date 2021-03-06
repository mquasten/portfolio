package de.mq.portfolio.share.support;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/mongo-test.xml", "/application-test.xml" })
@Ignore
public class HistoryRepositoryIntegrationTest {

	@Autowired
	@Qualifier("googleHistoryRepository")
	private HistoryRepository historyGoogleRestRepository;

	@Autowired
	@Qualifier("arivaHistoryRepository")
	private HistoryRepository historyArivaRestRepository;

	@Value("#{stocks}")
	private Map<String, String> stocks;

	@Value("#{arivaHistory}")
	private List<GatewayParameter> arivaHistory;

	private final Map<String, GatewayParameter> arivaParameter = new HashMap<>();

	@Value("#{wkns}")
	private Map<String, String> wkns;

	private final Share share = Mockito.mock(Share.class);

	final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

	private final Map<String, Double> maxDeviationDow = new HashMap<>();

	private final Map<String, Double> maxDeviationDax = new HashMap<>();
	
	
	private final HistoryDateUtil historyDateUtil= new HistoryDateUtil();

	@Before
	public final void setup() {
		arivaParameter.putAll(arivaHistory.stream().collect(Collectors.toMap(history -> history.code(), history -> history)));

		maxDeviationDow.put("GE", 17d);
		maxDeviationDow.put("GS", 176d);
		maxDeviationDow.put("IBM", 37d);
		maxDeviationDow.put("JPM", 60d);
		maxDeviationDow.put("MSFT", 51d);
		maxDeviationDow.put("PG", 47d);
		maxDeviationDow.put("V", 22d);
		maxDeviationDow.put("WMT", 18d);
		maxDeviationDow.put("WMT", 30d);
		maxDeviationDow.put("AAPL", 6d);
		maxDeviationDow.put("AXP", 22d);
		maxDeviationDow.put("CAT", 30d);
		maxDeviationDow.put("CVX", 44d);
		maxDeviationDow.put("DD", 32d);
		maxDeviationDow.put("HD", 36d);
		maxDeviationDow.put("JNJ", 30d);
		maxDeviationDow.put("KO", 13d);
		maxDeviationDow.put("MMM", 25d);
		maxDeviationDow.put("MRK", 33d);
		maxDeviationDow.put("PFE", 8d);
		maxDeviationDow.put("TRV", 35d);
		maxDeviationDow.put("UNH", 132d);
		maxDeviationDow.put("UTX", 74d);
		maxDeviationDow.put("VZ", 12d);
		maxDeviationDow.put("XOM", 34d);

		maxDeviationDow.put("MCD", 34d);
		maxDeviationDow.put("DIS", 65d);
		maxDeviationDow.put("INTC", 2d);
		maxDeviationDow.put("NKE", 22d);

		// maxDeviationDow.put("KO", 4d);
		maxDeviationDow.put("BA", 70d);
		maxDeviationDax.put("DBK.DE", 250d);

		ReflectionTestUtils.setField(historyArivaRestRepository, "imports", Arrays.asList(HistoryArivaRestRepositoryImpl.Imports.Rates));
	}

	int counter = 0;

	@Test
	@Ignore
	public void historySap() throws ParseException {

		final List<GatewayParameterAggregation<Share>> gatewayParameterAggregations = new ArrayList<>();

		int max = 4;
		final List<String> params = Arrays.asList("ETR:SAP", "FRA:SAP", "NYSE:SAP");
		IntStream.range(0, max - 1).forEach(i -> gatewayParameterAggregations.add(gatewayParameterAggregationGoogle(params.get(i))));

		@SuppressWarnings("unchecked")
		final List<Data>[] results = new List[max];

		// Mockito.when(share.id2()).thenReturn("910");

		Mockito.doReturn(wkns.get("SAP.DE")).when(share).wkn();

		results[0] = historyGoogleRestRepository.history(gatewayParameterAggregations.get(0)).rates();

		results[1] = historyGoogleRestRepository.history(gatewayParameterAggregations.get(1)).rates();

		results[2] = historyGoogleRestRepository.history(gatewayParameterAggregations.get(2)).rates();

		Mockito.when(share.index()).thenReturn(null);

		Mockito.when(share.code()).thenReturn("SAP.DE");
		results[3] = historyArivaRestRepository.history(gatewayParameterAggregationAriva("SAP.DE")).rates();

		final Map<Date, Double[]> prices = new HashMap<>();
		IntStream.range(0, max).forEach(i -> {
			results[i].forEach(data -> {
				prices.put(data.date(), new Double[max]);
			});

		});

		IntStream.range(0, max).forEach(i -> {
			results[i].forEach(data -> prices.get(data.date())[i] = data.value());

		});

		final List<Date> dates = new ArrayList<>(prices.keySet());
		Collections.sort(dates, (d1, d2) -> (int) Math.round(d1.getTime() - d2.getTime()));

		int missing[] = new int[] { 0 };
		dates.forEach(date -> {
			final Double values[] = prices.get(date);
			// System.out.println(df.format(date) +";" + values[0] +";"
			// +values[1] +";" +values[2] +";" +values[3]);

			if (Arrays.asList(values).stream().filter(value -> value == null).count() > 0) {
				missing[0]++;
			}

			if ((values[0] != null) && (values[3] != null)) {

				if (Math.abs(values[3] - values[0]) > 0d) {
					System.out.println(date + ":" + values[3] + "<=>" + values[0]);
				}
				Assert.assertEquals(values[3], values[0]);
			}

			if ((values[3] != null) && (values[1] != null)) {
				Assert.assertTrue(100 * Math.abs(values[1] - values[3]) / values[3] < 2d);
			}

			if ((values[3] != null) && (values[2] != null)) {
				double error = 100 * Math.abs(values[2] - values[3]) / values[3];

				Assert.assertTrue(error > 2.5d && error < 20);
			}

		});

		Assert.assertTrue(missing[0] < 20);

	}

	private GatewayParameterAggregation<Share> gatewayParameterAggregationAriva(final String code) {
		@SuppressWarnings("unchecked")
		final GatewayParameterAggregation<Share> gatewayParameterAggregationAriva = Mockito.mock(GatewayParameterAggregation.class);

		Mockito.when(gatewayParameterAggregationAriva.gatewayParameter(Gateway.ArivaRateHistory)).thenReturn(arivaParameter.get(code));
		Mockito.when(gatewayParameterAggregationAriva.domain()).thenReturn(share);
		return gatewayParameterAggregationAriva;
	}

	private GatewayParameterAggregation<Share> gatewayParameterAggregationGoogle(final String query) {
		@SuppressWarnings("unchecked")
		final GatewayParameterAggregation<Share> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);
		final Map<String, String> parameters = new HashMap<>();
		parameters.put("query", query);
		parameters.put("startdate" , historyDateUtil.oneYearBack(historyDateUtil.getGoogleDateFormat()));
	
		final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
		Mockito.when(gatewayParameter.urlTemplate()).thenReturn("http://www.google.com/finance/historical?q={query}&startdate={startdate}&output=csv");
		Mockito.when(gatewayParameter.parameters()).thenReturn(parameters);
		Mockito.when(gatewayParameterAggregation.gatewayParameter(Gateway.GoogleRateHistory)).thenReturn(gatewayParameter);
		Mockito.when(gatewayParameterAggregation.domain()).thenReturn(share);
		return gatewayParameterAggregation;
	}

	@Test
	@Ignore
	public void historyKO() {

		Mockito.when(share.code()).thenReturn("KO");
		Mockito.when(share.index()).thenReturn("Dow Jones");
		int max = 2;
		@SuppressWarnings("unchecked")
		final List<Data>[] results = new List[max];

		Mockito.doReturn(wkns.get("KO")).when(share).wkn();
		results[0] = historyGoogleRestRepository.history(gatewayParameterAggregationGoogle("NYSE:KO")).rates();
		results[1] = historyArivaRestRepository.history(gatewayParameterAggregationAriva("KO")).rates();

		final Map<Date, Double[]> prices = new HashMap<>();
		IntStream.range(0, max).forEach(i -> {
			results[i].forEach(data -> {
				prices.put(data.date(), new Double[max]);
			});

		});

		IntStream.range(0, max).forEach(i -> {
			results[i].forEach(data -> prices.get(data.date())[i] = data.value());

		});

		final List<Date> dates = new ArrayList<>(prices.keySet());
		Collections.sort(dates, (d1, d2) -> (int) Math.round(d1.getTime() - d2.getTime()));
		counter = 0;
		final int missing[] = new int[] { 0 };
		dates.forEach(date -> {
			final Double values[] = prices.get(date);
			// System.out.println(df.format(date) +";" + values[0] +";"
			// +values[1] );
			if (Arrays.asList(values).stream().filter(value -> value == null).count() > 0) {
				missing[0]++;
			}

			if (values[0] != null && values[1] != null) {

				Assert.assertTrue(Math.abs(100 * values[1] - 100 * values[0]) <= 13);

			}

		});

		// Assert.assertTrue(counter <= 2);
		Assert.assertTrue(missing[0] == 0);
	}

	@Test
	@Ignore
	public void historyEONA() {

		Mockito.when(share.code()).thenReturn("EOAN.DE");
		// Mockito.when(share.id2()).thenReturn("320");

		Mockito.when(share.wkn()).thenReturn("ENAG99");

		int max = 2;
		@SuppressWarnings("unchecked")
		final List<Data>[] results = new List[max];

		results[0] = historyGoogleRestRepository.history(gatewayParameterAggregationGoogle("ETR:EOAN")).rates();
		results[1] = historyArivaRestRepository.history(gatewayParameterAggregationAriva("EOAN.DE")).rates();

		final Map<Date, Double[]> prices = new HashMap<>();
		IntStream.range(0, max).forEach(i -> {
			results[i].forEach(data -> {
				prices.put(data.date(), new Double[max]);
			});

		});

		IntStream.range(0, max).forEach(i -> {
			results[i].forEach(data -> prices.get(data.date())[i] = data.value());

		});

		final List<Date> dates = new ArrayList<>(prices.keySet());
		Collections.sort(dates, (d1, d2) -> (int) Math.round(d1.getTime() - d2.getTime()));

		final int missing[] = new int[] { 0 };
		final int counter[] = new int[] { 0 };
		dates.stream().filter(date -> !date.before(new GregorianCalendar(2016, 8, 12).getTime())).forEach(date -> {
			final Double values[] = prices.get(date);
			// System.out.println(df.format(date) +";" + values[0] +";"
			// +values[1] );

			counter[0]++;

			if (values[0] != null && values[1] != null) {
				Assert.assertTrue(Math.abs(Math.round(100d * values[0]) - Math.round(100d * values[1])) <= 1d);

			} else {
				missing[0]++;
			}

		});

		Assert.assertTrue(counter[0] > 190);
		Assert.assertTrue(missing[0] == 0);

	}

	@Test
	@Ignore
	public void historyDB11() {

		Mockito.when(share.code()).thenReturn("DB1.DE");

		// Mockito.when(share.id2()).thenReturn("4587");

		Mockito.doReturn(wkns.get("DB1.DE")).when(share).wkn();

		int max = 2;
		@SuppressWarnings("unchecked")
		final List<Data>[] results = new List[max];

		results[0] = historyGoogleRestRepository.history(gatewayParameterAggregationGoogle("ETR:DB1")).rates();
		results[1] = historyArivaRestRepository.history(gatewayParameterAggregationAriva("DB1.DE")).rates().stream().filter(rate -> !rate.date().before(new GregorianCalendar(2016, 8, 5).getTime())).collect(Collectors.toList());

		final Map<Date, Double[]> prices = new HashMap<>();
		IntStream.range(0, max).forEach(i -> {
			results[i].forEach(data -> {
				prices.put(data.date(), new Double[max]);
			});

		});

		IntStream.range(0, max).forEach(i -> {
			results[i].forEach(data -> prices.get(data.date())[i] = data.value());

		});

		final List<Date> dates = new ArrayList<>(prices.keySet());
		Collections.sort(dates, (d1, d2) -> (int) Math.round(d1.getTime() - d2.getTime()));

		final int missing[] = new int[] { 0 };
		final int counter[] = new int[] { 0 };
		dates.stream().filter(date -> !date.before(new GregorianCalendar(2016, 8, 12).getTime())).forEach(date -> {
			final Double values[] = prices.get(date);
			// System.out.println(df.format(date) +";" + values[0] +";"
			// +values[1] );

			counter[0]++;

			if (values[0] != null && values[1] != null) {
				Assert.assertTrue(100 * Math.abs(values[0] - values[1]) <= 1d);

			} else {
				missing[0]++;
			}

		});

		Assert.assertTrue(counter[0] > 190);
		Assert.assertTrue(missing[0] == 0);
	}

	@Test
	@Ignore
	public final void allDow() {
		final List<GatewayParameter> dowList = arivaHistory.stream().filter(history -> !history.code().toUpperCase().endsWith(".DE")).collect(Collectors.toList());
		compareShares(dowList, maxDeviationDow);

	}

	@Test
	@Ignore
	public final void allDax() {
		final List<GatewayParameter> daxList = arivaHistory.stream().filter(history -> history.code().toUpperCase().endsWith(".DE") && !(history.code().equals("DB1.DE") || history.code().equals("DBK.DE"))).collect(Collectors.toList());
		compareShares(daxList, maxDeviationDow);
	}

	@Test
	@Ignore
	public final void dax() {
		singleShare("SAP.DE");
		// singleShare("JNJ");
	}

	private final void singleShare(final String code) {
		final List<GatewayParameter> singleShareList = arivaHistory.stream().filter(history -> history.code().equals(code)).collect(Collectors.toList());
		compareShares(singleShareList, maxDeviationDax);
	}

	private final void compareShares(final List<GatewayParameter> shareGatewayParameters, final Map<String, Double> maxDeviation) {

		shareGatewayParameters.stream().forEach(history -> {

			System.out.println("***" + history.code() + "***");

			final Map<Date, double[]> results = new HashMap<>();

			Mockito.when(share.code()).thenReturn(history.code());
			Mockito.when(share.wkn()).thenReturn(wkns.get(history.code()));

			historyGoogleRestRepository.history(gatewayParameterAggregationGoogle(stocks.get(history.code()))).rates().forEach(rate -> addResult(results, rate, 0));
			historyArivaRestRepository.history(gatewayParameterAggregationAriva(history.code())).rates().forEach(rate -> addResult(results, rate, 1));

			Assert.assertTrue(results.size() > 250);

			final Set<double[]> resultsWithBoth = results.values().stream().filter(values -> values[0] != 0d && values[1] != 0d).collect(Collectors.toSet());

			System.out.println(results.size() + ":" + resultsWithBoth.size());
			Assert.assertTrue(results.size() - resultsWithBoth.size() < 1);

			counter = 0;
			resultsWithBoth.forEach(values -> {

				// System.out.println(history.code() + ":" +values[0] + ":" +
				// values[1]);

				if (maxDeviation.containsKey(history.code())) {

					// System.out.println(history.code() + ":" +
					// Math.abs(values[0] - values[1]) + ":"+
					// maxDeviation.get(history.code()) );
					Assert.assertTrue(100d * Math.abs(values[0] - values[1]) < maxDeviation.get(history.code()));
				} else {

					// System.out.println(history.code() + ":" +values[0] + ":"
					// + values[1]);
					Assert.assertTrue(Math.abs(100d * values[0] - 100d * values[1]) < 1);
				}
				if (Math.abs(100d * values[0] - 100d * values[1]) > 1d) {

					System.out.println(history.code() + ":" + values[0] + ":" + values[1]);
					counter++;
				}
			});

			// System.out.println(counter);
			Assert.assertTrue(counter <= 11);

		});

	}

	private void addResult(final Map<Date, double[]> results, final Data rate, final int index) {
		if (!results.containsKey(rate.date())) {
			results.put(rate.date(), new double[2]);
		}
		results.get(rate.date())[index] = rate.value();
	}

	@Test
	@Ignore
	public final void arivaCSV() {

		final String parameterFormat = "{shareId:'%s', stockExchangeId:'%s', startDate:oneYearBack(germanYearToDayDateFormat), endDate:oneDayBack(germanYearToDayDateFormat), delimiter:'|'}";

		Collection<String> urlTempaltes = new HashSet<>();
		arivaHistory.stream().forEach(history -> {

			System.out.println(history.code() + ";" + history.gateway() + ";" + history.urlTemplate() + ";" + String.format(parameterFormat, history.parameters().get("shareId"), history.parameters().get("stockExchangeId")));
			urlTempaltes.add(history.urlTemplate());
		});

		String url = urlTempaltes.stream().findAny().get();
		System.out.println("^GDAXI" + ";" + Gateway.ArivaRateHistory + ";" + url + ";" + String.format(parameterFormat, "290", "12"));
		System.out.println("^DJI" + ";" + Gateway.ArivaRateHistory + ";" + url + ";" + String.format(parameterFormat, "4325", "71"));

	}

	@Test
	@Ignore
	public final void googleCSV() {
		stocks.entrySet().stream().sorted((c1, c2) -> compare(c1, c2)).forEach(entry -> {
			System.out.println(entry.getKey() + ";" + Gateway.GoogleRateHistory.name() + ";" + "http://www.google.com/finance/historical?q={query}&startdate={startDate}&output=csv" + ";{query:'" + entry.getValue() + "', startDate:oneYearBack(googleDateFormat)}");
		});

		// http://www.google.com/finance/historical?q={query}&output=csv
	}

	private int compare(Entry<String, String> c1, Entry<String, String> c2) {
		int index1 = c1.getKey().endsWith("DE") ? 1 : 0;
		int index2 = c2.getKey().endsWith("DE") ? 1 : 0;

		if (Math.signum(index1 - index2) != 0d) {
			return ((Float) Math.signum(index1 - index2)).intValue();
		}
		return c1.getKey().compareTo(c2.getKey());
	}

}
