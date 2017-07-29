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
import static org.activiti.engine.impl.context.Context.getProcessEngineConfiguration;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import it.vige.reservations.PDFGenerator;
import it.vige.reservations.model.Ticket;

/**
 * Creates the boarding pass for the traveler
 * 
 * @author lucastancapiano
 *
 */
public class SetBoardingPass implements TaskListener {

	private static final long serialVersionUID = 7930555954056624168L;

	@Override
	public void notify(DelegateTask delegateTask) {
		Ticket ticket = (Ticket) delegateTask.getExecution().getVariable(TICKET);

		PDFGenerator generator = new PDFGenerator(ticket);
		TaskService taskService = getProcessEngineConfiguration().getTaskService();
		taskService.createAttachment("application/pdf", delegateTask.getId(), delegateTask.getProcessInstanceId(),
				"Boarding Pass", "Your Boarding Pass", generator.generateBoardingPass());
	}

}
