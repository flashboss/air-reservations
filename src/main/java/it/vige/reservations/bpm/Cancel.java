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

import static it.vige.reservations.Constants.TICKET;
import static it.vige.reservations.State.CANCELED;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import it.vige.reservations.model.Ticket;

/**
 * Cancel a ticket. Th eoperation can be done by the traveler or by the staff
 * 
 * @author lucastancapiano
 *
 */
public class Cancel implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		Ticket ticket = (Ticket) execution.getVariable(TICKET);
		ticket.getFlight().setState(CANCELED);
		execution.setVariable(TICKET, ticket);
	}

}
