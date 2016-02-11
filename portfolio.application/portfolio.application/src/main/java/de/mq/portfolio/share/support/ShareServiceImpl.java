package de.mq.portfolio.share.support;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
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
	 * @see de.mq.portfolio.share.ShareService#timeCourse(de.mq.portfolio.share.Share)
	 */
	@Override
	public final TimeCourse timeCourse(final Share share) {
		return historyRepository.history(share);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.ShareService#replacetTmeCourse(de.mq.portfolio.share.TimeCourse)
	 */
	@Override
	public final void replacetTmeCourse(final TimeCourse timeCourse) {
		shareRepository.deleteTimeCourse(timeCourse.share());
		shareRepository.save(timeCourse);
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.ShareService#shares()
	 */
	@Override
	public final Collection<Share> shares() {
		return shareRepository.shares();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.ShareService#save(de.mq.portfolio.share.Share)
	 */
	@Override
	public final void save(final Share share) {
		shareRepository.save(share);
	}
	
}
