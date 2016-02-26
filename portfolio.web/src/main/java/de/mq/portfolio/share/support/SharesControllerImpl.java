package de.mq.portfolio.share.support;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.ShareService;

@Component("sharesController")
@Scope("singleton")
public class SharesControllerImpl {
	
	private final ShareService shareService;
	
	@Autowired
	SharesControllerImpl(final ShareService shareService) {
		this.shareService = shareService;
	}

	public final Collection<Share> shares(final Share share) {
		return shareService.shares();
	}

}
