package de.mq.portfolio.shareportfolio.support;

import de.mq.portfolio.share.TimeCourse;

public interface TimeCourseRetrospective {

	TimeCourse timeCourse();

	double end();

	double start();

	String name();

	double rate();

}