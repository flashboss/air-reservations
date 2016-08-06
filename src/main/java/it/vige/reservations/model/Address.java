package it.vige.reservations.model;

import java.io.Serializable;

/**
 * The address of the flight
 * 
 * @author lucastancapiano
 *
 */
public class Address implements Serializable {

	private static final long serialVersionUID = 6097112279554597834L;

	/**
	 * The city
	 */
	private String city;
	/**
	 * The nation
	 */
	private String state;
	/**
	 * The airport name
	 */
	private String airport;

	public Address(String city, String state, String airport) {
		super();
		this.city = city;
		this.state = state;
		this.airport = airport;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAirport() {
		return airport;
	}

	public void setAirport(String airport) {
		this.airport = airport;
	}

	@Override
	public String toString() {
		return state + "," + city + "," + airport;
	}
}
