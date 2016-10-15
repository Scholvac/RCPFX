package de.sos.rcp.ui;

import com.anchorage.docks.node.DockNode;

import javafx.scene.Node;
import javafx.stage.Stage;

public interface IUIElement 
{
	String getTitle();
	String getUniqueID();
	String getType();
	
	Node createFXNode(Stage primaryStage);
	
	void onClose();
	
	void setDockNode(DockNode dock);
}
