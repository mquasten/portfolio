package de.mq.portfolio.share.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;


@Service("shareService")
class ShareServiceImpl implements ShareService {

	
	private HistoryRepository historyRepository;

	private ShareRepository shareRepository;

	@Autowired
	ShareServiceImpl(HistoryRepository historyRepository, ShareRepository shareRepository) {
		this.historyRepository = historyRepository;
		this.shareRepository = shareRepository;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.share.support.ShareService#timeCourse(de.mq.portfolio.
	 * share.support.Share)
	 */
	@Override
	public final TimeCourse timeCourse(final Share share) {
		return historyRepository.history(share);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.ShareService#timeCourses(org.springframework.data.domain.Pageable, de.mq.portfolio.share.Share)
	 */
	@Override
	public final Collection<TimeCourse> timeCourses(final Pageable pageable, final Share share) {
		return shareRepository.timeCourses(pageable, share);
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.ShareService#timeCourse(java.lang.String)
	 */
	@Override
	public final Optional<TimeCourse> timeCourse(final String code) {
		return shareRepository.timeCourses(Arrays.asList(code)).stream().findFirst();
	}

	@Override
	public Pageable pageable(final Share share, final Sort sort, final Number size) {
		return shareRepository.pageable(share,sort, size);
	}
	
	@Override
	public Collection<String> indexes() {
		return shareRepository.distinctIndex();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.share.support.ShareService#replacetTmeCourse(de.mq.portfolio
	 * .share.support.TimeCourse)
	 */
	@Override
	public final void replacetTimeCourse(final TimeCourse timeCourse) {
		shareRepository.deleteTimeCourse(timeCourse.share());
		shareRepository.save(timeCourse);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.share.support.ShareService#shares()
	 */
	@Override
	public final Collection<Share> shares() {
		return shareRepository.shares();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.share.support.ShareService#save(de.mq.portfolio.share.
	 * support.Share)
	 */
	@Override
	public final void save(final Share share) {
		shareRepository.save(share);
	}

}
