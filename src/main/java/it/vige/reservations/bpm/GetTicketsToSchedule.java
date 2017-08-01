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

import static it.vige.reservations.Constants.EMAIL_CALCULATOR;
import static it.vige.reservations.Constants.HOSTNAME;
import static it.vige.reservations.Constants.TICKETS;
import static it.vige.reservations.Constants.TICKETS_TO_ALERT;
import static it.vige.reservations.Constants.TICKETS_TO_CANCEL;
import static it.vige.reservations.Constants.USERTASK4;
import static it.vige.reservations.DemoData.differenceBetween;
import static it.vige.reservations.State.REQUESTED;
import static java.net.InetAddress.getByName;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;
import static java.util.stream.Collectors.toList;
import static org.activiti.engine.impl.context.Context.getProcessEngineConfiguration;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.task.Task;

import it.vige.reservations.EmailCalculator;
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

	private static final Logger LOGGER = getLogger(GetTicketsToSchedule.class.getName());

	@Override
	public void execute(DelegateExecution execution) {

		TaskService taskService = getProcessEngineConfiguration().getTaskService();
		List<Task> tasks = taskService.createTaskQuery().includeProcessVariables().includeTaskLocalVariables()
				.taskDefinitionKey(USERTASK4).active().list();
		execution.setVariableLocal(TICKETS_TO_ALERT, new ArrayList<Ticket>());
		execution.setVariableLocal(TICKETS_TO_CANCEL, new ArrayList<Ticket>());
		if (tasks.size() > 0) {
			try {
				execution.setVariableLocal(HOSTNAME, getByName("localhost"));
			} catch (UnknownHostException e) {
				LOGGER.log(SEVERE, e.getMessage());
			}
			@SuppressWarnings("unchecked")
			List<Ticket> tickets = (List<Ticket>) tasks.get(0).getProcessVariables().get(TICKETS);
			Date today = new Date();

			List<Ticket> ticketsToAlert = tickets.stream().filter(ticket -> {
				long difference = differenceBetween(ticket.getFlight().getStartTime(), today, HOURS);
				return difference <= MAX_HOURS_TO_ALERT && difference > MAX_HOURS_TO_CANCEL
						&& ticket.getFlight().getState() == REQUESTED;
			}).collect(toList());
			execution.setVariableLocal(TICKETS_TO_ALERT, ticketsToAlert);

			List<Ticket> ticketsToCancel = tickets.stream().filter(
					ticket -> differenceBetween(ticket.getFlight().getStartTime(), today, HOURS) <= MAX_HOURS_TO_CANCEL
							&& ticket.getFlight().getState() == REQUESTED)
					.collect(toList());
			execution.setVariableLocal(TICKETS_TO_CANCEL, ticketsToCancel);
			execution.setVariable(EMAIL_CALCULATOR, new EmailCalculator());
		}
	}

}
