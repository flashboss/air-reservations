package it.vige.reservations.bpm;

import java.util.List;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import it.vige.reservations.model.Flight;
import it.vige.reservations.model.Payment;

/**
 * Creates the payment for the user
 * 
 * @author lucastancapiano
 *
 */
public class GetPayment implements TaskListener {

	private static final long serialVersionUID = 1247029334241810480L;

	@Override
	public void notify(DelegateTask delegateTask) {
		@SuppressWarnings("unchecked")
		List<Flight> flights = (List<Flight>) delegateTask.getVariable("choosenFlights");
		Payment payment = new Payment(flights);
		delegateTask.getExecution().setVariableLocal("payment", payment);
	}

}
