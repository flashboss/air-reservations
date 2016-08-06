package it.vige.reservations.bpm;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import it.vige.reservations.PDFGenerator;
import it.vige.reservations.model.Ticket;

/**
 * Creates the boarding pass for the traveller
 * 
 * @author lucastancapiano
 *
 */
public class SetBoardingPass implements TaskListener {

	private static final long serialVersionUID = 7930555954056624168L;

	@Override
	public void notify(DelegateTask delegateTask) {
		Ticket ticket = (Ticket) delegateTask.getExecution().getVariable("ticket");

		PDFGenerator generator = new PDFGenerator(ticket);
		TaskService taskService = delegateTask.getExecution().getEngineServices().getTaskService();
		taskService.createAttachment("application/pdf", delegateTask.getId(), delegateTask.getProcessInstanceId(),
				"Boarding Pass", "Your Boarding Pass", generator.generateBoardingPass());
	}

}
