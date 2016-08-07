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

import static it.vige.reservations.DemoData.getDate;
import static it.vige.reservations.State.CANCELED;
import static it.vige.reservations.State.CHECKOUT;
import static it.vige.reservations.State.REQUESTED;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.history.HistoricVariableInstanceQuery;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;

import it.vige.reservations.model.Payment;
import it.vige.reservations.model.Ticket;

/**
 * Tests all flow of the reservations. See the reservation.bpmn file for details
 * 
 * @author lucastancapiano
 *
 */
public class ReservationsTest extends Startup {

	/**
	 * It executes all the tasks and verify if they work. It calls the execute
	 * method and then tests the checkout, seats and cancel operation
	 */
	@Deployment(resources = { "bpm/reservations.bpmn", "bpm/checkout.bpmn", "bpm/cancel.bpmn" })
	public void testChooseFlight() {
		init();

		// AUTHENTICATION
		// Always reset authenticated user to avoid any mistakes
		identityService.setAuthenticatedUserId(TRAVELLER_USER_NAME);

		// EXECUTE TEST
		execute(runtimeService, taskService, historyService);

		// MY TICKETS
		List<Task> myTickets = taskService.createTaskQuery().taskDefinitionKey("usertask4").includeProcessVariables()
				.list();
		assertEquals(4, myTickets.size());
		Task task = myTickets.get(0);
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("operation", CHECKOUT.name());
		taskService.complete(task.getId(), variables);
		task = myTickets.get(1);
		taskService.complete(task.getId(), variables);
		task = myTickets.get(2);
		variables.put("operation", CANCELED.name());
		taskService.complete(task.getId(), variables);
		task = myTickets.get(3);
		taskService.complete(task.getId(), variables);

		// CHOOSE THE SEATS
		myTickets = taskService.createTaskQuery().taskDefinitionKey("usertask5").includeProcessVariables().list();
		assertEquals(2, myTickets.size());
		task = myTickets.get(0);
		variables = new HashMap<String, Object>();
		taskService.complete(task.getId(), variables);
		task = myTickets.get(1);
		taskService.complete(task.getId(), variables);

		// TWO CHECKOUT PROCESS MUST BE STILL ACTIVE BECAUSE THE SEAT IS NOT
		// CHOOSEN
		myTickets = taskService.createTaskQuery().taskDefinitionKey("usertask5").includeProcessVariables().list();
		assertEquals(2, myTickets.size());
		task = myTickets.get(0);
		variables = new HashMap<String, Object>();
		variables.put("seat", 333333L);
		taskService.complete(task.getId(), variables);
		task = myTickets.get(1);
		variables.put("seat", 333333L);
		taskService.complete(task.getId(), variables);

		// TWO CHECKOUT PROCESS MUST BE STILL ACTIVE BECAUSE A NOT EXISTING SEAT
		// WAS CHOOSEN
		myTickets = taskService.createTaskQuery().taskDefinitionKey("usertask5").includeProcessVariables().list();
		assertEquals(2, myTickets.size());
		task = myTickets.get(0);
		variables = taskService.getVariables(task.getId());
		List<String> seatStr = asList(((String) variables.get("seats")).split("\\[|,|\\]"));
		seatStr = seatStr.subList(1, seatStr.size());
		List<Long> seats = seatStr.stream().map(s -> new Long(s.trim())).collect(toList());
		variables.put("seat", seats.get(4));
		taskService.complete(task.getId(), variables);
		task = myTickets.get(1);
		variables = taskService.getVariables(task.getId());
		seatStr = asList(((String) variables.get("seats")).split("\\[|,|\\]"));
		seatStr = seatStr.subList(1, seatStr.size());
		seats = seatStr.stream().map(s -> new Long(s.trim())).collect(toList());
		variables.put("seat", seats.get(5));
		taskService.complete(task.getId(), variables);

		// THE TRAVELER RECEIVES THE BOARDING PASSES FOR THE TICKET IN CHECKOUT STATE
		List<Task> myBoardingPasses = taskService.createTaskQuery().taskDefinitionKey("usertask6")
				.includeProcessVariables().list();
		assertEquals(2, myBoardingPasses.size());
		task = myBoardingPasses.get(0);
		Date dueDate = ((Ticket) task.getProcessVariables().get("ticket")).getFlight().getArriveTime();
		assertEquals(dueDate, task.getDueDate());
		List<Attachment> attachments = taskService.getTaskAttachments(task.getId());
		assertEquals(1, attachments.size());
		assertEquals("Boarding Pass", attachments.get(0).getName());

		task = myBoardingPasses.get(1);
		dueDate = ((Ticket) task.getProcessVariables().get("ticket")).getFlight().getArriveTime();
		assertEquals(dueDate, task.getDueDate());
		attachments = taskService.getTaskAttachments(task.getId());
		assertEquals(1, attachments.size());
		assertEquals("Boarding Pass", attachments.get(0).getName());
		myBoardingPasses.forEach(t -> taskService.complete(t.getId()));

		// THE STAFF RECEIVES THE NOTIFICATIONS FOR THE TICKET IN CHECKOUT STATE
		List<Task> staffCheckoutTickets = taskService.createTaskQuery().taskDefinitionKey("usertask7").includeProcessVariables()
				.list();
		assertEquals(2, staffCheckoutTickets.size());
		task = staffCheckoutTickets.get(0);
		dueDate = ((Ticket) task.getProcessVariables().get("ticket")).getFlight().getArriveTime();
		assertEquals(dueDate, task.getDueDate());

		task = staffCheckoutTickets.get(1);
		dueDate = ((Ticket) task.getProcessVariables().get("ticket")).getFlight().getArriveTime();
		assertEquals(dueDate, task.getDueDate());
		staffCheckoutTickets.forEach(t -> taskService.complete(t.getId()));

		// THE STAFF RECEIVES THE NOTIFICATIONS FOR THE TICKET IN CANCEL STATE
		List<Task> staffCancelTickets = taskService.createTaskQuery().taskDefinitionKey("usertask8").includeProcessVariables()
				.list();
		assertEquals(2, staffCancelTickets.size());
		task = staffCancelTickets.get(0);
		dueDate = ((Ticket) task.getProcessVariables().get("ticket")).getFlight().getArriveTime();
		assertEquals(dueDate, task.getDueDate());

		task = staffCancelTickets.get(1);
		dueDate = ((Ticket) task.getProcessVariables().get("ticket")).getFlight().getArriveTime();
		assertEquals(dueDate, task.getDueDate());
		staffCancelTickets.forEach(t -> taskService.complete(t.getId()));

		end();
	}

