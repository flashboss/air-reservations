package it.vige.reservations;

import static com.itextpdf.text.Element.ALIGN_CENTER;
import static com.itextpdf.text.Font.FontFamily.TIMES_ROMAN;
import static com.itextpdf.text.pdf.PdfWriter.getInstance;
import static java.util.Arrays.asList;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import it.vige.reservations.model.Flight;
import it.vige.reservations.model.Ticket;

/**
 * It generates PDF documents. Used to create the attachments for the users
 * 
 * @author lucastancapiano
 *
 */
public class PDFGenerator {

	/**
	 * The default font of the document
	 */
	private static Font catFont = new Font(TIMES_ROMAN, 18, Font.BOLD);

	/**
	 * The tickets to write in the document
	 */
	private java.util.List<Ticket> tickets;

	private static final Logger LOGGER = getLogger(PDFGenerator.class.getName());

	public PDFGenerator(Ticket ticket) {
		this.tickets = asList(new Ticket[] { ticket });
	}

	public PDFGenerator(java.util.List<Ticket> tickets) {
		this.tickets = tickets;
	}

	/**
	 * It generate the PDF showing the tickets of the user
	 * 
	 * @return a stream document with the ticket of the user
	 */
	public InputStream generateTickets() {
		Document document = new Document();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			getInstance(document, outputStream);
			document.open();

			Paragraph preface = new Paragraph();
			// We add one empty line
			addEmptyLine(preface, 1);
			// Lets write a big header
			preface.add(new Paragraph("Tickets:", catFont));
			addEmptyLine(preface, 2);

			// add a table
			createTable(preface);
			document.add(preface);

			document.close();
		} catch (Exception e) {
			LOGGER.log(SEVERE, e.getMessage());
		}
		return new ByteArrayInputStream(outputStream.toByteArray());
	}

	/**
	 * It generates the boarding pass
	 * 
	 * @return The stream document of the boarding pass
	 */
	public InputStream generateBoardingPass() {
		Document document = new Document();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			getInstance(document, outputStream);
			document.open();

			Paragraph preface = new Paragraph();
			// We add one empty line
			addEmptyLine(preface, 1);
			addEmptyLine(preface, 2);

			// add a table
			preface.add(tickets.get(0) + "");
			document.add(preface);

			document.close();
		} catch (Exception e) {
			LOGGER.log(SEVERE, e.getMessage());
		}
		return new ByteArrayInputStream(outputStream.toByteArray());
	}

	/**
	 * Creates a formatted table for the tickets of the user
	 * 
	 * @param subCatPart
	 *            the paragraph of the document where add the the table
	 * @throws BadElementException
	 *             Signals an attempt to create an <CODE>Element</CODE> that
	 *             hasn't got the right form
	 */
	private void createTable(Paragraph subCatPart) throws BadElementException {
		PdfPTable table = new PdfPTable(5);

		PdfPCell c1 = new PdfPCell(new Phrase("ID"));
		c1.setHorizontalAlignment(ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("USER"));
		c1.setHorizontalAlignment(ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("COMPANY"));
		c1.setHorizontalAlignment(ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("FROM"));
		c1.setHorizontalAlignment(ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("TO"));
		c1.setHorizontalAlignment(ALIGN_CENTER);
		table.addCell(c1);
		table.setHeaderRows(1);

		for (Ticket ticket : tickets) {
			table.addCell(ticket.getId());
			table.addCell(ticket.getUserName());
			Flight flight = ticket.getFlight();
			table.addCell(flight.getCompany());
			table.addCell(flight.getAddressFrom() + " " + flight.getStartTime());
			table.addCell(flight.getAddressTo() + " " + flight.getArriveTime());
		}

		subCatPart.add(table);

	}

	/**
	 * Adds an empty line to the document
	 * 
	 * @param paragraph
	 *            The paragraph where add the empty line
	 * @param number
	 *            The number of empty lines to add
	 */
	private void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}
}
