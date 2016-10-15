package de.sos.rcp.ui.impl;

import de.sos.rcp.RCPApplication;
import de.sos.rcp.mgr.PropertyManager;
import de.sos.rcp.ui.IView;

public abstract class AbstractView extends AbstractUIElement implements IView {

	public AbstractView(String title, String type, String uuid) {
		super(title, type, uuid);
	}

	public AbstractView(String title, String type) {
		super(title, type);
	}

	public AbstractView(String title) {
		super(title);
	}

	
	

	
}
