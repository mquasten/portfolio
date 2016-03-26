package de.mq.portfolio.shareportfolio.support;

import java.io.ByteArrayOutputStream;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;

import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

@Component("portfolio2PdfConverter")

public class Portfolio2PdfConverter implements Converter<PortfolioAO, byte[]>{

	@Override
	public byte[] convert(final PortfolioAO portfolioAO) {
		final Document document = new Document(PageSize.A4.rotate());
		try (final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			PdfWriter.getInstance(document, os);
			document.open();
			
				document.addTitle(portfolioAO.getName());
				document.add(new Paragraph("Aktien"));
				final Table varianceSharesTable = new  Table(5);
				varianceSharesTable.setWidth(100);
				addCell(varianceSharesTable, "Aktie");
				addCell(varianceSharesTable, "Standardabweichung [‰]");
				addCell(varianceSharesTable, "Anteil [%]");
				addCell(varianceSharesTable, "Rendite [%]");
				addCell(varianceSharesTable, "Dividenden [%]");
				varianceSharesTable.setAlignment(Element.ALIGN_LEFT);
				portfolioAO.getTimeCourses().forEach(tc ->
				{
					addCell(varianceSharesTable, String.valueOf(tc.share().name()));
					addCell(varianceSharesTable, String.valueOf(tc.standardDeviation()));
					addCell(varianceSharesTable, String.valueOf(portfolioAO.getWeights().get(tc)));
					addCell(varianceSharesTable, String.valueOf(tc.totalRate()));
					addCell(varianceSharesTable, String.valueOf(tc.totalRateDividends()));
					
				});
			
				addCell(varianceSharesTable, "");
				addCell(varianceSharesTable, String.valueOf(portfolioAO.getMinStandardDeviation()));
				addCell(varianceSharesTable, "");
				addCell(varianceSharesTable, String.valueOf(portfolioAO.getTotalRate()));
				addCell(varianceSharesTable, String.valueOf(portfolioAO.getTotalRateDividends()));
			   document.add(varianceSharesTable);
			   
				
				
				document.add(new Paragraph("Korrelationen"));
				
				final Table corrlationTable = new  Table(portfolioAO.getShares().size()+1);
				corrlationTable.setAlignment(Element.ALIGN_LEFT);
				corrlationTable.setWidth(100);
				addCell(corrlationTable, "Korrelationen [%]");
				portfolioAO.getShares().forEach(share -> addCell(corrlationTable, share));
				portfolioAO.getCorrelations().forEach(e -> {
					
					addCell(corrlationTable, e.getKey());
					portfolioAO.getShares().forEach(share -> addCell(corrlationTable, String.valueOf(e.getValue().get(share))));
					
				});
				
				document.add(corrlationTable);
				document.close();
			
			return os.toByteArray();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		

	}

	private void addCell(final Table table, String  text)  {
		try {
			table.addCell(text);
		} catch (BadElementException ex) {
			throw new IllegalStateException(ex);
		}
	}

	
	
	

}
