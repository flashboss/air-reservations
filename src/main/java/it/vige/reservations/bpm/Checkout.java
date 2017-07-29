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

import static it.vige.reservations.Constants.SEAT;
import static it.vige.reservations.Constants.SEATS;
import static it.vige.reservations.Constants.TICKET;
import static it.vige.reservations.State.CHECKOUT;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import it.vige.reservations.model.Ticket;

/**
 * Sets the seat choosen by the user and do the checkout
 * 
 * @author lucastancapiano
 *
 */
public class Checkout implements TaskListener {

	private static final long serialVersionUID = 1247029334241810480L;

	@Override
	public void notify(DelegateTask delegateTask) {
		Ticket ticket = (Ticket) delegateTask.getExecution().getVariable(TICKET);
		Object variable = delegateTask.getExecution().getVariable(SEAT);
		if (variable != null) {
			long seat = (long) variable;
			List<String> seatStr = asList(((String) delegateTask.getExecution().getVariable(SEATS)).split("\\[|,|\\]"));
			seatStr = seatStr.subList(1, seatStr.size());
			List<Long> seats = seatStr.stream().map(s -> new Long(s.trim())).collect(toList());
			if (seats.contains(seat)) {
				ticket.getFlight().setState(CHECKOUT);
			}
			ticket.setSeat(seat);
		}
	}

}
