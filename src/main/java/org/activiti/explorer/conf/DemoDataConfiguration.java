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
 * @author Joram Barrez
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
