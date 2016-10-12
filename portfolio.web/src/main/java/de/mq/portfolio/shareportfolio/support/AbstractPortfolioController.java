package de.mq.portfolio.shareportfolio.support;

import java.io.IOException;
import java.util.Optional;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.ResourceAccessException;

import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import de.mq.portfolio.support.UserModel;

@Component("portfolioController")
@Scope("singleton")
public abstract class AbstractPortfolioController {

	static final String DISPOSITION_PATTERN = "attachment; filename=\"%s.pdf\"";
	static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
	static final String CONTENT_TYPE_APPLICATION_PDF = "application/pdf";
	static final int DEFAULT_PAGE_SIZE = 10;
	static final Sort DEFAULT_SORT = new Sort("name");
	static final String REDIRECT_PATTERN = "portfolio?faces-redirect=true&portfolioId=%s";
	static final String REDIRECT_TO_PORTFOLIOS_PAGE = "portfolios?faces-redirect=true";
	private final SharePortfolioService sharePortfolioService;

	private final ExchangeRateService exchangeRateService;

	private final Converter<PortfolioAO, byte[]> pdfConverter;

	private final ShareService shareService;

	@Autowired
	AbstractPortfolioController(final SharePortfolioService sharePortfolioService, final ShareService shareService, final ExchangeRateService exchangeRateService, final @Qualifier("portfolio2PdfConverter") Converter<PortfolioAO, byte[]> pdfConverter) {
		this.sharePortfolioService = sharePortfolioService;
		this.shareService = shareService;
		this.exchangeRateService = exchangeRateService;
		this.pdfConverter = pdfConverter;
	}

	public void init(final PortfolioSearchAO portfolioSearchAO, final UserModel userModel) {
		page(portfolioSearchAO);
		if (userModel.getPortfolioId() != null) {
			portfolioSearchAO.setPortfolioName(sharePortfolioService.sharePortfolio(userModel.getPortfolioId()).name());
		}
		portfolioSearchAO.setExchangeRateCalculator(exchangeRateService.exchangeRateCalculator());
	}

	public void page(final PortfolioSearchAO portfolioSearchAO) {
		portfolioSearchAO.setPageable(sharePortfolioService.pageable(portfolioSearchAO.criteria(), DEFAULT_SORT, DEFAULT_PAGE_SIZE));

		portfolioSearchAO.setSharePortfolios(sharePortfolioService.portfolios(portfolioSearchAO.getPageable(), portfolioSearchAO.criteria()));

	}

	public void activate(final PortfolioSearchAO portfolioSearchAO, final UserModel userModel) {
		Assert.notNull(portfolioSearchAO.getSelectedPortfolio().id(), "Portfolio should be persistent");
		userModel.setPortfolioId(portfolioSearchAO.getSelectedPortfolio().id());
		portfolioSearchAO.setPortfolioName(portfolioSearchAO.getSelectedPortfolio().name());
	}

	public String save(final SharePortfolio sharePortfolio, final String existsMessage) {
		try {
			sharePortfolioService.save(sharePortfolio);
			return REDIRECT_TO_PORTFOLIOS_PAGE;
		} catch (final DuplicateKeyException de) {
			facesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, existsMessage, null));
			return null;
		}
	}

	public String assign(final PortfolioAO portfolioAO) {
		sharePortfolioService.assign(portfolioAO.getSharePortfolio(), portfolioAO.getTimeCourses());
		return String.format(REDIRECT_PATTERN, portfolioAO.getId());
	}

	public void init(final PortfolioAO portfolioAO) {
		if (portfolioAO.getId() == null) {
			portfolioAO.setSharePortfolio(BeanUtils.instantiateClass(SharePortfolioImpl.class), Optional.empty());
			return;
		}

		final SharePortfolio sharePortfolio = sharePortfolioService.sharePortfolio(portfolioAO.getId());
		portfolioAO.setSharePortfolio(sharePortfolio, Optional.of(exchangeRateService.exchangeRateCalculator(sharePortfolio.exchangeRateTranslations())));

	}

	public final void pdf(final PortfolioAO portfolioAO) {

		final HttpServletResponse response = (HttpServletResponse) facesContext().getExternalContext().getResponse();
		response.reset();
		response.setContentType(CONTENT_TYPE_APPLICATION_PDF);

		response.setHeader(HEADER_CONTENT_DISPOSITION, String.format(DISPOSITION_PATTERN, portfolioAO.getName())); // The

		try {
			final byte[] content = pdfConverter.convert(portfolioAO);
			response.setContentLength(content.length);
			FileCopyUtils.copy(content, response.getOutputStream());
		} catch (IOException ex) {
			throw new ResourceAccessException("Unable to create Pdf", ex);
		}

		facesContext().responseComplete();
	}

	public final String commit(final String portfolioName) {
		final SharePortfolio sharePortfolio = sharePortfolioService.committedPortfolio(portfolioName);
		return String.format(REDIRECT_PATTERN, sharePortfolio.id());

	}

	public final String delete(final String portfolioId) {
		sharePortfolioService.delete(portfolioId);
		return REDIRECT_TO_PORTFOLIOS_PAGE;
	}

	public final String deleteTimeCourse(final String sharePortfolioId, final String timeCourseCode) {

		final Optional<TimeCourse> timeCourse = shareService.timeCourse(timeCourseCode);
		if (!timeCourse.isPresent()) {
			return String.format(REDIRECT_PATTERN, sharePortfolioId);
		}

		final SharePortfolio sharePortfolio = sharePortfolioService.sharePortfolio(sharePortfolioId);
		Assert.isTrue(!sharePortfolio.isCommitted(), "Portfolio should not be committed.");

		sharePortfolio.remove(timeCourse.get());
		sharePortfolioService.save(sharePortfolio);
		return String.format(REDIRECT_PATTERN, sharePortfolio.id());

	}

	@Lookup
	abstract FacesContext facesContext();

}
