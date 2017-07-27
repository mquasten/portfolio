package de.mq.portfolio.share.support;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.TimeCourse;

@Component
class TimeCourseDateRangeConverterImpl implements TimeCourseConverter {


	private final HistoryDateUtil historyDateUtil;
	

	TimeCourseDateRangeConverterImpl(HistoryDateUtil historyDateUtil) {
		this.historyDateUtil = historyDateUtil;
	}

	@Override
	public final TimeCourse convert(final TimeCourse source) {
		return new TimeCourseImpl(source.share(), beforeYesterday(source.rates()), beforeYesterday(source.dividends()));
	}

	private List<Data> beforeYesterday(final Collection<Data> data) {
		final Date yesterday = historyDateUtil.getOneDayBack(); 
		final Date oneYearBack = historyDateUtil.getOneYearBack();
		
		return data.stream().filter(date -> ( (! date.date().after(yesterday))     &&   (! date.date().before(oneYearBack))    )           ).collect(Collectors.toList());
	}
	
	

	@Override
	public final  TimeCourseConverterType timeCourseConverterType() {
		return TimeCourseConverter.TimeCourseConverterType.DateInRange;
	}

}
