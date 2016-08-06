package it.vige.reservations.bpm;

import static it.vige.reservations.State.REQUESTED;

import java.util.List;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import it.vige.reservations.model.Flight;

/**
 * Add the flight choosen by the user and ready to pay
 * 
 * @author lucastancapiano
 *
 */
public class AddFlight implements TaskListener {

	private static final long serialVersionUID = 1247129334241810480L;

	@Override
	public void notify(DelegateTask delegateTask) {
		@SuppressWarnings("unchecked")
		List<Flight> flights = (List<Flight>) delegateTask.getExecution().getVariable("choosenFlights");
		Flight flight = (Flight) delegateTask.getVariable("flight");
		flight.setState(REQUESTED);
		flights.add(flight);
	}

}
