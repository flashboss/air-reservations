package org.activiti.explorer.ui;

import static com.vaadin.ui.themes.Reindeer.SPLITPANEL_SMALL;
import static it.vige.reservations.DemoData.isAdmin;
import static org.activiti.engine.ProcessEngines.getDefaultProcessEngine;
import static org.activiti.engine.impl.identity.Authentication.getAuthenticatedUserId;

import org.activiti.engine.IdentityService;
import org.activiti.explorer.ui.custom.ToolBar;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;

/**
 * Superclass for all Explorer pages
 * 
 * @author Joram Barrez
 * @author Frederik Heremans
 */
public abstract class AbstractPage extends CustomComponent {

	private static final long serialVersionUID = 1L;

	protected ToolBar toolBar;
	protected GridLayout grid;
	protected AbstractSelect select;
	protected boolean showEvents;

	protected transient IdentityService identityService;

	// Overriding attach(), so we can construct the components first, before the
	// UI is built,
	// that way, all member fields of subclasses are initialized properly
	@Override
	public void attach() {
		initUi();
	}

	/**
	 * Override this method (and call super()) when you want to influence the
	 * UI.
	 */
	protected void initUi() {

		identityService = getDefaultProcessEngine().getIdentityService();
		showEvents = getEventComponent() != null;

		addMainLayout();
		setSizeFull();
		addMenuBar();
		if (isAdmin(getAuthenticatedUserId(), identityService))
			addSearch();
		addSelectComponent();
		if (showEvents) {
			addEventComponent();
		}
	}

	protected void addEventComponent() {
		grid.addComponent(getEventComponent(), 2, 0, 2, 2);
	}

	/**
	 * Subclasses are expected to provide their own menuBar.
	 */
	protected void addMenuBar() {

		// Remove any old menu bar
		String activeEntry = null;
		if (toolBar != null) {
			activeEntry = toolBar.getCurrentEntryKey();
			grid.removeComponent(toolBar);
		}

		// Create menu bar
		ToolBar menuBar = createMenuBar();
		if (menuBar != null) {
			toolBar = createMenuBar();
			grid.addComponent(toolBar, 0, 0, 1, 0);

			if (activeEntry != null) {
				toolBar.setActiveEntry(activeEntry);
			}
		}
	}

	public ToolBar getToolBar() {
		return toolBar;
	}

	protected abstract ToolBar createMenuBar();

	protected void addMainLayout() {
		if (showEvents) {
			grid = new GridLayout(3, 3);
			grid.setColumnExpandRatio(0, .25f);
			grid.setColumnExpandRatio(1, .52f);
			grid.setColumnExpandRatio(2, .23f);
		} else {
			grid = new GridLayout(2, 3);

			grid.setColumnExpandRatio(0, .25f);
			grid.setColumnExpandRatio(1, .75f);
		}

		grid.addStyleName(SPLITPANEL_SMALL);
		grid.setSizeFull();

		// Height division
		grid.setRowExpandRatio(2, 1.0f);

		setCompositionRoot(grid);
	}

	protected void addSearch() {
		Component searchComponent = getSearchComponent();
		if (searchComponent != null) {
			grid.addComponent(searchComponent, 0, 1);
		}
	}

	protected void addSelectComponent() {
		AbstractSelect select = createSelectComponent();
		if (select != null) {
			grid.addComponent(select, 0, 2);
		}
	}

	/**
	 * Returns an implementation of {@link AbstractSelect}, which will be
	 * displayed on the left side of the page, allowing to select elements from
	 * eg. a list, tree, etc.
	 */
	protected abstract AbstractSelect createSelectComponent();

	/**
	 * Refreshes the elements of the list, and selects the next one (useful when
	 * the selected element is deleted).
	 */
	public abstract void refreshSelectNext();

	/**
	 * Select a specific element from the selection component.
	 */
	public abstract void selectElement(int index);

	protected void setDetailComponent(Component detail) {
		if (grid.getComponent(1, 1) != null) {
			grid.removeComponent(1, 1);
		}
		if (detail != null) {
			grid.addComponent(detail, 1, 1, 1, 2);
		}
	}

	protected Component getDetailComponent() {
		return grid.getComponent(1, 0);
	}

	/**
	 * Override to get the search component to display above the table. Return
	 * null when no search should be displayed.
	 */
	public Component getSearchComponent() {
		return null;
	}

	/**
	 * Get the component to display the events in.
	 * 
	 * Return null by default: no event-component will be used, in that case the
	 * main UI will be two columns instead of three.
	 * 
	 * Override in case the event component must be shown: three columns will be
	 * used then.
	 */
	protected Component getEventComponent() {
		return null;
	}

}
