package de.mq.portfolio.shareportfolio.support;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.Writer;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfWriter;

import de.mq.portfolio.shareportfolio.SharePortfolio;
import de.mq.portfolio.support.UserModel;

@Component("portfolioController")
@Scope("singleton")
public class PortfolioControllerImpl {

	private static final String REDIRECT_TO_PORTFOLIOS_PAGE = "portfolios?faces-redirect=true";
	private final SharePortfolioService sharePortfolioService;

	@Autowired
	PortfolioControllerImpl(final SharePortfolioService sharePortfolioService) {
		this.sharePortfolioService = sharePortfolioService;
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

	public final void pdf(final PortfolioAO portfolioAO, final FacesContext facesContext) throws DocumentException, IOException {
		System.out.println("****");
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
		response.reset();
		response.setContentType("application/pdf");
		// response.setContentLength(contentLength);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + portfolioAO.getName() + ".pdf" + "\""); // The
																													
																														// instead.

		final Document document = new Document();
		
		
		try (final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			PdfWriter.getInstance(document, os);
			document.open();
			
				document.addTitle(portfolioAO.getName());
				document.add(new Paragraph("Standardabweichungen der Aktien [%]"));
				final Table table = new  Table(2);
				table.setAlignment(Element.ALIGN_LEFT);
				portfolioAO.getStandardDeviations().forEach(e -> { 
					
				try {
				table.addCell(e.getKey());
				table.addCell(String.valueOf(e.getValue()));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
				
				});
				; 
			document.add(table);
				
				document.close();
			
			FileCopyUtils.copy(os.toByteArray(), response.getOutputStream());
		}

		facesContext.responseComplete();
	}

}
