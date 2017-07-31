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
package it.vige.reservations.bpm;

import static it.vige.reservations.Constants.CURRENT_USER;
import static it.vige.reservations.Constants.FILE_PATH;
import static it.vige.reservations.Constants.PAYMENT;
import static it.vige.reservations.Constants.TICKETS;
import static java.lang.System.getProperty;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import it.vige.reservations.PDFGenerator;
import it.vige.reservations.model.Payment;
import it.vige.reservations.model.Ticket;

/**
 * Generates the tickets after the payment and creates the ticket receipt that
 * will send to user by email
 * 
 * @author lucastancapiano
 *
 */
public class SetPayment implements TaskListener {

	private static final long serialVersionUID = 1247029334241810480L;

	private static final Logger LOGGER = getLogger(SetPayment.class.getName());

	@Override
	public void notify(DelegateTask delegateTask) {
		Payment payment = (Payment) delegateTask.getVariable(PAYMENT);
		payment.setUserName((String) delegateTask.getVariable(CURRENT_USER));
		payment.setDate(new Date());
		List<Ticket> tickets = new ArrayList<Ticket>();
		payment.getFlights().forEach(flight -> tickets.add(new Ticket(flight, payment.getUserName())));
		InputStream pdf = new PDFGenerator(tickets).generateTickets();
		String filePath = getProperty("java.io.tmpdir") + payment.getUserName() + ".pdf";
		try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
			byte[] bytes = new byte[pdf.available()];
			pdf.read(bytes);
			outputStream.write(bytes);
			delegateTask.getExecution().setVariableLocal(FILE_PATH, filePath);
		} catch (IOException e) {
			LOGGER.log(SEVERE, e.getMessage());
		}
		delegateTask.getExecution().setVariable(TICKETS, tickets);
	}

}
