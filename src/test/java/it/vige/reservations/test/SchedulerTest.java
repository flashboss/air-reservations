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
package it.vige.reservations.test;

import static it.vige.reservations.Constants.MAILTASK2;
import static it.vige.reservations.Constants.OPERATION;
import static it.vige.reservations.Constants.SCHEDULER;
import static it.vige.reservations.Constants.SEAT;
import static it.vige.reservations.Constants.SEATS;
import static it.vige.reservations.Constants.TICKETS_TO_ALERT;
import static it.vige.reservations.Constants.TICKETS_TO_CANCEL;
import static it.vige.reservations.Constants.USERTASK4;
import static it.vige.reservations.Constants.USERTASK5;
import static it.vige.reservations.Constants.USERTASK6;
import static it.vige.reservations.Constants.USERTASK7;
import static it.vige.reservations.Constants.USERTASK8;
import static it.vige.reservations.State.ALERTED;
import static it.vige.reservations.State.CANCELED;
import static it.vige.reservations.State.CHECKOUT;
import static it.vige.reservations.test.ReservationsTest.execute;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.history.HistoricVariableInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.engine.test.Deployment;

import it.vige.reservations.model.Ticket;

/**
 * Tests all flow of the scheduler. See the scheduler.bpmn file for details
 * 
 * @author lucastancapiano
 *
 */
public class SchedulerTest extends Startup {

	/**
	 * Verify the alert messages, if the mail is sent and if the sate of the
	 * flight is set to ALERTED
	 */
	@Deployment(resources = { "bpm/reservations.bpmn", "bpm/scheduler.bpmn", "bpm/cancel.bpmn" })
	public void testAlertedMessages() {
		init();

		// AUTHENTICATION
		// Always reset authenticated user to avoid any mistakes
		identityService.setAuthenticatedUserId(TRAVELER_USER_NAME);

		// STARTING RESERVATIONS PROCESS. I NEED TO GET TICKETS IN THE DB
		execute(runtimeService, taskService, historyService);

		// AUTHENTICATION
		// Always reset authenticated user to avoid any mistakes
		identityService.setAuthenticatedUserId(ADMIN_USER_NAME);

		// STARTING PROCESS
		runtimeService.startProcessInstanceByKey(SCHEDULER);

		// VERIFY IF THE MAIL IS SENT
		HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService
				.createHistoricActivityInstanceQuery();
		List<HistoricActivityInstance> tickets = historicActivityInstanceQuery.activityId(MAILTASK2).list();
		assertEquals(4, tickets.size());

		// VERIFY IF THE STATES OF THE ALERTED FLIGHTS ARE CHANGED
		HistoricVariableInstanceQuery historicVariableInstanceQuery = historyService
				.createHistoricVariableInstanceQuery();
		List<HistoricVariableInstance> historyAlertedTickets = historicVariableInstanceQuery
				.variableName(TICKETS_TO_ALERT).list();
		@SuppressWarnings("unchecked")
		List<Ticket> alertedTickets = historyAlertedTickets.stream().map(historic -> (List<Ticket>) historic.getValue())
				.flatMap(l -> l.stream()).collect(toList());
		assertEquals(4, alertedTickets.size());
		alertedTickets.forEach(ticket -> assertEquals(ALERTED, ticket.getFlight().getState()));

		// DELETE THE TASKS TO CLOSE THE PROCESSES
		List<Task> tasks = taskService.createTaskQuery().includeProcessVariables().includeTaskLocalVariables()
				.taskDefinitionKey(USERTASK4).active().list();
		for (Task task : tasks) {
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put(OPERATION, CANCELED);
			taskService.complete(task.getId(), variables);
		}

		// DELETE THE TASKS TO CLOSE THE PROCESSES
		tasks = taskService.createTaskQuery().includeProcessVariables().includeTaskLocalVariables()
				.taskDefinitionKey(USERTASK8).active().list();
		for (Task task : tasks)
			taskService.complete(task.getId());
		end();
	}

	/**
	 * Verify the deletion of the expired requested flights
	 */
	@Deployment(resources = { "bpm/reservations.bpmn", "bpm/scheduler.bpmn", "bpm/cancel.bpmn" })
	public void testCanceledTasks() {
		init();

		// AUTHENTICATION
		// Always reset authenticated user to avoid any mistakes
		identityService.setAuthenticatedUserId(TRAVELER_USER_NAME);

		// STARTING RESERVATIONS PROCESS. I NEED TO GET TICKETS IN THE DB
		execute(runtimeService, taskService, historyService);

		// TO TEST THE CANCELED TASKS I MUST TO REPLACE THE CURRENT FLIGHT DATES
		List<Task> tasks = taskService.createTaskQuery().includeProcessVariables().includeTaskLocalVariables()
				.taskDefinitionKey(USERTASK4).active().list();
		for (Task task : tasks) {
			Ticket ticket = (Ticket) taskService.getVariable(task.getId(), "ticket");
			@SuppressWarnings("unchecked")
			List<Ticket> tickets = (List<Ticket>) taskService.getVariable(task.getId(), "tickets");
			Ticket otherTicket = tickets.stream().filter(oticket -> oticket.equals(ticket)).collect(toList()).get(0);
			Date today = new Date();
			ticket.getFlight().setStartTime(today);
			otherTicket.getFlight().setStartTime(today);
			taskService.setVariable(task.getId(), "tickets", tickets);
			taskService.setVariable(task.getId(), "ticket", ticket);
		}

		// AUTHENTICATION
		// Always reset authenticated user to avoid any mistakes
		identityService.setAuthenticatedUserId(ADMIN_USER_NAME);

		// STARTING PROCESS
		runtimeService.startProcessInstanceByKey(SCHEDULER);

		// VERIFY IF THE STATES OF THE CANCELED FLIGHTS ARE CHANGED
		HistoricVariableInstanceQuery historicVariableInstanceQuery = historyService
				.createHistoricVariableInstanceQuery();
		List<HistoricVariableInstance> historyCanceledTickets = historicVariableInstanceQuery
				.variableName(TICKETS_TO_CANCEL).list();
		@SuppressWarnings("unchecked")
		List<Ticket> canceledTickets = historyCanceledTickets.stream()
				.map(historic -> (List<Ticket>) historic.getValue()).flatMap(l -> l.stream()).collect(toList());
		assertEquals(4, canceledTickets.size());
		canceledTickets.forEach(ticket -> assertEquals(CANCELED, ticket.getFlight().getState()));

		// DELETE THE TASKS TO CLOSE THE PROCESSES
		tasks = taskService.createTaskQuery().includeProcessVariables().includeTaskLocalVariables()
				.taskDefinitionKey(USERTASK8).active().list();
		for (Task task : tasks)
			taskService.complete(task.getId());
		end();
	}

