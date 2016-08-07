/******************************************************************************
 * Vige, Home of Professional Open Source Copyright 2010, Vige, and           *
 * individual contributors by the @authors tag. See the copyright.txt in the  *
 * distribution for a full listing of individual contributors.                *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain    *
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0        *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/
package it.vige.reservations.test;

import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;

import it.vige.reservations.DemoData;
import it.vige.reservations.PDFGenerator;
import it.vige.reservations.model.Ticket;
import junit.framework.TestCase;

/**
 * Tests the PDF generator creating samples of boarding pass and ticket receipt
 * 
 * @author lucastancapiano
 *
 */
public class PDFTest extends TestCase {

	private final static String TRAVELER_USER_NAME = "gonzo";

	private static final Logger LOGGER = getLogger(PDFTest.class.getName());

	/**
	 * Generate a samples of boarding pass and ticket receipt
	 */
	@Test
	public void testGenerate() {
		List<Ticket> tickets = new ArrayList<Ticket>();
		new DemoData().getFlights(null).forEach(flight -> tickets.add(new Ticket(flight, TRAVELER_USER_NAME)));
		PDFGenerator generator = new PDFGenerator(tickets);
		InputStream inputStream = generator.generateTickets();
		readTickets(inputStream);
		generator = new PDFGenerator(tickets.get(0));
		inputStream = generator.generateBoardingPass();
		readBoardingPass(inputStream);
	}

	/**
	 * Tests the generated ticket receipt
	 * 
	 * @param inputStream
	 *            The ticket receipt as stream
	 */
	private void readTickets(InputStream inputStream) {
		PdfReader reader;
		try {
			reader = new PdfReader(inputStream);
			PdfReaderContentParser parser = new PdfReaderContentParser(reader);
			int numberOfPages = reader.getNumberOfPages();
			String pages = parser.processContent(1, new SimpleTextExtractionStrategy()).getResultantText();
			assertEquals(2, numberOfPages);
			assertTrue(pages.contains("Tickets"));
			assertTrue(pages.contains("ALITALIA"));
			assertTrue(pages.contains("German"));

			reader.close();
		} catch (IOException e) {
			LOGGER.log(SEVERE, e.getMessage());
		}
	}

	/**
	 * Tests the generated boarding pass
	 * 
	 * @param inputStream
	 *            The boarding pass as stream
	 */
	private void readBoardingPass(InputStream inputStream) {
		PdfReader reader;
		try {
			reader = new PdfReader(inputStream);
			PdfReaderContentParser parser = new PdfReaderContentParser(reader);
			int numberOfPages = reader.getNumberOfPages();
			String pages = parser.processContent(1, new SimpleTextExtractionStrategy()).getResultantText();
			assertEquals(1, numberOfPages);
			assertTrue(pages.contains("Boarding pass"));
			assertTrue(pages.contains("BRITISH AIRLINES"));
			assertTrue(pages.contains("Australy"));

			reader.close();
		} catch (IOException e) {
			LOGGER.log(SEVERE, e.getMessage());
		}
	}
}
