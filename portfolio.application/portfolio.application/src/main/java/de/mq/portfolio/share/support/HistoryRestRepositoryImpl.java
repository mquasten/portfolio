package de.mq.portfolio.share.support;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

@Repository
class HistoryRestRepositoryImpl implements HistoryRepository {

	private final RestOperations restOperations;

	final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	private final String url = "http://real-chart.finance.yahoo.com/table.csv?s=%s&a=%s&b=%s&c=%s";

	private final int periodeInDays = 365;

	@Autowired
	HistoryRestRepositoryImpl(final RestOperations restOperations) {
		this.restOperations = restOperations;
	}

	@Override
	public final TimeCourse history(final Share share) {

		final GregorianCalendar cal = new GregorianCalendar();
		cal.add(Calendar.DATE, -periodeInDays);
		final int month = cal.get(Calendar.MONTH);
		final int day = cal.get(Calendar.DAY_OF_MONTH);
		final int year = cal.get(Calendar.YEAR);

		final String requestUrl = String.format(url, share.code(), month, day, year);

		final Collection<Data> rates = getValues(requestUrl, 4);

		final Collection<Data> dividends = getValues(requestUrl + "&g=v", 1);

		return new TimeCourseImpl(share, rates, dividends);
	}

	private Collection<Data> getValues(final String requestUrl, final int colIndex) {
		final List<Data> results = Arrays.asList(restOperations.getForObject(requestUrl, String.class).split("\n")).stream().map(line -> line.split(",")).filter(cols -> cols.length >= colIndex + 1).filter(cols -> isDate(df, cols[0]))
				.map(cols -> new DataImpl(cols[0], Double.parseDouble(cols[colIndex]))).collect(Collectors.toList());
		results.sort((d1, d2) -> Long.valueOf(d1.date().getTime() - d2.date().getTime()).intValue());
		return results;
	}

	private boolean isDate(final SimpleDateFormat df, final String key) {
		try {

			df.parse(key);
			return true;
		} catch (final ParseException ex) {
			return false;
		}
	}

}
