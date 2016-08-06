package it.vige.reservations.bpm;

import static it.vige.reservations.State.CHECKOUT;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import it.vige.reservations.model.Ticket;

/**
 * Sets the seat choosen by the user and do the checkout
 * 
 * @author lucastancapiano
 *
 */
public class Checkout implements TaskListener {

	private static final long serialVersionUID = 1247029334241810480L;

	@Override
	public void notify(DelegateTask delegateTask) {
		Ticket ticket = (Ticket) delegateTask.getExecution().getVariable("ticket");
		Object variable = delegateTask.getExecution().getVariable("seat");
		if (variable != null) {
			long seat = (long) variable;
			List<String> seatStr = asList(
					((String) delegateTask.getExecution().getVariable("seats")).split("\\[|,|\\]"));
			seatStr = seatStr.subList(1, seatStr.size());
			List<Long> seats = seatStr.stream().map(s -> new Long(s.trim())).collect(toList());
			if (seats.contains(seat)) {
				ticket.getFlight().setState(CHECKOUT);
			}
			ticket.setSeat(seat);
		}
	}

}
