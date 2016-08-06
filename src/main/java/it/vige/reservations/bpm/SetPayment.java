package it.vige.reservations.bpm;

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
		Payment payment = (Payment) delegateTask.getVariable("payment");
		payment.setUserName((String) delegateTask.getVariable("currentUser"));
		payment.setDate(new Date());
		List<Ticket> tickets = new ArrayList<Ticket>();
		payment.getFlights().forEach(flight -> tickets.add(new Ticket(flight, payment.getUserName())));
		InputStream pdf = new PDFGenerator(tickets).generateTickets();
		String filePath = getProperty("java.io.tmpdir") + payment.getUserName() + ".pdf";
		try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
			byte[] bytes = new byte[pdf.available()];
			pdf.read(bytes);
			outputStream.write(bytes);
			delegateTask.getExecution().createVariableLocal("file_path", filePath);
		} catch (IOException e) {
			LOGGER.log(SEVERE, e.getMessage());
		}
		delegateTask.getExecution().createVariableLocal("tickets", tickets);
	}

}
