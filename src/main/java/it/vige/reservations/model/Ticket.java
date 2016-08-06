package it.vige.reservations.model;

import java.io.Serializable;

/**
 * When the user pay the flights, the tickets are created
 * 
 * @author lucastancapiano
 *
 */
public class Ticket implements Serializable {

	private static final long serialVersionUID = -5927021752230036125L;

	/**
	 * The user name of the payer
	 */
	private String userName;
	/**
	 * The flight choose by the user
	 */
	private Flight flight;
	/**
	 * The seat choose by the user
	 */
	private long seat;

	public Ticket(Flight flight, String userName) {
		super();
		this.flight = flight;
		this.userName = userName;
	}

	/**
	 * The id of the ticket. It is generated summing the start time of the
	 * flight + the user name + the source city of the flight
	 * 
	 * @return The id of the ticket
	 */
	public String getId() {
		return flight.getStartTime().getTime() + userName + flight.getAddressFrom().getCity();
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getSeat() {
		return seat;
	}

	public void setSeat(long seat) {
		this.seat = seat;
	}

	@Override
	public String toString() {
		String decoration = "\n--------------------------\n";
		return decoration + "Boarding pass: " + getId() + decoration + flight + decoration + seat + decoration;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getId().hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ticket other = (Ticket) obj;
		if (!getId().equals(other.getId()))
			return false;
		return true;
	}

}
