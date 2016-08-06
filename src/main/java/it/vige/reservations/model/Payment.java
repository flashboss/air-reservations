package it.vige.reservations.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The payment. It contains all the choose flights by the user, the info of the
 * payment and resulting prize
 * 
 * @author lucastancapiano
 *
 */
public class Payment implements Serializable {

	private static final long serialVersionUID = -6777270032875331795L;

	/**
	 * When the payment is done
	 */
	private Date date;
	/**
	 * The credit card number of the payer
	 */
	private String creditCard;
	/**
	 * Th elast thrre codes of the credit card of the payer
	 */
	private String lastThreeCode;
	/**
	 * The choose flights by the user
	 */
	private List<Flight> flights = new ArrayList<Flight>();
	/**
	 * The user name of the payer
	 */
	private String userName;

	public Payment(List<Flight> flights) {
		super();
		this.flights = flights;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(String creditCard) {
		this.creditCard = creditCard;
	}

	public String getLastThreeCode() {
		return lastThreeCode;
	}

	public void setLastThreeCode(String lastThreeCode) {
		this.lastThreeCode = lastThreeCode;
	}

	public List<Flight> getFlights() {
		return flights;
	}

	public void setFlights(List<Flight> flights) {
		this.flights = flights;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * The final prize obtained summing all prizes of the choose flights
	 * 
	 * @return The final prize
	 */
	public double getPrize() {
		return flights.stream().mapToDouble(p -> p.getPrize()).sum();
	}

}
