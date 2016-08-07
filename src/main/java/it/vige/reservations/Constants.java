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
	 * The group id for the staff. The staff manage the tickets of the
	 * travelers
	 */
	String STAFF = "staff";
	/**
	 * The group id of the travelers. The travelers do the reservations,
	 * checkout and refund requests
	 */
	String TRAVELER = "traveler";
}
