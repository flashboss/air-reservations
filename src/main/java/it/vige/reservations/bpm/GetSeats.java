package it.vige.reservations.bpm;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import it.vige.reservations.DemoData;
import it.vige.reservations.model.Ticket;

/**
 * Gets the available seats of the requested flight to checkout
 * 
 * @author lucastancapiano
 *
 */
public class GetSeats implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Ticket ticket = (Ticket) execution.getVariable("ticket");
		String seats = new DemoData().getSeats(ticket.getFlight()) + "";
		execution.setVariable("seats", seats);
	}

}
