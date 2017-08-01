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
 * The constants for the group users
 * 
 * @author lucastancapiano
 *
 */
public interface Constants {
	/**
	 * The group id for the staff. The staff manage the tickets of the travelers
	 */
	String STAFF = "staff";
	/**
	 * The group id of the travelers. The travelers do the reservations,
	 * checkout and refund requests
	 */
	String TRAVELER = "traveler";
	/**
	 * Payment variable to manage the payment informations
	 */
	String PAYMENT = "payment";
	/**
	 * The informations of the flight
	 */
	String FLIGHT = "flight";
	/**
	 * The informations of the tickets
	 */
	String TICKETS = "tickets";
	/**
	 * The informations of the ticket
	 */
	String TICKET = "ticket";
	/**
	 * The informations of the tickets to alert
	 */
	String TICKETS_TO_ALERT = "ticketsToAlert";
	/**
	 * The informations of the tickets to cancel
	 */
	String TICKETS_TO_CANCEL = "ticketsToCancel";
	/**
	 * The informations of the seats
	 */
	String SEATS = "seats";
	/**
	 * The informations of the seat
	 */
	String SEAT = "seat";
	/**
	 * The informations of the choosen flights
	 */
	String CHOOSEN_FLIGHTS = "choosenFlights";
	/**
	 * The informations of the host name
	 */
	String HOSTNAME = "hostName";
	/**
	 * The informations of the current user
	 */
	String CURRENT_USER = "currentUser";
	/**
	 * The informations of the email
	 */
	String EMAIL = "email";
	/**
	 * The informations of the file path
	 */
	String FILE_PATH = "file_path";
	/**
	 * The informations of the flights
	 */
	String FLIGHTS = "flights";
	/**
	 * The informations of the date from
	 */
	String DATE_FROM = "dateFrom";
	/**
	 * The informations of the address to
	 */
	String ADDRESS_TO = "addressTo";
	/**
	 * The informations of the address from
	 */
	String ADDRESS_FROM = "addressFrom";
	/**
	 * The informations of the prize
	 */
	String PRIZE = "prize";
	/**
	 * The informations of the user task 1
	 */
	String USERTASK1 = "usertask1";
	/**
	 * The informations of the user task 2
	 */
	String USERTASK2 = "usertask2";
	/**
	 * The informations of the user task 3
	 */
	String USERTASK3 = "usertask3";
	/**
	 * The informations of the user task 4
	 */
	String USERTASK4 = "usertask4";
	/**
	 * The informations of the user task 5
	 */
	String USERTASK5 = "usertask5";
	/**
	 * The informations of the user task 6
	 */
	String USERTASK6 = "usertask6";
	/**
	 * The informations of the user task 7
	 */
	String USERTASK7 = "usertask7";
	/**
	 * The informations of the user task 8
	 */
	String USERTASK8 = "usertask8";
	/**
	 * The informations of the mail task 1
	 */
	String MAILTASK1 = "mailtask1";
	/**
	 * The informations of the mail task 2
	 */
	String MAILTASK2 = "mailtask2";
	/**
	 * The informations of the operation
	 */
	String OPERATION = "operation";
	/**
	 * The informations of the reservations
	 */
	String RESERVATIONS = "reservations";
	/**
	 * The informations of the scheduler
	 */
	String SCHEDULER = "scheduler";
	/**
	 * The informations of the username
	 */
	String USERNAME = "username";
	/**
	 * The informations of the password
	 */
	String PASSWORD = "password";
	/**
	 * a service to return the email of the choosen user
	 */
	String EMAIL_CALCULATOR = "email_calculator";

}
