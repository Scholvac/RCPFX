package de.sos.rcp.action;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.Node;

public interface IAction {
	
	public String getLabel();
	public void execute();
	
	public ReadOnlyBooleanWrapper activeProperty();
	public ReadOnlyBooleanWrapper enableProperty();
	public Node getIcon();
}
