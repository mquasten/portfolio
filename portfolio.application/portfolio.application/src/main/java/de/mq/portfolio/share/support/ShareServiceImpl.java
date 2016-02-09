package de.mq.portfolio.share.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;





@Service
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
	 * @see de.mq.portfolio.share.ShareService#importTimeCourses()
	 */
	@Override
	public final void importTimeCourses() {
		shareRepository.shares().forEach(share ->  {
			System.out.println("****");
			System.out.println(share.name() + "(" + share.code() +")");
			final TimeCourse timeCourse = historyRepository.history(share);
			shareRepository.deleteTimeCourse(share);
			shareRepository.save(timeCourse);
		});
	}

	
}
