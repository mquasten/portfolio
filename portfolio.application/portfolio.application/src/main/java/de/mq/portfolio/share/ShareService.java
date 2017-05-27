package de.mq.portfolio.share;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;




public interface ShareService{
	
	

	TimeCourse timeCourse(final Share share);

	void replaceTimeCourse(final TimeCourse timeCourse);

	Collection<Share> shares();

	void save(final Share share);

	Collection<TimeCourse> timeCourses(final Pageable pageable, final Share share);

	Pageable pageable(final Share share, final Sort sort, final Number size);

	Collection<String> indexes();

	Optional<TimeCourse> timeCourse(final String code);

	Collection<TimeCourse> realTimeCourses(Collection<String> codes, boolean useLastStoredTimeCourse);


	

}