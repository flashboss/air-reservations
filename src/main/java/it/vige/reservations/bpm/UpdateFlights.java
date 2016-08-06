package it.vige.reservations.bpm;

import java.util.Date;
import java.util.List;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import it.vige.reservations.DemoData;
import it.vige.reservations.model.Address;
import it.vige.reservations.model.Flight;

/**
 * Executes the search of the flights passing parameters as date from, source
 * address, destination address and prize
 * 
 * @author lucastancapiano
 *
 */
public class UpdateFlights implements TaskListener {

	private static final long serialVersionUID = 1247029334241810480L;

	@Override
	public void notify(DelegateTask delegateTask) {
		DemoData demoData = new DemoData();
		@SuppressWarnings("unchecked")
		List<Flight> flights = (List<Flight>) delegateTask.getExecution().getVariable("flights");
		flights.clear();
		@SuppressWarnings("unchecked")
		List<Flight> choosenFlights = (List<Flight>) delegateTask.getExecution().getVariable("choosenFlights");
		choosenFlights.clear();
		Date dateFrom = (Date) delegateTask.getExecution().getVariable("dateFrom");
		String addressTo = (String) delegateTask.getExecution().getVariable("addressTo");
		String addressFrom = (String) delegateTask.getExecution().getVariable("addressFrom");
		Object prize = delegateTask.getExecution().getVariable("prize");
		Flight flight = new Flight(null, dateFrom, null, new Address(null, null, addressTo),
				new Address(null, null, addressFrom), prize != null ? (double) prize : null, null);
		flights.addAll(demoData.getFlights(flight));
	}

}
