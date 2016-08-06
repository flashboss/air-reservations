package org.activiti.explorer.ui.mainlayout;

import static com.vaadin.ui.Alignment.TOP_CENTER;
import static com.vaadin.ui.Alignment.TOP_RIGHT;
import static it.vige.reservations.DemoData.isAdmin;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.engine.impl.identity.Authentication.getAuthenticatedUserId;
import static org.activiti.explorer.ExplorerApp.get;
import static org.activiti.explorer.Messages.HEADER_LOGOUT;
import static org.activiti.explorer.Messages.MAIN_MENU_MANAGEMENT;
import static org.activiti.explorer.Messages.MAIN_MENU_PROCESS;
import static org.activiti.explorer.Messages.MAIN_MENU_REPORTS;
import static org.activiti.explorer.Messages.MAIN_MENU_TASKS;
import static org.activiti.explorer.Messages.PASSWORD_CHANGE;
import static org.activiti.explorer.Messages.PROFILE_EDIT;
import static org.activiti.explorer.Messages.PROFILE_SHOW;
import static org.activiti.explorer.ViewManager.MAIN_NAVIGATION_MANAGE;
import static org.activiti.explorer.ViewManager.MAIN_NAVIGATION_PROCESS;
import static org.activiti.explorer.ViewManager.MAIN_NAVIGATION_REPORT;
import static org.activiti.explorer.ViewManager.MAIN_NAVIGATION_TASK;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_ACTIVE;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_APPLICATION_LOGO;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_HEADER_PROFILE_BOX;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_HEADER_PROFILE_MENU;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_MAIN_MENU_BUTTON;
import static org.activiti.explorer.ui.mainlayout.ExplorerLayout.STYLE_WORKFLOW_CONSOLE_LOGO;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.explorer.Environments;
import org.activiti.explorer.I18nManager;
import org.activiti.explorer.ViewManager;
import org.activiti.explorer.identity.LoggedInUser;
import org.activiti.explorer.ui.Images;
import org.activiti.explorer.ui.profile.ChangePasswordPopupWindow;

import com.vaadin.terminal.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.Reindeer;

/**
 * @author Joram Barrez
 * @author Frederik Heremans
 */
@SuppressWarnings("serial")
public class MainMenuBar extends HorizontalLayout {

	private static final long serialVersionUID = 1L;

	protected ViewManager viewManager;
	protected I18nManager i18nManager;
	protected Map<String, Button> menuItemButtons;
	protected String currentMainNavigation;

	protected transient IdentityService identityService;

	public MainMenuBar() {
		identityService = getDefaultProcessEngine().getIdentityService();
		this.viewManager = get().getViewManager();
		this.i18nManager = get().getI18nManager();

		menuItemButtons = new HashMap<String, Button>();
		init();
	}

	/**
	 * Highlights the given main navigation in the menubar.
	 */
	public synchronized void setMainNavigation(String navigation) {
		if (currentMainNavigation != null) {
			menuItemButtons.get(currentMainNavigation).removeStyleName(STYLE_ACTIVE);
		}
		currentMainNavigation = navigation;

		Button current = menuItemButtons.get(navigation);
		if (current != null) {
			current.addStyleName(STYLE_ACTIVE);
		}
	}

	protected void init() {
		setHeight(54, UNITS_PIXELS);
		setWidth(100, UNITS_PERCENTAGE);

		setMargin(false, true, false, false);

		initTitle();
		initButtons();
		initProfileButton();
	}

