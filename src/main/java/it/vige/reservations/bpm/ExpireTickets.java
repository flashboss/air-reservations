package it.vige.reservations.bpm;

import java.util.Date;
import java.util.List;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;

/**
 * Cancel all started tickets never requested by the user, they are notified by
 * the scheduler. Theese ticket are expired
 * 
 * @author lucastancapiano
 *
 */
public class ExpireTickets implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		TaskService taskService = execution.getEngineServices().getTaskService();
		TaskQuery taskQuery = taskService.createTaskQuery().includeProcessVariables().includeTaskLocalVariables();
		List<Task> tasks = taskQuery.taskDefinitionKey("usertask6").list();
		List<Task> staffTasks = taskQuery.taskDefinitionKey("usertask7").list();
		tasks.addAll(staffTasks);
		Date today = new Date();
		for (Task task : tasks) {
			if (today.compareTo(task.getDueDate()) >= 0) {
				taskService.complete(task.getId());
			}
		}
	}

}
