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
import static it.vige.reservations.Constants.CURRENT_USER;
import static it.vige.reservations.Constants.EMAIL;
import static it.vige.reservations.Constants.FLIGHTS;
import static org.activiti.engine.impl.context.Context.getProcessEngineConfiguration;

import java.util.ArrayList;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import it.vige.reservations.model.Flight;

/**
 * Initialize internal variables and creates the mail of the authenticated user
 * 
 * @author lucastancapiano
 *
 */
public class SetFlights implements ExecutionListener {

	private static final long serialVersionUID = 1247129334241810480L;

	@Override
	public void notify(DelegateExecution execution) {
		execution.setVariableLocal(FLIGHTS, new ArrayList<Flight>());
		execution.setVariableLocal(CHOOSEN_FLIGHTS, new ArrayList<Flight>());
		String currentUser = (String) execution.getVariable(CURRENT_USER);
		String email = getProcessEngineConfiguration().getIdentityService().getUserInfo(currentUser, "email");
		execution.setVariableLocal(EMAIL, email);
	}

}
