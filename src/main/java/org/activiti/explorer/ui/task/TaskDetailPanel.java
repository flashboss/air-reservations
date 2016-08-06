package org.activiti.explorer.ui.task;

import static com.vaadin.ui.Alignment.BOTTOM_RIGHT;
import static com.vaadin.ui.Alignment.MIDDLE_LEFT;
import static com.vaadin.ui.Label.CONTENT_XHTML;
import static com.vaadin.ui.themes.BaseTheme.BUTTON_LINK;
import static com.vaadin.ui.themes.Reindeer.LABEL_H2;
import static com.vaadin.ui.themes.Reindeer.LAYOUT_WHITE;
import static it.vige.reservations.DemoData.isAdmin;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.engine.impl.identity.Authentication.getAuthenticatedUserId;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.BUTTON_OK;
import static org.activiti.explorer.Messages.TASK_CLAIM;
import static org.activiti.explorer.Messages.TASK_COMPLETE;
import static org.activiti.explorer.Messages.TASK_COMPLETED;
import static org.activiti.explorer.Messages.TASK_CREATED_SHORT;
import static org.activiti.explorer.Messages.TASK_FORM_HELP;
import static org.activiti.explorer.Messages.TASK_NO_DESCRIPTION;
import static org.activiti.explorer.Messages.TASK_PART_OF_PROCESS;
import static org.activiti.explorer.Messages.TASK_RESET_FORM;
import static org.activiti.explorer.Messages.TASK_SUBTASK_OF_PARENT_TASK;
import static org.activiti.explorer.ui.Images.TASK_50;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_CLICKABLE;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_DETAIL_BLOCK;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_TASK_HEADER_CREATE_TIME;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_TITLE_BLOCK;

import java.util.Map;

import org.activiti.engine.FormService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.explorer.I18nManager;
import org.activiti.explorer.NotificationManager;
import org.activiti.explorer.ViewManager;
import org.activiti.explorer.ui.custom.DetailPanel;
import org.activiti.explorer.ui.custom.PrettyTimeLabel;
import org.activiti.explorer.ui.form.FormPropertiesEventListener;
import org.activiti.explorer.ui.form.FormPropertiesForm;
import org.activiti.explorer.ui.form.FormPropertiesForm.FormPropertiesEvent;
import org.activiti.explorer.ui.task.listener.ClaimTaskClickListener;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

/**
 * The central panel on the task page, showing all the details of a task.
 * 
 * @author Joram Barrez
 */
public class TaskDetailPanel extends DetailPanel {

	private static final long serialVersionUID = 1L;

	protected Task task;

	// Services
	protected transient TaskService taskService;
	protected transient FormService formService;
	protected transient RepositoryService repositoryService;
	protected ViewManager viewManager;
	protected I18nManager i18nManager;
	protected NotificationManager notificationManager;

	// UI
	protected TaskPage taskPage;
	protected VerticalLayout centralLayout;
	protected FormPropertiesForm taskForm;
	protected TaskInvolvedPeopleComponent involvedPeople;
	protected SubTaskComponent subTaskComponent;
	protected TaskRelatedContentComponent relatedContent;
	protected Button completeButton;
	protected Button claimButton;

	protected transient IdentityService identityService;

	public TaskDetailPanel(Task task, TaskPage taskPage) {
		identityService = getDefaultProcessEngine().getIdentityService();
		this.task = task;
		this.taskPage = taskPage;

		this.taskService = getDefaultProcessEngine().getTaskService();
		this.formService = getDefaultProcessEngine().getFormService();
		this.repositoryService = getDefaultProcessEngine().getRepositoryService();
		this.viewManager = get().getViewManager();
		this.i18nManager = get().getI18nManager();
		this.notificationManager = get().getNotificationManager();
	}

	@Override
	public void attach() {
		super.attach();
		init();
	}

