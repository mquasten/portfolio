package de.mq.portfolio.share.support;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.TimeCourse;

@Component
class TimeCourseDateRangeConverterImpl implements TimeCourseConverter {



	@Override
	public final TimeCourse convert(final TimeCourse source) {
		return new TimeCourseImpl(source.share(), beforeYesterday(source.rates()), beforeYesterday(source.dividends()));
	}

	private List<Data> beforeYesterday(final Collection<Data> data) {
		final Date yesterday = dateDaysBack(HistoryRepository.OFFSET_DAYS_ONE_DAY_BACK);
		final Date oneYearBack = dateDaysBack(HistoryRepository.OFFSET_DAYS_ONE_YEAR_BACK);
		return data.stream().filter(date -> ( (! date.date().after(yesterday))     &&   (! date.date().before(oneYearBack))    )           ).collect(Collectors.toList());
	}
	
	
	
	private Date dateDaysBack(final long daysBack) {
		return Date.from(LocalDate.now().minusDays(daysBack).atStartOfDay(ZoneId.systemDefault()).toInstant());
	} 

	@Override
	public final  TimeCourseConverterType timeCourseConverterType() {
		return TimeCourseConverter.TimeCourseConverterType.DateInRange;
	}

}
