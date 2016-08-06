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
