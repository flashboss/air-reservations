package it.vige.reservations.bpm;

import static it.vige.reservations.State.CANCELED;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.task.Task;

import it.vige.reservations.model.Ticket;

/**
 * Cancel all requested tickets notified by the scheduler. Theese ticket are expired
 * 
 * @author lucastancapiano
 *
 */
public class CancelTickets implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		@SuppressWarnings("unchecked")
		List<Ticket> tickets = (List<Ticket>) execution.getVariable("ticketsToCancel");
		tickets.forEach(ticket -> ticket.getFlight().setState(CANCELED));
		execution.setVariable("ticketsToCancel", tickets);
		TaskService taskService = execution.getEngineServices().getTaskService();
		List<Task> tasks = taskService.createTaskQuery().includeProcessVariables().includeTaskLocalVariables()
				.taskDefinitionKey("usertask4").list();
		for (Task task : tasks) {
			Ticket ticket = (Ticket) taskService.getVariable(task.getId(), "ticket");
			if (tickets.contains(ticket)) {
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("operation", CANCELED);
				taskService.complete(task.getId(), variables);
			}
		}
	}

}
