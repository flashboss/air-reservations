/******************************************************************************
 * Vige, Home of Professional Open Source Copyright 2010, Vige, and           *
 * individual contributors by the @authors tag. See the copyright.txt in the  *
 * distribution for a full listing of individual contributors.                *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain    *
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0        *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/
package it.vige.reservations.bpm;

import static it.vige.reservations.Constants.OPERATION;
import static it.vige.reservations.Constants.TICKET;
import static it.vige.reservations.Constants.TICKETS_TO_CANCEL;
import static it.vige.reservations.Constants.USERTASK4;
import static it.vige.reservations.State.CANCELED;
import static org.activiti.engine.impl.context.Context.getProcessEngineConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.task.Task;

import it.vige.reservations.model.Ticket;

/**
 * Cancel all requested tickets notified by the scheduler. Theese ticket are
 * expired
 * 
 * @author lucastancapiano
 *
 */
public class CancelTickets implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		@SuppressWarnings("unchecked")
		List<Ticket> tickets = (List<Ticket>) execution.getVariable(TICKETS_TO_CANCEL);
		tickets.forEach(ticket -> ticket.getFlight().setState(CANCELED));
		execution.setVariable(TICKETS_TO_CANCEL, tickets);
		TaskService taskService = getProcessEngineConfiguration().getTaskService();
		List<Task> tasks = taskService.createTaskQuery().includeProcessVariables().includeTaskLocalVariables()
				.taskDefinitionKey(USERTASK4).list();
		for (Task task : tasks) {
			Ticket ticket = (Ticket) taskService.getVariable(task.getId(), TICKET);
			if (tickets.contains(ticket)) {
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put(OPERATION, CANCELED);
				taskService.complete(task.getId(), variables);
			}
		}
	}

}
