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
package it.vige.reservations;

/**
 * Contains utilities and demo services
 * 
 * @author lucastancapiano
 *
 */
import static it.vige.reservations.Constants.STAFF;
import static it.vige.reservations.Constants.TRAVELER;
import static it.vige.reservations.State.STARTED;
import static java.util.Arrays.asList;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.getInstance;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.activiti.engine.impl.util.IoUtil.readInputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.Picture;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.Deployment;
import org.subethamail.smtp.server.SMTPServer;

import it.vige.reservations.model.Address;
import it.vige.reservations.model.Flight;

public class DemoData {

	/**
	 * The SMTP server to start to send the mails to the users.
	 */
	private SMTPServer smtpServer;

	public SMTPServer getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(SMTPServer smtpServer) {
		this.smtpServer = smtpServer;
	}

	/**
	 * Starts the mail server
	 */
	public void startMailServer() {
		MyMessageHandlerFactory myFactory = new MyMessageHandlerFactory();
		smtpServer = new SMTPServer(myFactory);
		smtpServer.setPort(25000);
		smtpServer.start();
	}

	/**
	 * Stops the mail server
	 */
	public void stopMailServer() {
		smtpServer.stop();
	}

	/**
	 * Create the user
	 * 
	 * @param identityService
	 *            The service to create the users
	 * @param userId
	 *            Id of the user
	 * @param firstName
	 *            First name of the user
	 * @param lastName
	 *            Last name of the user
	 * @param password
	 *            Password of the user to login
	 * @param email
	 *            Email of the user. It will be used to receive the messages
	 * @param imageResource
	 *            Image availbale in the profile view of the user
	 * @param groups
	 *            groups of the users. The user can be an admin, staff or
	 *            traveler
	 * @param userInfo
	 *            Other custom properties for the user
	 */
	public void createUser(IdentityService identityService, String userId, String firstName, String lastName,
			String password, String email, String imageResource, List<String> groups, List<String> userInfo) {

		if (identityService.createUserQuery().userId(userId).count() == 0) {

			// Following data can already be set by demo setup script

			User user = identityService.newUser(userId);
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setPassword(password);
			user.setEmail(email);
			identityService.saveUser(user);

			if (groups != null) {
				for (String group : groups) {
					identityService.createMembership(userId, group);
				}
			}
		}

		// Following data is not set by demo setup script

		// image
		if (imageResource != null) {
			byte[] pictureBytes = readInputStream(this.getClass().getClassLoader().getResourceAsStream(imageResource),
					null);
			Picture picture = new Picture(pictureBytes, "image/jpeg");
			identityService.setUserPicture(userId, picture);
		}

		// user info
		if (userInfo != null) {
			for (int i = 0; i < userInfo.size(); i += 2) {
				identityService.setUserInfo(userId, userInfo.get(i), userInfo.get(i + 1));
			}
		}

	}

	/**
	 * Create the group of the user
	 * 
	 * @param identityService
	 *            The service to create the groups
	 * @param groupId
	 *            Id of the group
	 * @param type
	 *            Type of the group. It can be assignment for travelers and
	 *            staff or security role for admin and user
	 */
	public void createGroup(IdentityService identityService, String groupId, String type) {
		if (identityService.createGroupQuery().groupId(groupId).count() == 0) {
			Group newGroup = identityService.newGroup(groupId);
			newGroup.setName(groupId.substring(0, 1).toUpperCase() + groupId.substring(1));
			newGroup.setType(type);
			identityService.saveGroup(newGroup);
		}
	}

	/**
	 * Create demo users for the application
	 * 
	 * @param identityService
	 *            The service to create the users
	 */
	public void initDemoUsers(IdentityService identityService) {
		createUser(identityService, "kermit", "Kermit", "The Frog", "kermit", "kermit@activiti.org",
				"org/activiti/explorer/images/kermit.jpg", Arrays.asList(STAFF, TRAVELER, "user", "admin"),
				Arrays.asList("birthDate", "10-10-1955", "jobTitle", "Muppet", "location", "Hollywoord", "phone",
						"+123456789", "twitterName", "alfresco", "skype", "activiti_kermit_frog"));

		createUser(identityService, "gonzo", "Gonzo", "The Great", "gonzo", "gonzo@activiti.org",
				"org/activiti/explorer/images/gonzo.jpg", Arrays.asList(TRAVELER, "user"),
				asList("email", "frodobaggins@vige.it"));
		createUser(identityService, "fozzie", "Fozzie", "Bear", "fozzie", "fozzie@activiti.org",
				"org/activiti/explorer/images/fozzie.jpg", Arrays.asList(STAFF, "user"),
				asList("email", "bilbobaggins@vige.it"));
	}