	protected void init() {
		setSizeFull();
		addStyleName(LAYOUT_WHITE);

		// Central panel: all task data
		this.centralLayout = new VerticalLayout();
		centralLayout.setMargin(true);
		setDetailContainer(centralLayout);

		initHeader();
		initDescriptionAndClaimButton();
		initProcessLink();
		initParentTaskLink();
		initPeopleDetails();
		if (isAdmin(getAuthenticatedUserId(), identityService))
			initSubTasks();
		initRelatedContent();
		initTaskForm();

	}

	protected void initHeader() {
		GridLayout taskDetails = new GridLayout(2, 2);
		taskDetails.setWidth(100, UNITS_PERCENTAGE);
		taskDetails.addStyleName(STYLE_TITLE_BLOCK);
		taskDetails.setSpacing(true);
		taskDetails.setMargin(false, false, true, false);
		taskDetails.setColumnExpandRatio(1, 1.0f);
		centralLayout.addComponent(taskDetails);

		// Add image
		Embedded image = new Embedded(null, TASK_50);
		taskDetails.addComponent(image, 0, 0, 0, 1);

		// Add task name
		Label nameLabel = new Label(task.getName());
		nameLabel.addStyleName(LABEL_H2);
		taskDetails.addComponent(nameLabel, 1, 0);
		taskDetails.setComponentAlignment(nameLabel, MIDDLE_LEFT);

		// Properties
		HorizontalLayout propertiesLayout = new HorizontalLayout();
		propertiesLayout.setSpacing(true);
		taskDetails.addComponent(propertiesLayout);

		propertiesLayout.addComponent(new DueDateComponent(task, i18nManager, taskService));
		propertiesLayout.addComponent(new PriorityComponent(task, i18nManager, taskService));

		initCreateTime(propertiesLayout);
	}

	protected void initCreateTime(HorizontalLayout propertiesLayout) {
		PrettyTimeLabel createLabel = new PrettyTimeLabel(i18nManager.getMessage(TASK_CREATED_SHORT),
				task.getCreateTime(), "", true);
		createLabel.addStyleName(STYLE_TASK_HEADER_CREATE_TIME);
		propertiesLayout.addComponent(createLabel);
	}

	protected void initDescriptionAndClaimButton() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.addStyleName(STYLE_DETAIL_BLOCK);
		layout.setWidth(100, UNITS_PERCENTAGE);
		layout.setSpacing(true);
		centralLayout.addComponent(layout);

