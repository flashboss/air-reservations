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

import static it.vige.reservations.State.ALERTED;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import it.vige.reservations.model.Ticket;

/**
 * Marks the flights as alerted if the flight is ready to expire
 * 
 * @author lucastancapiano
 *
 */
public class AlertTickets implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		@SuppressWarnings("unchecked")
		List<Ticket> tickets = (List<Ticket>) execution.getVariable("ticketsToAlert");
		tickets.forEach(ticket -> ticket.getFlight().setState(ALERTED));
		execution.setVariable("ticketsToAlert", tickets);
	}

}