	protected void initButtons() {

		// TODO: fixed widths based on i18n strings?
		Button taskButton = addMenuButton(MAIN_NAVIGATION_TASK, i18nManager.getMessage(MAIN_MENU_TASKS),
				Images.MAIN_MENU_TASKS, false, 0);
		taskButton.addListener(new ShowTasksClickListener());
		menuItemButtons.put(MAIN_NAVIGATION_TASK, taskButton);

		if (isAdmin(getAuthenticatedUserId(), identityService)) {
			taskButton.setWidth(80, UNITS_PIXELS);
			Button processButton = addMenuButton(MAIN_NAVIGATION_PROCESS, i18nManager.getMessage(MAIN_MENU_PROCESS),
					Images.MAIN_MENU_PROCESS, false, 80);
			processButton.addListener(new ShowProcessDefinitionsClickListener());
			menuItemButtons.put(MAIN_NAVIGATION_PROCESS, processButton);

			Button reportingButton = addMenuButton(MAIN_NAVIGATION_REPORT, i18nManager.getMessage(MAIN_MENU_REPORTS),
					Images.MAIN_MENU_REPORTS, false, 80);
			reportingButton.addListener(new ShowReportsClickListener());
			menuItemButtons.put(MAIN_NAVIGATION_REPORT, reportingButton);

			if (get().getLoggedInUser().isAdmin()) {
				Button manageButton = addMenuButton(MAIN_NAVIGATION_MANAGE,
						i18nManager.getMessage(MAIN_MENU_MANAGEMENT), Images.MAIN_MENU_MANAGE, false, 90);
				manageButton.addListener(new ShowManagementClickListener());
				menuItemButtons.put(MAIN_NAVIGATION_MANAGE, manageButton);
			}
		}
	}

	protected void initTitle() {
		Label title = new Label();
		title.addStyleName(Reindeer.LABEL_H1);

		if (get().getEnvironment().equals(Environments.ALFRESCO)) {
			title.addStyleName(STYLE_WORKFLOW_CONSOLE_LOGO);
		} else {
			title.addStyleName(STYLE_APPLICATION_LOGO);
		}

		addComponent(title);

		setExpandRatio(title, 1.0f);
	}

	protected Button addMenuButton(String type, String label, Resource icon, boolean active, float width) {
		Button button = new Button(label);
		button.addStyleName(type);
		button.addStyleName(STYLE_MAIN_MENU_BUTTON);
		button.addStyleName(Reindeer.BUTTON_LINK);
		button.setHeight(54, UNITS_PIXELS);
		button.setIcon(icon);
		button.setWidth(width, UNITS_PIXELS);

		addComponent(button);
		setComponentAlignment(button, TOP_CENTER);

		return button;
	}

	protected void initProfileButton() {
		final LoggedInUser user = get().getLoggedInUser();

		// User name + link to profile
		MenuBar profileMenu = new MenuBar();
		profileMenu.addStyleName(STYLE_HEADER_PROFILE_BOX);
		MenuItem rootItem = profileMenu.addItem(user.getFirstName() + " " + user.getLastName(), null);
		rootItem.setStyleName(STYLE_HEADER_PROFILE_MENU);

		if (useProfile()) {
			// Show profile
			rootItem.addItem(i18nManager.getMessage(PROFILE_SHOW), new Command() {
				public void menuSelected(MenuItem selectedItem) {
					get().getViewManager().showProfilePopup(user.getId());
				}
			});

			// Edit profile
			rootItem.addItem(i18nManager.getMessage(PROFILE_EDIT), new Command() {

				public void menuSelected(MenuItem selectedItem) {
					// TODO: Show in edit-mode
					get().getViewManager().showProfilePopup(user.getId());
				}
			});

			// Change password
			rootItem.addItem(i18nManager.getMessage(PASSWORD_CHANGE), new Command() {
				public void menuSelected(MenuItem selectedItem) {
					get().getViewManager().showPopupWindow(new ChangePasswordPopupWindow());
				}
			});

			rootItem.addSeparator();
		}

		// Logout
		rootItem.addItem(i18nManager.getMessage(HEADER_LOGOUT), new Command() {
			public void menuSelected(MenuItem selectedItem) {
				get().close();
			}
		});

		addComponent(profileMenu);
		setComponentAlignment(profileMenu, TOP_RIGHT);
		setExpandRatio(profileMenu, 1.0f);
	}

	protected boolean useProfile() {
		return true;
	}

	// Listener classes
	private class ShowTasksClickListener implements ClickListener {
		public void buttonClick(ClickEvent event) {
			get().getViewManager().showInboxPage();
		}
	}

	private class ShowProcessDefinitionsClickListener implements ClickListener {
		public void buttonClick(ClickEvent event) {
			get().getViewManager().showDeployedProcessDefinitionPage();
		}
	}

	private class ShowReportsClickListener implements ClickListener {
		public void buttonClick(ClickEvent event) {
			get().getViewManager().showRunReportPage();
		}
	}

	private class ShowManagementClickListener implements ClickListener {
		public void buttonClick(ClickEvent event) {
			get().getViewManager().showDatabasePage();
		}
	}
}