	/**
	 * Create demo groups for teh application
	 * 
	 * @param identityService
	 *            The service to create the groups
	 */
	public void initDemoGroups(IdentityService identityService) {
		String[] assignmentGroups = new String[] { STAFF, TRAVELER };
		for (String groupId : assignmentGroups) {
			createGroup(identityService, groupId, "assignment");
		}

		String[] securityGroups = new String[] { "user", "admin" };
		for (String groupId : securityGroups) {
			createGroup(identityService, groupId, "security-role");
		}
	}

	/**
	 * Delete all current users and groups from the database. To use only for
	 * tests
	 * 
	 * @param identityService
	 *            The service to delete users and groups
	 */
	public void deleteAllIdentities(IdentityService identityService) {
		List<User> users = identityService.createUserQuery().list();
		for (User user : users) {
			identityService.deleteUser(user.getId());
		}
		List<Group> groups = identityService.createGroupQuery().list();
		for (Group group : groups) {
			identityService.deleteGroup(group.getId());
		}
	}

	/**
	 * Delete all history of the workflows. To use only only for tests
	 * 
	 * @param historyService
	 *            The service where delete all historical data
	 */
	public void deleteAllHistories(HistoryService historyService) {
		List<HistoricProcessInstance> historicInstances = historyService.createHistoricProcessInstanceQuery().list();
		for (HistoricProcessInstance historicProcessInstance : historicInstances)
			try {
				historyService.deleteHistoricProcessInstance(historicProcessInstance.getId());
			} catch (ActivitiObjectNotFoundException ex) {

			}
	}

	/**
	 * Delete all workflows. To use only for tests
	 * 
	 * @param repositoryService
	 *            The service where delete all workflows.
	 */
	public void deleteAllIDeployments(RepositoryService repositoryService) {
		List<Deployment> deployments = repositoryService.createDeploymentQuery().list();
		for (Deployment deployment : deployments) {
			repositoryService.deleteDeployment(deployment.getId());
		}
	}

