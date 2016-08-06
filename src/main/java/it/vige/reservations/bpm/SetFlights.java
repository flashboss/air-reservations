package it.vige.reservations.bpm;

import java.util.ArrayList;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import it.vige.reservations.model.Flight;

/**
 * Initialize internal variables and creates the mail of the authenticated user
 * 
 * @author lucastancapiano
 *
 */
public class SetFlights implements ExecutionListener {

	private static final long serialVersionUID = 1247129334241810480L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		execution.createVariableLocal("flights", new ArrayList<Flight>());
		execution.createVariableLocal("choosenFlights", new ArrayList<Flight>());
		String currentUser = (String) execution.getVariable("currentUser");
		String email = execution.getEngineServices().getIdentityService().getUserInfo(currentUser, "email");
		execution.createVariableLocal("email", email);
	}

}
