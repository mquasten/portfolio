package de.mq.portfolio.share;

import java.util.Collection;




public interface ShareService{

	TimeCourse timeCourse(final Share share);

	void replacetTimeCourse(final TimeCourse timeCourse);

	Collection<Share> shares();

	void save(final Share share);

	

	

}