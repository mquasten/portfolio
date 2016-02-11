package de.mq.portfolio.share.support;

import java.util.Collection;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

interface ShareRepository {

	Collection<Share> shares();

	void save(final TimeCourse timeCourse);

	void deleteTimeCourse(final Share share);

	void save(final Share share);

}