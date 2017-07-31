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

import static it.vige.reservations.Constants.CHOOSEN_FLIGHTS;
import static it.vige.reservations.Constants.PAYMENT;

import java.util.List;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import it.vige.reservations.model.Flight;
import it.vige.reservations.model.Payment;

/**
 * Creates the payment for the user
 * 
 * @author lucastancapiano
 *
 */
public class GetPayment implements TaskListener {

	private static final long serialVersionUID = 1247029334241810480L;

	@Override
	public void notify(DelegateTask delegateTask) {
		@SuppressWarnings("unchecked")
		List<Flight> flights = (List<Flight>) delegateTask.getVariable(CHOOSEN_FLIGHTS);
		Payment payment = new Payment(flights);
		delegateTask.getExecution().setVariable(PAYMENT, payment);
	}

}
