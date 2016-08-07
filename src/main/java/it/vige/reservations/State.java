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
 * They are the states of the flight
 * 
 * @author lucastancapiano
 *
 */
public enum State {
	/**
	 * The flight is ready to choice
	 */
	STARTED,
	/**
	 * The flight is choose by the user
	 */
	REQUESTED,
	/**
	 * The flight is notified to the user if the checkout is not still done
	 */
	ALERTED,
	/**
	 * The checkout of the flight is done
	 */
	CHECKOUT,
	/**
	 * The flight is canceled by the user or by the scheduler
	 */
	CANCELED
}
