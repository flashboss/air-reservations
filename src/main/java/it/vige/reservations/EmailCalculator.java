package it.vige.reservations;

import static org.activiti.engine.impl.context.Context.getProcessEngineConfiguration;

import java.io.Serializable;

import org.activiti.engine.IdentityService;

public class EmailCalculator implements Serializable {

	private static final long serialVersionUID = -6950214335354061770L;

	public String getEmail(String userName) {
		IdentityService identityService = getProcessEngineConfiguration().getIdentityService();
		return identityService.getUserInfo(userName, "email");
	}
}
