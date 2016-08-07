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
package org.activiti.explorer.conf;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.repository.Deployment;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import it.vige.reservations.DemoData;

/**
 * @author lucastancapiano
 */
@Configuration
public class DemoDataConfiguration extends DemoData {

	protected static final Logger LOGGER = getLogger(DemoDataConfiguration.class);

	@Autowired
	protected IdentityService identityService;

	@Autowired
	protected RepositoryService repositoryService;

	@Autowired
	protected RuntimeService runtimeService;

	@Autowired
	protected TaskService taskService;

	@Autowired
	protected ManagementService managementService;

	@Autowired
	protected ProcessEngineConfigurationImpl processEngineConfiguration;

	@Autowired
	protected Environment environment;

	@PostConstruct
	public void init() {
		startMailServer();
		if (Boolean.valueOf(environment.getProperty("create.demo.users", "true"))) {
			LOGGER.info("Initializing demo groups");
			initDemoGroups(identityService);
			LOGGER.info("Initializing demo users");
			initDemoUsers(identityService);
		}

		if (Boolean.valueOf(environment.getProperty("create.demo.definitions", "true"))) {
			LOGGER.info("Initializing demo process definitions");
			initProcessDefinitions();
		}
	}

	@PreDestroy
	public void stop() {
		stopMailServer();
	}

	protected void initProcessDefinitions() {

		String deploymentName = "Demo processes";
		List<Deployment> deploymentList = repositoryService.createDeploymentQuery().deploymentName(deploymentName)
				.list();

		if (deploymentList == null || deploymentList.isEmpty()) {
			repositoryService.createDeployment().name(deploymentName).addClasspathResource("bpm/cancel.bpmn")
					.addClasspathResource("bpm/checkout.bpmn").addClasspathResource("bpm/reservations.bpmn")
					.addClasspathResource("bpm/scheduler.bpmn").deploy();
		}

	}

}
