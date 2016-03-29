package de.mq.portfolio.shareportfolio.support;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

@Component("portfolio2PdfConverter")

public class Portfolio2PdfConverter implements Converter<PortfolioAO, byte[]>{

	final Font headline = FontFactory.getFont(FontFactory.TIMES_BOLD, 24);
	
	final Font tableHeadline = FontFactory.getFont(FontFactory.TIMES_BOLD, 10);
	final Font tableCell = FontFactory.getFont(FontFactory.TIMES, 10);
	
	final NumberFormat numberFormat = NumberFormat.getInstance();
	
	@Override
	public byte[] convert(final PortfolioAO portfolioAO) {
		numberFormat.setMaximumFractionDigits(2);
		numberFormat.setMinimumFractionDigits(2);
		final Document document = new Document(PageSize.A4.rotate());
		try (final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
		PdfWriter.getInstance(document, os);
			
			
		
	        document.open();
				document.addTitle(portfolioAO.getName());
				document.add(new Paragraph(String.format("Aktien %s", portfolioAO.getName() ), headline));
				final Table varianceSharesTable = new  Table(5);
				varianceSharesTable.setWidth(100);
				addCellHeader(varianceSharesTable,  "Aktie");
				addCellHeader(varianceSharesTable, "Standardabweichung [‰]");
				addCellHeader(varianceSharesTable, "Anteil [%]");
				addCellHeader(varianceSharesTable, "Rendite [%]");
				addCellHeader(varianceSharesTable, "Dividenden [%]");
				varianceSharesTable.setAlignment(Element.ALIGN_LEFT);
				portfolioAO.getTimeCourses().forEach(tc ->
				{
					addCellHeader(varianceSharesTable, tc.share().name());
					addCell(varianceSharesTable, tc.standardDeviation(), 1000d);
					addCell(varianceSharesTable, portfolioAO.getWeights().get(tc), 100d);
					addCell(varianceSharesTable, tc.totalRate(), 100d);
					addCell(varianceSharesTable, tc.totalRateDividends(), 100d);
					
				});
			
				addCell(varianceSharesTable,null, 0);
				addCellHeader(varianceSharesTable, portfolioAO.getMinStandardDeviation(), 1000d);
				addCell(varianceSharesTable, null, 0);
				addCellHeader(varianceSharesTable, portfolioAO.getTotalRate(), 100d);
				addCellHeader(varianceSharesTable, portfolioAO.getTotalRateDividends(), 100d);
			   document.add(varianceSharesTable);
			   
				
				
				document.add(new Paragraph(String.format("Korrelationen %s", portfolioAO.getName()), headline));
				
				final Table corrlationTable = new  Table(portfolioAO.getShares().size()+1);
				corrlationTable.setAlignment(Element.ALIGN_LEFT);
				corrlationTable.setWidth(100);
				addCellHeader(corrlationTable, "Korrelationen [%]");
				portfolioAO.getShares().forEach(share -> addCellHeader(corrlationTable, share));
				portfolioAO.getCorrelations().forEach(e -> {
					
					addCellHeader(corrlationTable, e.getKey());
					portfolioAO.getShares().forEach(share -> addCell(corrlationTable,e.getValue().get(share), 100d));
					
				});
				
				document.add(corrlationTable);
				document.close();
			
			return os.toByteArray();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		

	}

	private void addCell(final Table table, final Double value, final double scale)  {
		try {
			table.addCell(new Phrase(text(value, scale), tableCell));
		} catch (BadElementException ex) {
			throw new IllegalStateException(ex);
		}
	}

	private String text(final Double value, final double scale) {
		if( value == null){
			return "";
		}
		return numberFormat.format(value*scale);
	}

	private void addCellHeader(final Table table, String  text)  {
		try {
			table.addCell(new Phrase(text, tableHeadline ));
		} catch (BadElementException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	private void addCellHeader(final Table table, final Double value, final double scale)  {
		try {
			table.addCell(new Phrase(text(value, scale), tableHeadline));
		} catch (BadElementException ex) {
			throw new IllegalStateException(ex);
		}
	}

}