	/**
	 * Verify the deletion of the expired checkout flights
	 */
	@Deployment(resources = { "bpm/reservations.bpmn", "bpm/scheduler.bpmn", "bpm/checkout.bpmn" })
	public void testExpiredTasks() {
		init();

		// AUTHENTICATION
		// Always reset authenticated user to avoid any mistakes
		identityService.setAuthenticatedUserId(TRAVELER_USER_NAME);

		// STARTING RESERVATIONS PROCESS. I NEED TO GET TICKETS IN THE DB
		execute(runtimeService, taskService, historyService);

		// MY TICKETS
		List<Task> myTickets = taskService.createTaskQuery().taskDefinitionKey(USERTASK4).includeProcessVariables()
				.list();
		assertEquals(4, myTickets.size());
		Task task = myTickets.get(0);
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(OPERATION, CHECKOUT.name());
		taskService.complete(task.getId(), variables);
		task = myTickets.get(1);
		taskService.complete(task.getId(), variables);
		task = myTickets.get(2);
		taskService.complete(task.getId(), variables);
		task = myTickets.get(3);
		taskService.complete(task.getId(), variables);

		// TWO CHECKOUT PROCESS MUST BE STILL ACTIVE BECAUSE A NOT EXISTING SEAT
		// WAS CHOOSEN
		myTickets = taskService.createTaskQuery().taskDefinitionKey(USERTASK5).includeProcessVariables().list();
		assertEquals(4, myTickets.size());
		task = myTickets.get(0);
		variables = taskService.getVariables(task.getId());
		List<String> seatStr = asList(((String) variables.get(SEATS)).split("\\[|,|\\]"));
		seatStr = seatStr.subList(1, seatStr.size());
		List<Long> seats = seatStr.stream().map(s -> new Long(s.trim())).collect(toList());
		variables.put(SEAT, seats.get(4));
		taskService.complete(task.getId(), variables);
		task = myTickets.get(1);
		variables = taskService.getVariables(task.getId());
		seatStr = asList(((String) variables.get(SEATS)).split("\\[|,|\\]"));
		seatStr = seatStr.subList(1, seatStr.size());
		seats = seatStr.stream().map(s -> new Long(s.trim())).collect(toList());
		variables.put(SEAT, seats.get(5));
		taskService.complete(task.getId(), variables);
		task = myTickets.get(2);
		variables = taskService.getVariables(task.getId());
		seatStr = asList(((String) variables.get(SEATS)).split("\\[|,|\\]"));
		seatStr = seatStr.subList(1, seatStr.size());
		seats = seatStr.stream().map(s -> new Long(s.trim())).collect(toList());
		variables.put(SEAT, seats.get(6));
		taskService.complete(task.getId(), variables);
		task = myTickets.get(3);
		variables = taskService.getVariables(task.getId());
		seatStr = asList(((String) variables.get(SEATS)).split("\\[|,|\\]"));
		seatStr = seatStr.subList(1, seatStr.size());
		seats = seatStr.stream().map(s -> new Long(s.trim())).collect(toList());
		variables.put(SEAT, seats.get(7));
		taskService.complete(task.getId(), variables);

		// TO TEST THE EXPIRED TASKS I MUST TO REPLACE THE CURRENT DUE DATE
		TaskQuery taskQuery = taskService.createTaskQuery().includeProcessVariables().includeTaskLocalVariables();
		List<Task> tasks = taskQuery.taskDefinitionKey(USERTASK6).active().list();
		List<Task> staffTasks = taskQuery.taskDefinitionKey(USERTASK7).list();
		tasks.addAll(staffTasks);
		for (Task usertask : tasks) {
			taskService.setDueDate(usertask.getId(), new Date());
		}

		// AUTHENTICATION
		// Always reset authenticated user to avoid any mistakes
		identityService.setAuthenticatedUserId(ADMIN_USER_NAME);

		// STARTING PROCESS
		runtimeService.startProcessInstanceByKey(SCHEDULER);

		end();
	}
}