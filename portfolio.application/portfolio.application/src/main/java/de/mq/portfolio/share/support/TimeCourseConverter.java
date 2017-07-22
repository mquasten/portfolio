package de.mq.portfolio.share.support;



import org.springframework.core.convert.converter.Converter;


import de.mq.portfolio.share.TimeCourse;

interface TimeCourseConverter  extends Converter<TimeCourse, TimeCourse> {
	
	enum TimeCourseConverterType {
		DateInRange,
		DividendEuroUSD;
	}
	
	
	
	
	TimeCourseConverterType timeCourseConverterType();
	
}
