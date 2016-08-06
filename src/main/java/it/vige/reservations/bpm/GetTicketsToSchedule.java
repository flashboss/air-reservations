package it.vige.reservations.bpm;

import static it.vige.reservations.DemoData.differenceBetween;
import static it.vige.reservations.State.REQUESTED;
import static java.net.InetAddress.getByName;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.task.Task;

import it.vige.reservations.model.Ticket;

/**
 * Gets all ticket to alert and cancel by the scheduler. The scheduler searches
 * all flights ready to fly 48 hours before and all expired tickets
 * 
 * @author lucastancapiano
 *
 */
public class GetTicketsToSchedule implements JavaDelegate {

	private final static int MAX_HOURS_TO_ALERT = 48;
	private final static int MAX_HOURS_TO_CANCEL = 4;

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		TaskService taskService = execution.getEngineServices().getTaskService();
		List<Task> tasks = taskService.createTaskQuery().includeProcessVariables().includeTaskLocalVariables()
				.taskDefinitionKey("usertask4").active().list();
		execution.createVariableLocal("ticketsToAlert", new ArrayList<Ticket>());
		execution.createVariableLocal("ticketsToCancel", new ArrayList<Ticket>());
		if (tasks.size() > 0) {
			execution.createVariableLocal("hostName", getByName("localhost"));
			@SuppressWarnings("unchecked")
			List<Ticket> tickets = (List<Ticket>) tasks.get(0).getProcessVariables().get("tickets");
			Date today = new Date();

			List<Ticket> ticketsToAlert = tickets.stream().filter(ticket -> {
				long difference = differenceBetween(ticket.getFlight().getStartTime(), today, HOURS);
				return difference <= MAX_HOURS_TO_ALERT && difference > MAX_HOURS_TO_CANCEL
						&& ticket.getFlight().getState() == REQUESTED;
			}).collect(toList());
			execution.setVariableLocal("ticketsToAlert", ticketsToAlert);

			List<Ticket> ticketsToCancel = tickets.stream()
					.filter(ticket -> differenceBetween(ticket.getFlight().getStartTime(), today,
							HOURS) <= MAX_HOURS_TO_CANCEL && ticket.getFlight().getState() == REQUESTED)
					.collect(toList());
			execution.setVariableLocal("ticketsToCancel", ticketsToCancel);
		}
	}

}
