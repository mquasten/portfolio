package de.mq.portfolio.share.support;

import java.util.Collection;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

interface ShareRepository {

	Collection<Share> shares();

	void save(final TimeCourse timeCourse);

	void deleteTimeCourse(final Share share);

	void save(final Share share);

	Collection<TimeCourse> timeCourses(final Pageable pageable, final Share criteria);

	Pageable pageable(final Share criteria, final Sort sort, final Number pageSize);

	Collection<String> distinctIndex();

	

}