package it.vige.reservations.bpm;

import static it.vige.reservations.State.ALERTED;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import it.vige.reservations.model.Ticket;

/**
 * Marks the flights as alerted if the flight is ready to expire
 * 
 * @author lucastancapiano
 *
 */
public class AlertTickets implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		@SuppressWarnings("unchecked")
		List<Ticket> tickets = (List<Ticket>) execution.getVariable("ticketsToAlert");
		tickets.forEach(ticket -> ticket.getFlight().setState(ALERTED));
		execution.setVariable("ticketsToAlert", tickets);
	}

}
