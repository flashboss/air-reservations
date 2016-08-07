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

import org.activiti.engine.impl.test.ResourceActivitiTestCase;

import it.vige.reservations.DemoData;

/**
 * Parent test case for all the tests of the application. It has the groups
 * constant names used for the test, the initial creation of the environment and
 * the final clean of teh environment
 * 
 * @author lucastancapiano
 *
 */
public class Startup extends ResourceActivitiTestCase {

	/**
	 * Default admin user to start the scheduler process
	 */
	protected final static String ADMIN_USER_NAME = "kermit";
	/**
	 * Default traveler user to work with the reservations
	 */
	protected final static String TRAVELER_USER_NAME = "gonzo";

	/**
	 * Used to work with the mail server and user operations
	 */
	private DemoData demoData = new DemoData();

	public Startup() {
		super("activiti.cfg-mem.xml");
	}

	/**
	 * Inits the mail server and create the demo users and groups
	 */
	public void init() {
		// STARTING MAIL SERVER
		demoData.startMailServer();

		// TEST GROUPS AND USERS
		demoData.initDemoGroups(identityService);
		demoData.initDemoUsers(identityService);
	}

	/**
	 * Ends the environment cleaning the database and stopping the mail server
	 */
	public void end() {
		// CLEANING DB
		demoData.deleteAllIdentities(identityService);
		demoData.deleteAllHistories(historyService);
		demoData.deleteAllIDeployments(repositoryService);

		// STOP MAIL SERVER
		demoData.stopMailServer();

	}

}