	/**
	 * Demo funcion to get a list of flights
	 * 
	 * @param flight
	 *            Contains the informations to find in the flights list
	 * @return The resulting searched flights
	 */
	public List<Flight> getFlights(Flight flight) {
		Date today = new Date();
		return new ArrayList<Flight>(asList(new Flight[] {
				new Flight("BRITISH AIRLINES", addHours(today, 48), addHours(today, 148),
						new Address("Rome", "Italy", "Fiumicino"), new Address("Sidney", "Australy", "Ottavian"),
						5678.987, STARTED),
				new Flight("ALITALIA", addHours(today, 48), addHours(today, 248),
						new Address("Frankfort", "German", "Deutch port"), new Address("Lima", "Peru", "Limaport"),
						2578.987, STARTED),
				new Flight("AIR FRANCE", addHours(today, 48), addHours(today, 418),
						new Address("Milan", "Italy", "Segrate"), new Address("London", "England", "Birm air"),
						2567.987, STARTED),
				new Flight("KLM", addHours(today, 48), addHours(today, 148),
						new Address("London", "England", "St Luis"), new Address("Copenhagen", "Denmark", "Wilport"),
						2578.987, STARTED),
				new Flight("KLM", addHours(today, 48), addHours(today, 418),
						new Address("Reykyavik", "Island", "Islandese Airport"),
						new Address("Dublin", "Ireland", "Ireport"), 25678.987, STARTED),
				new Flight("ALITALIA", addHours(today, 48), addHours(today, 148),
						new Address("Marsiglia", "France", "Marsport"), new Address("Rome", "Italy", "Ciampino"),
						1678.987, STARTED),
				new Flight("ALITALIA", addHours(today, 48), addHours(today, 481),
						new Address("Madrid", "Spain", "Betport"), new Address("Nizza", "France", "Nizport"), 278.187,
						STARTED),
				new Flight("AIR FRANCE", addHours(today, 48), addHours(today, 448),
						new Address("Bruxelles", "Belgium", "St Wert"), new Address("Paris", "France", "Np Times"),
						258.927, STARTED),
				new Flight("AIR FRANCE", addHours(today, 48), addHours(today, 484),
						new Address("Moscow", "Russian", "Moscow air"), new Address("Bhirmll", "Austria", "Birmport"),
						256.937, STARTED),
				new Flight("AIR FRANCE", addHours(today, 48), addHours(today, 448),
						new Address("Chile City", "Cile", "Chileport"), new Address("Nizza", "France", "Nizport"),
						5178.983, STARTED),
				new Flight("BRITISH AIRLINES", addHours(today, 48), addHours(today, 98),
						new Address("New York", "United States", "York airport"),
						new Address("Birmingham", "England", "Old airport"), 2278.387, STARTED),
				new Flight("BRITISH AIRLINES", addHours(today, 48), addHours(today, 98),
						new Address("Birmingham", "England", "Birm port"),
						new Address("Toronto", "Canada", "Torontoport"), 2598.187, STARTED),
				new Flight("BRITISH AIRLINES", addHours(today, 48), addHours(today, 98),
						new Address("Oslo", "Norway", "Nor oslo"), new Address("London", "England", "Switz"), 298.117,
						STARTED),
				new Flight("ALITALIA", addHours(today, 48), addHours(today, 100),
						new Address("Bruxelles", "Belgium", "Borom"), new Address("Milan", "Italy", "Minalcino"),
						378.927, STARTED),
				new Flight("AIR FRANCE", addHours(today, 48), addHours(today, 100),
						new Address("Paris", "France", "Lovre"), new Address("Athens", "Greek", "Athens port"), 208.989,
						STARTED) }));
	}

	/**
	 * Demo funcion to get a list of seats for the flight
	 * 
	 * @param flight
	 *            The flight where search the available seats
	 * @return The resulting searched seats list
	 */
	public List<Integer> getSeats(Flight flight) {
		return asList(new Integer[] { 3, 5, 7, 8, 9, 11, 22, 34, 67, 89, 99 });
	}

	/**
	 * Utility function to get a date through parameters
	 * 
	 * @param number
	 *            A list of number representing in the order the year, month,
	 *            day, hour and minutes. Year, month and day are mandatory. Hour
	 *            and minutes are optional
	 * @return The calculated date with the sent parameters
	 */
	public static Date getDate(int... number) {
		Calendar c1 = getInstance();
		c1.set(number[0], number[1], number[2], number.length > 3 ? number[3] : 0, number.length > 4 ? number[4] : 0);
		return c1.getTime();
	}

	/**
	 * Utility function to add hours on a date
	 * 
	 * @param date
	 *            The date where add the hours
	 * @param hours
	 *            The hours to add on the date
	 * @return The calculated date with the hours to add
	 */
	public static Date addHours(Date date, int hours) {
		Calendar cal = getInstance(); // creates calendar
		cal.setTime(date); // sets calendar time/date
		cal.add(HOUR_OF_DAY, hours); // adds hour
		return cal.getTime(); // returns new date object, one hour in the future
	}

	/**
	 * Get a diff between two dates
	 * 
	 * @param date1
	 *            the oldest date
	 * @param date2
	 *            the newest date
	 * @param timeUnit
	 *            the unit in which you want the diff
	 * @return the diff value, in the provided unit
	 */
	public static long differenceBetween(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date1.getTime() - date2.getTime();
		return timeUnit.convert(diffInMillies, MILLISECONDS);
	}

	/**
	 * Verify if the passed user is an admin
	 * 
	 * @param user
	 *            The user to verify
	 * @param identityService
	 *            The service where find the user informations
	 * @return true if the user is an admin, false if a simple user
	 */
	public static boolean isAdmin(String user, IdentityService identityService) {
		return identityService.createUserQuery().userId(Authentication.getAuthenticatedUserId()).memberOfGroup("admin")
				.count() > 0;
	}

}