		initClaimButton(layout);
		initDescription(layout);
	}

	protected void initClaimButton(HorizontalLayout layout) {
		if (!isCurrentUserAssignee() && canUserClaimTask()) {
			claimButton = new Button(i18nManager.getMessage(TASK_CLAIM));
			claimButton.addListener(new ClaimTaskClickListener(task.getId(), taskService));
			layout.addComponent(claimButton);
			layout.setComponentAlignment(claimButton, MIDDLE_LEFT);
		}
	}

	protected void initDescription(HorizontalLayout layout) {
		final CssLayout descriptionLayout = new CssLayout();
		descriptionLayout.setWidth(100, UNITS_PERCENTAGE);
		layout.addComponent(descriptionLayout);
		layout.setExpandRatio(descriptionLayout, 1.0f);
		layout.setComponentAlignment(descriptionLayout, MIDDLE_LEFT);

		String descriptionText = null;
		if (task.getDescription() != null && !"".equals(task.getDescription())) {
			descriptionText = task.getDescription();
		} else {
			descriptionText = i18nManager.getMessage(TASK_NO_DESCRIPTION);
		}
		final Label descriptionLabel = new Label(descriptionText);
		descriptionLabel.addStyleName(STYLE_CLICKABLE);
		descriptionLayout.addComponent(descriptionLabel);

		descriptionLayout.addListener(new LayoutClickListener() {
			private static final long serialVersionUID = 4353520999904518740L;

			public void layoutClick(LayoutClickEvent event) {
				if (event.getClickedComponent() != null && event.getClickedComponent().equals(descriptionLabel)) {
					// layout for textarea + ok button
					final VerticalLayout editLayout = new VerticalLayout();
					editLayout.setSpacing(true);

					// textarea
					final TextArea descriptionTextArea = new TextArea();
					descriptionTextArea.setNullRepresentation("");
					descriptionTextArea.setWidth(100, UNITS_PERCENTAGE);
					descriptionTextArea.setValue(task.getDescription());
					editLayout.addComponent(descriptionTextArea);

					// ok button
					Button okButton = new Button(i18nManager.getMessage(BUTTON_OK));
					editLayout.addComponent(okButton);
					editLayout.setComponentAlignment(okButton, BOTTOM_RIGHT);

					// replace
					descriptionLayout.replaceComponent(descriptionLabel, editLayout);

					// When OK is clicked -> update task data + ui
					okButton.addListener(new ClickListener() {
						private static final long serialVersionUID = -1167130371098575331L;

						public void buttonClick(ClickEvent event) {
							// Update data
							task.setDescription(descriptionTextArea.getValue().toString());
							taskService.saveTask(task);

							// Update UI
							descriptionLabel.setValue(task.getDescription());
							descriptionLayout.replaceComponent(editLayout, descriptionLabel);
						}
					});
				}
			}
		});
	}

	protected void initProcessLink() {
		if (task.getProcessInstanceId() != null) {
			ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
					.processDefinitionId(task.getProcessDefinitionId()).singleResult();

			Button showProcessInstanceButton = new Button(
					i18nManager.getMessage(TASK_PART_OF_PROCESS, getProcessDisplayName(processDefinition)));
			showProcessInstanceButton.addStyleName(BUTTON_LINK);
			showProcessInstanceButton.addListener(new ClickListener() {
				private static final long serialVersionUID = -5810099164511027713L;

				public void buttonClick(ClickEvent event) {
					viewManager.showMyProcessInstancesPage(task.getProcessInstanceId());
				}
			});

			centralLayout.addComponent(showProcessInstanceButton);
			addEmptySpace(centralLayout);
		}
	}

	protected String getProcessDisplayName(ProcessDefinition processDefinition) {
		if (processDefinition.getName() != null) {
			return processDefinition.getName();
		} else {
			return processDefinition.getKey();
		}
	}

	protected void initParentTaskLink() {
		if (task.getParentTaskId() != null) {
			final Task parentTask = taskService.createTaskQuery().taskId(task.getParentTaskId()).singleResult();

			Button showParentTaskButton = new Button(
					i18nManager.getMessage(TASK_SUBTASK_OF_PARENT_TASK, parentTask.getName()));
			showParentTaskButton.addStyleName(BUTTON_LINK);
			showParentTaskButton.addListener(new ClickListener() {
				private static final long serialVersionUID = 8400051793540470618L;

				public void buttonClick(ClickEvent event) {
					viewManager.showTaskPage(parentTask.getId());
				}
			});

			centralLayout.addComponent(showParentTaskButton);
			addEmptySpace(centralLayout);
		}
	}

	protected void initPeopleDetails() {
		involvedPeople = new TaskInvolvedPeopleComponent(task, this);
		centralLayout.addComponent(involvedPeople);
	}

	protected void initSubTasks() {
		subTaskComponent = new SubTaskComponent(task);
		centralLayout.addComponent(subTaskComponent);
	}

	protected void initRelatedContent() {
		relatedContent = new TaskRelatedContentComponent(task, this);
		centralLayout.addComponent(relatedContent);
	}

	protected void initTaskForm() {
		// Check if task requires a form
		TaskFormData formData = formService.getTaskFormData(task.getId());
		if (formData != null && formData.getFormProperties() != null && !formData.getFormProperties().isEmpty()) {
			taskForm = new FormPropertiesForm();
			taskForm.setSubmitButtonCaption(i18nManager.getMessage(TASK_COMPLETE));
			taskForm.setCancelButtonCaption(i18nManager.getMessage(TASK_RESET_FORM));
			taskForm.setFormHelp(i18nManager.getMessage(TASK_FORM_HELP));
			taskForm.setFormProperties(formData.getFormProperties());

			taskForm.addListener(new FormPropertiesEventListener() {

				private static final long serialVersionUID = -3893467157397686736L;

				@Override
				protected void handleFormSubmit(FormPropertiesEvent event) {
					Map<String, String> properties = event.getFormProperties();
					formService.submitTaskFormData(task.getId(), properties);
					notificationManager.showInformationNotification(TASK_COMPLETED, task.getName());
					taskPage.refreshSelectNext();
				}

				@Override
				protected void handleFormCancel(FormPropertiesEvent event) {
					// Clear the form values
					taskForm.clear();
				}
			});
			// Only if current user is task's assignee
			taskForm.setEnabled(isCurrentUserAssignee());

			// Add component to page
			centralLayout.addComponent(taskForm);
		} else {
			// Just add a button to complete the task
			// TODO: perhaps move to a better place

			CssLayout buttonLayout = new CssLayout();
			buttonLayout.addStyleName(STYLE_DETAIL_BLOCK);
			buttonLayout.setWidth(100, UNITS_PERCENTAGE);
			centralLayout.addComponent(buttonLayout);

			completeButton = new Button(i18nManager.getMessage(TASK_COMPLETE));

			completeButton.addListener(new ClickListener() {

				private static final long serialVersionUID = 1L;

				public void buttonClick(ClickEvent event) {
					// If no owner, make assignee owner (will go into archived
					// then)
					if (task.getOwner() == null) {
						task.setOwner(task.getAssignee());
						taskService.setOwner(task.getId(), task.getAssignee());
					}

					taskService.complete(task.getId());
					notificationManager.showInformationNotification(TASK_COMPLETED, task.getName());
					taskPage.refreshSelectNext();
				}
			});

			completeButton.setEnabled(isCurrentUserAssignee() || isCurrentUserOwner());
			buttonLayout.addComponent(completeButton);
		}
	}

	protected boolean isCurrentUserAssignee() {
		String currentUser = get().getLoggedInUser().getId();
		return currentUser.equals(task.getAssignee());
	}

	protected boolean isCurrentUserOwner() {
		String currentUser = get().getLoggedInUser().getId();
		return currentUser.equals(task.getOwner());
	}

	protected boolean canUserClaimTask() {
		return taskService.createTaskQuery().taskCandidateUser(get().getLoggedInUser().getId()).taskId(task.getId())
				.count() == 1;
	}

	protected void addEmptySpace(ComponentContainer container) {
		Label emptySpace = new Label("&nbsp;", CONTENT_XHTML);
		emptySpace.setSizeUndefined();
		container.addComponent(emptySpace);
	}

	public void notifyPeopleInvolvedChanged() {
		involvedPeople.refreshPeopleGrid();
		taskPage.getTaskEventPanel().refreshTaskEvents();
	}

	public void notifyAssigneeChanged() {
		if (get().getLoggedInUser().getId().equals(task.getAssignee())) { // switch
																			// view
																			// to
																			// inbox
																			// if
																			// assignee
																			// is
																			// current
																			// user
			viewManager.showInboxPage(task.getId());
		} else {
			involvedPeople.refreshAssignee();
			taskPage.getTaskEventPanel().refreshTaskEvents();
		}
	}

	public void notifyOwnerChanged() {
		if (get().getLoggedInUser().getId().equals(task.getOwner())) { // switch
																		// view
																		// to
																		// tasks
																		// if
																		// owner
																		// is
																		// current
																		// user
			viewManager.showTasksPage(task.getId());
		} else {
			involvedPeople.refreshOwner();
			taskPage.getTaskEventPanel().refreshTaskEvents();
		}
	}

	public void notifyRelatedContentChanged() {
		relatedContent.refreshTaskAttachments();
		taskPage.getTaskEventPanel().refreshTaskEvents();
	}

}