	/**
	 * It tests the flow starting by the search of the flights until to the
	 * email of the received ticket
	 * 
	 * @param runtimeService
	 *            The service to start the process
	 * @param taskService
	 *            The service to find and complete the tasks
	 * @param historyService
	 *            The service to see the history of the tasks
	 */
	public static void execute(RuntimeService runtimeService, TaskService taskService, HistoryService historyService) {

		// STARTING PROCESS
		runtimeService.startProcessInstanceByKey("reservations");

		// SEARCH FLIGHTS
		List<Task> searchFlights = taskService.createTaskQuery().taskDefinitionKey("usertask1")
				.includeProcessVariables().list();
		assertEquals(1, searchFlights.size());
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("dateFrom", getDate(2016, 7, 3));
		variables.put("addressForm", "Australy");
		variables.put("addressTo", "London");
		variables.put("prize", 122.99);
		Task firstTask = searchFlights.get(0);
		taskService.complete(firstTask.getId(), variables);

		// CHOOSE FLIGHTS
		List<Task> chooseFlights = taskService.createTaskQuery().taskDefinitionKey("usertask2")
				.includeProcessVariables().list();
		assertEquals(15, chooseFlights.size());
		String id = chooseFlights.get(2).getId();
		taskService.claim(id, TRAVELLER_USER_NAME);
		id = chooseFlights.get(5).getId();
		taskService.claim(id, TRAVELLER_USER_NAME);
		id = chooseFlights.get(8).getId();
		taskService.claim(id, TRAVELLER_USER_NAME);
		id = chooseFlights.get(9).getId();
		taskService.claim(id, TRAVELLER_USER_NAME);
		taskService.complete(id, variables);

		// PAYMENT
		List<Task> payments = taskService.createTaskQuery().taskDefinitionKey("usertask3").includeProcessVariables()
				.list();
		assertEquals(1, payments.size());
		firstTask = payments.get(0);
		variables = firstTask.getProcessVariables();
		Payment payment = (Payment) variables.get("payment");
		payment.setCreditCard("MYHH3339YYTVF9988");
		payment.setLastThreeCode("GH7");
		assertNull(payment.getDate());
		assertNull(payment.getUserName());
		taskService.complete(firstTask.getId(), variables);

		// VERIFY IF THE MAIL IS SENT
		HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService
				.createHistoricActivityInstanceQuery();
		List<HistoricActivityInstance> receiveTicket = historicActivityInstanceQuery.activityId("mailtask1").list();
		assertEquals(1, receiveTicket.size());

		// VERIFY IF THE STATES OF THE REQUESTED FLIGHTS ARE CHANGED
		HistoricVariableInstanceQuery historicVariableInstanceQuery = historyService
				.createHistoricVariableInstanceQuery();
		List<HistoricVariableInstance> historyTickets = historicVariableInstanceQuery.variableName("tickets").list();
		@SuppressWarnings("unchecked")
		List<Ticket> requestedTickets = historyTickets.stream().map(historic -> (List<Ticket>) historic.getValue())
				.flatMap(l -> l.stream()).collect(toList());
		assertEquals(4, requestedTickets.size());
		requestedTickets.forEach(ticket -> assertEquals(REQUESTED, ticket.getFlight().getState()));

	}
}