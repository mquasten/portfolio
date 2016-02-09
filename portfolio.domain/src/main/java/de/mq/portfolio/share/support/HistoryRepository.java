package de.mq.portfolio.share.support;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

interface HistoryRepository {

	TimeCourse history(Share share);

}