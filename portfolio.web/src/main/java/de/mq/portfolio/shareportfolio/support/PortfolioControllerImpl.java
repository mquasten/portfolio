package de.mq.portfolio.shareportfolio.support;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.ResourceAccessException;

import de.mq.portfolio.shareportfolio.SharePortfolio;
import de.mq.portfolio.support.UserModel;

@Component("portfolioController")
@Scope("singleton")
public class PortfolioControllerImpl {

	private static final String REDIRECT_TO_PORTFOLIOS_PAGE = "portfolios?faces-redirect=true";
	private final SharePortfolioService sharePortfolioService;
	
	private final Converter<PortfolioAO, byte[]> pdfConverter;

	@Autowired
	PortfolioControllerImpl(final SharePortfolioService sharePortfolioService,  final @Qualifier("portfolio2PdfConverter") Converter<PortfolioAO, byte[]> pdfConverter) {
		this.sharePortfolioService = sharePortfolioService;
		this.pdfConverter=pdfConverter;
	}

	public void init(final PortfolioSearchAO portfolioSearchAO, final UserModel userModel) {
		page(portfolioSearchAO);
		if (userModel.getPortfolioId() != null) {
			portfolioSearchAO.setPortfolioName(sharePortfolioService.sharePortfolio(userModel.getPortfolioId()).name());
		}

	}

	public void page(final PortfolioSearchAO portfolioSearchAO) {
		portfolioSearchAO.setPageable(sharePortfolioService.pageable(portfolioSearchAO.criteria(), new Sort("name"), 10));

		portfolioSearchAO.setSharePortfolios(sharePortfolioService.portfolios(portfolioSearchAO.getPageable(), portfolioSearchAO.criteria()));
	}

	public void activate(final PortfolioSearchAO portfolioSearchAO, final UserModel userModel) {
		userModel.setPortfolioId(portfolioSearchAO.getSelectedPortfolio().id());
		portfolioSearchAO.setPortfolioName(portfolioSearchAO.getSelectedPortfolio().name());
	}

	public String save(final SharePortfolio sharePortfolio, final FacesContext facesContext, final String existsMessage) {
		try {
			sharePortfolioService.save(sharePortfolio);
			return REDIRECT_TO_PORTFOLIOS_PAGE;
		} catch (final DuplicateKeyException de) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, existsMessage, null));
			return null;
		}
	}

	public void init(final PortfolioAO portfolioAO) {
		if (portfolioAO.getId() == null) {
			portfolioAO.setSharePortfolio(BeanUtils.instantiateClass(SharePortfolioImpl.class));
			return;
		}

		portfolioAO.setSharePortfolio(sharePortfolioService.sharePortfolio(portfolioAO.getId()));

	}

	public final void pdf(final PortfolioAO portfolioAO, final FacesContext facesContext)  {
		
		
		final HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		response.reset();
		response.setContentType("application/pdf");
		
		response.setHeader("Content-Disposition", "attachment; filename=\"" + portfolioAO.getName() + ".pdf" + "\""); // The
		
			try {
				final byte[] content = pdfConverter.convert(portfolioAO);
				response.setContentLength(content.length);
				FileCopyUtils.copy(content, response.getOutputStream());
			} catch (IOException ex) {
			   throw new ResourceAccessException("Unable to create Pdf", ex);
			}
	

		facesContext.responseComplete();
	}

}
