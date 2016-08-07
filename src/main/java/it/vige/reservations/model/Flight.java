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
package it.vige.reservations.model;

import java.io.Serializable;
import java.util.Date;

import it.vige.reservations.State;

/**
 * The flight
 * 
 * @author lucastancapiano
 *
 */
public class Flight implements Serializable {

	private static final long serialVersionUID = -2847424192229429802L;

	/**
	 * The company of the flight
	 */
	private String company;
	/**
	 * The start time when the flight starts
	 */
	private Date startTime;
	/**
	 * The arrive time when the flight ends
	 */
	private Date arriveTime;
	/**
	 * The source address of the flight
	 */
	private Address addressTo;
	/**
	 * The destination address of the flight
	 */
	private Address addressFrom;
	/**
	 * The prize of the flight
	 */
	private Double prize;
	/**
	 * The state of the flight. It can be started, requested, alerted, checkout
	 * or canceled
	 */
	private State state;

	public Flight(String company, Date startTime, Date arriveTime, Address addressTo, Address addressFrom, Double prize,
			State state) {
		super();
		this.company = company;
		this.startTime = startTime;
		this.arriveTime = arriveTime;
		this.addressTo = addressTo;
		this.addressFrom = addressFrom;
		this.prize = prize;
		this.state = state;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(Date arriveTime) {
		this.arriveTime = arriveTime;
	}

	public Address getAddressTo() {
		return addressTo;
	}

	public void setAddressTo(Address addressTo) {
		this.addressTo = addressTo;
	}

	public Address getAddressFrom() {
		return addressFrom;
	}

	public void setAddressFrom(Address addressFrom) {
		this.addressFrom = addressFrom;
	}

	public Double getPrize() {
		return prize;
	}

	public void setPrize(Double prize) {
		this.prize = prize;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return company + " from: " + startTime + " " + addressFrom + " to: " + arriveTime + " " + addressTo;
	}

}
