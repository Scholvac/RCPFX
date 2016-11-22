package de.sos.rcp.ui.impl;

import java.util.UUID;

import com.anchorage.docks.node.DockNode;

import de.sos.rcp.RCPApplication;
import de.sos.rcp.mgr.PropertyManager;
import de.sos.rcp.ui.IUIElement;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.stage.Stage;

public abstract class AbstractUIElement implements IUIElement
{
	private String mUUID;
	private String mType;
	private String mTitle;
	protected DockNode mDockNode;
	
	
	public AbstractUIElement(String title){
		this(title, null);
	}
	public AbstractUIElement(String title, String type) {
		this(title, type, UUID.randomUUID().toString());
	}
	public AbstractUIElement(String title,  String type, String uuid) {
		mTitle = title;
		mType = type;
		mUUID = uuid;
	}
	
	@Override
	public String getTitle() {
		return mTitle;
	}
	@Override
	public String getUniqueID() {
		return mUUID;
	}
	@Override
	public String getType() {
		return mType;
	}
	
	@Override
	public Node createFXNode(Stage primaryStage){
		Node n = getFXNode(primaryStage);
		if (n != null){
			PropertyManager memento = RCPApplication.getPropertyManager().get("UIElement."+getType()+"Properties", new PropertyManager(null));
			restoreProperties(memento);
		}
		return n;
	}

	
	protected abstract Node getFXNode(Stage primaryStage);
	
	/**
	 * Override this method to restore properties of the ui element
	 * @param memento ui type dependent block in the global memory
	 * @warn the memento is unique only for the type of uiElement, if different views of same type should be stored, that has to be done 
	 * by the UIElement itself
	 * @note this method is called direct after the javafx node has been created. 
	 */
	protected void restoreProperties(PropertyManager memento) {}
	
	@Override
	public void onClose() {	
		PropertyManager memento = RCPApplication.getPropertyManager().get("UIElement."+getType()+"Properties", new PropertyManager(null));
		saveProperties(memento);
	}
	/**
	 * Override this method to save the current state of the view and to restore it the next time
	 * @param memento view type dependend block in the global memory
	 * @warn the memento is unique only for the type of view, if different views of same type should be stored, that has to be done 
	 * by the view itself
	 */
	protected void saveProperties(PropertyManager memento) { }
	
	@Override
	public void setDockNode(DockNode dock) {
		mDockNode = dock;
		mDockNode.getContent().focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue)
					onGainFocus();
				else
					onLostFocus();	
			}
		});
	}

	public DockNode getDockNode(){ return mDockNode;}
	
	
	protected void onGainFocus() {
	}
	protected void onLostFocus() {
	}
}
