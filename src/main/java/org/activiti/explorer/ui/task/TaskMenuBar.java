package org.activiti.explorer.ui.task;

import static it.vige.reservations.DemoData.isAdmin;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.engine.impl.identity.Authentication.getAuthenticatedUserId;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.TASK_MENU_ARCHIVED;
import static org.activiti.explorer.Messages.TASK_MENU_INBOX;
import static org.activiti.explorer.Messages.TASK_MENU_INVOLVED;
import static org.activiti.explorer.Messages.TASK_MENU_QUEUED;
import static org.activiti.explorer.Messages.TASK_MENU_TASKS;
import static org.activiti.explorer.Messages.TASK_NEW;

import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.explorer.I18nManager;
import org.activiti.explorer.ViewManager;
import org.activiti.explorer.identity.LoggedInUser;
import org.activiti.explorer.ui.Images;
import org.activiti.explorer.ui.custom.ToolBar;
import org.activiti.explorer.ui.custom.ToolbarEntry;
import org.activiti.explorer.ui.custom.ToolbarEntry.ToolbarCommand;
import org.activiti.explorer.ui.custom.ToolbarPopupEntry;
import org.activiti.explorer.ui.task.data.ArchivedListQuery;
import org.activiti.explorer.ui.task.data.InboxListQuery;
import org.activiti.explorer.ui.task.data.InvolvedListQuery;
import org.activiti.explorer.ui.task.data.QueuedListQuery;
import org.activiti.explorer.ui.task.data.TasksListQuery;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * The menu bar which is shown when 'Tasks' is selected in the main menu.
 * 
 * @author Joram Barrez
 * @author Frederik Heremans
 */
public class TaskMenuBar extends ToolBar {

	private static final long serialVersionUID = 1L;

	public static final String ENTRY_TASKS = "tasks";
	public static final String ENTRY_INBOX = "inbox";
	public static final String ENTRY_QUEUED = "queued";
	public static final String ENTRY_INVOLVED = "involved";
	public static final String ENTRY_ARCHIVED = "archived";

	protected transient IdentityService identityService;
	protected ViewManager viewManager;
	protected I18nManager i18nManager;

	public TaskMenuBar() {
		identityService = getDefaultProcessEngine().getIdentityService();
		this.viewManager = get().getViewManager();
		this.i18nManager = get().getI18nManager();

		initItems();
		initActions();
	}

	protected void initItems() {
		setWidth("100%");
		boolean admin = isAdmin(getAuthenticatedUserId(), identityService);
		// TODO: the counts should be done later by eg a Refresher component

		// Inbox
		long inboxCount = new InboxListQuery().size();
		ToolbarEntry inboxEntry = addToolbarEntry(ENTRY_INBOX, i18nManager.getMessage(TASK_MENU_INBOX),
				new ToolbarCommand() {
					private static final long serialVersionUID = 6195232837389141525L;

					public void toolBarItemSelected() {
						viewManager.showInboxPage();
					}
				});
		inboxEntry.setCount(inboxCount);

		LoggedInUser user = get().getLoggedInUser();
		if (admin) {
			// Tasks
			long tasksCount = new TasksListQuery().size();
			ToolbarEntry tasksEntry = addToolbarEntry(ENTRY_TASKS, i18nManager.getMessage(TASK_MENU_TASKS),
					new ToolbarCommand() {
						private static final long serialVersionUID = -5816797607333588945L;

						public void toolBarItemSelected() {
							viewManager.showTasksPage();
						}
					});
			tasksEntry.setCount(tasksCount);
		}

		// Queued
		List<Group> groups = user.getGroups();
		ToolbarPopupEntry queuedItem = addPopupEntry(ENTRY_QUEUED, (i18nManager.getMessage(TASK_MENU_QUEUED)));
		long queuedCount = 0;
		for (final Group group : groups) {
			long groupCount = new QueuedListQuery(group.getId()).size();

			queuedItem.addMenuItem(group.getName() + " (" + groupCount + ")", new ToolbarCommand() {
				private static final long serialVersionUID = 7424607283050240484L;

				public void toolBarItemSelected() {
					viewManager.showQueuedPage(group.getId());
				}
			});

			queuedCount += groupCount;
		}
		queuedItem.setCount(queuedCount);

		if (admin) {
			// Involved
			long involvedCount = new InvolvedListQuery().size();
			ToolbarEntry involvedEntry = addToolbarEntry(ENTRY_INVOLVED, i18nManager.getMessage(TASK_MENU_INVOLVED),
					new ToolbarCommand() {
						private static final long serialVersionUID = -4654896325560940296L;

						public void toolBarItemSelected() {
							viewManager.showInvolvedPage();
						}
					});
			involvedEntry.setCount(involvedCount);

			// Archived
			long archivedCount = new ArchivedListQuery().size();
			ToolbarEntry archivedEntry = addToolbarEntry(ENTRY_ARCHIVED, i18nManager.getMessage(TASK_MENU_ARCHIVED),
					new ToolbarCommand() {
						private static final long serialVersionUID = 427779350846251705L;

						public void toolBarItemSelected() {
							viewManager.showArchivedPage();
						}
					});
			archivedEntry.setCount(archivedCount);
		}
	}

	protected void initActions() {
		if (isAdmin(getAuthenticatedUserId(), identityService)) {
			Button newCaseButton = new Button();
			newCaseButton.setCaption(i18nManager.getMessage(TASK_NEW));
			newCaseButton.setIcon(Images.TASK_16);
			addButton(newCaseButton);

			newCaseButton.addListener(new ClickListener() {
				private static final long serialVersionUID = 4669346991093165424L;

				public void buttonClick(ClickEvent event) {
					NewCasePopupWindow newTaskPopupWindow = new NewCasePopupWindow();
					viewManager.showPopupWindow(newTaskPopupWindow);
				}
			});
		}
	}

}
