package it.vige.reservations.bpm;

import static it.vige.reservations.State.CANCELED;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import it.vige.reservations.model.Ticket;

/**
 * Cancel a ticket. Th eoperation can be done by the traveller or by the staff
 * 
 * @author lucastancapiano
 *
 */
public class Cancel implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		Ticket ticket = (Ticket) execution.getVariable("ticket");
		ticket.getFlight().setState(CANCELED);
		execution.setVariable("ticket", ticket);
	}

}
