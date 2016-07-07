package de.mq.portfolio.shareportfolio.support;

import org.springframework.util.Assert;

import de.mq.portfolio.share.TimeCourse;


class TimeCourseRetrospectiveImpl implements TimeCourseRetrospective {
	
	private final TimeCourse timeCourse;
	private final double start;
	private final double end;
	
	TimeCourseRetrospectiveImpl(final TimeCourse timeCourse, final double start, final double end) {
		Assert.notNull(timeCourse, "TimeCourse is mandatory");
		this.timeCourse = timeCourse;
		this.start = start;
		this.end = end;
	}

	
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.TimeCourseRetrospective#timeCourse()
	 */
	@Override
	public final TimeCourse timeCourse() {
		return timeCourse;
	}
	
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.TimeCourseRetrospective#end()
	 */
	@Override
	public final double end() {
		return end;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.TimeCourseRetrospective#start()
	 */
	@Override
	public final double start() {
		return start;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.TimeCourseRetrospective#name()
	 */
	@Override
	public final String name() {
		return timeCourse != null ? timeCourse.name() : null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.TimeCourseRetrospective#rate()
	 */
	@Override
	public final double rate() {
		return (end-start)/start;
	}

}
