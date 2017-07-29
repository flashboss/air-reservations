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

import static org.activiti.engine.impl.context.Context.getProcessEngineConfiguration;

import java.util.Date;
import java.util.List;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;

/**
 * Cancel all started tickets never requested by the user, they are notified by
 * the scheduler. Theese ticket are expired
 * 
 * @author lucastancapiano
 *
 */
public class ExpireTickets implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		TaskService taskService = getProcessEngineConfiguration().getTaskService();
		TaskQuery taskQuery = taskService.createTaskQuery().includeProcessVariables().includeTaskLocalVariables();
		List<Task> tasks = taskQuery.taskDefinitionKey("usertask6").list();
		List<Task> staffTasks = taskQuery.taskDefinitionKey("usertask7").list();
		tasks.addAll(staffTasks);
		Date today = new Date();
		for (Task task : tasks) {
			if (today.compareTo(task.getDueDate()) >= 0) {
				taskService.complete(task.getId());
			}
		}
	}

}
