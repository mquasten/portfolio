package de.mq.portfolio.shareportfolio.support;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

	static final int SIZE_VARIANCE_TABLE = 100;



	static final String HEADLINE_CORRELATION_PATTERN = "Korrelationen %s";



	static final String HEADLINE_SHARE_PATTERN = "Aktien %s [Währung: %s]";

	final Font headline = FontFactory.getFont(FontFactory.TIMES_BOLD, 24);
	
	final Font tableHeadline = FontFactory.getFont(FontFactory.TIMES_BOLD, 10);
	final Font tableCell = FontFactory.getFont(FontFactory.TIMES, 10);
	
	
	final NumberFormat numberFormat = NumberFormat.getInstance();
	final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
	
	
	private final Converter<String,String> currencyConverter;
	
	@Autowired
	Portfolio2PdfConverter(@Qualifier("currencyConverter") final Converter<String, String> currencyConverter) {
		this.currencyConverter = currencyConverter;
	}

	@Override
	public byte[] convert(final PortfolioAO portfolioAO) {
		numberFormat.setMaximumFractionDigits(2);
		numberFormat.setMinimumFractionDigits(2);

		final Document document = newDocument();
		try (final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
		PdfWriter.getInstance(document, os);
			
			
		
	        document.open();
				document.addTitle(portfolioAO.getName());
				document.add(new Paragraph(String.format(HEADLINE_SHARE_PATTERN, portfolioAO.getName() , currencyConverter.convert(portfolioAO.getCurrency()) ), headline));
				final Table varianceSharesTable = newVarianceTable();
				
				varianceSharesTable.setWidth(SIZE_VARIANCE_TABLE);
				addCellHeader(varianceSharesTable,  "Aktie");
				addCellHeader(varianceSharesTable,  "WKN");
				addCellHeader(varianceSharesTable, "Zeitreihe");
				addCellHeader(varianceSharesTable, "Standardabweichung [‰]");
				addCellHeader(varianceSharesTable, "Anteil [%]");
				addCellHeader(varianceSharesTable, "Rendite [%]");
				addCellHeader(varianceSharesTable, "Dividenden [%]");
				varianceSharesTable.setAlignment(Element.ALIGN_LEFT);
				portfolioAO.getTimeCourses().forEach(tc ->
				{
					addCellHeader(varianceSharesTable, tc.share().name());
					addCell(varianceSharesTable, tc.wkn());
					
					addCell(varianceSharesTable, "" +dateFormat.format( tc.start()) +" - "+ dateFormat.format( tc.end()) );
					addCell(varianceSharesTable, tc.standardDeviation(), 1000d);
					addCell(varianceSharesTable, portfolioAO.getWeights().get(tc), 100d);
					addCell(varianceSharesTable, tc.totalRate(), 100d);
					addCell(varianceSharesTable, tc.totalRateDividends(), 100d);
					
				});
			
				addCell(varianceSharesTable,null, 0);
				addCell(varianceSharesTable,null, 0);
				addCell(varianceSharesTable,null, 0);
				addCellHeader(varianceSharesTable, portfolioAO.getMinStandardDeviation(), 1000d);
				addCell(varianceSharesTable, null, 0);
				addCellHeader(varianceSharesTable, portfolioAO.getTotalRate(), 100d);
				addCellHeader(varianceSharesTable, portfolioAO.getTotalRateDividends(), 100d);
			   document.add(varianceSharesTable);
			   
				
				
				document.add(new Paragraph(String.format(HEADLINE_CORRELATION_PATTERN, portfolioAO.getName()), headline));
				
				final Table corrlationTable = newCorrelationTable(portfolioAO.getShares().size()+1);
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

	Table newCorrelationTable(final int size) throws BadElementException {
		return new  Table(size);
	}

	Table newVarianceTable() throws BadElementException {
		return new  Table(7);
	}

	Document newDocument() {
		return new Document(PageSize.A4.rotate());
	}

	private void addCell(final Table table, final Double value, final double scale)  {
		try {
			table.addCell(new Phrase(text(value, scale), tableCell));
		} catch (BadElementException ex) {
			throw new IllegalStateException(ex);
		}
	}
	private void addCell(final Table table, final String value)  {
		try {
			table.addCell(new Phrase(value, tableCell));
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
