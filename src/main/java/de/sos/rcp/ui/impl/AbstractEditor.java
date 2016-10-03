package de.sos.rcp.ui.impl;

import de.sos.rcp.ui.IEditor;
import javafx.scene.Node;
import javafx.stage.Stage;

public abstract class AbstractEditor extends AbstractUIElement implements IEditor {

	public AbstractEditor(String title, String type, String uuid) {
		super(title, type, uuid);
	}

	public AbstractEditor(String title, String type) {
		super(title, type);
	}

	public AbstractEditor(String title) {
		super(title);
	}

	

}
