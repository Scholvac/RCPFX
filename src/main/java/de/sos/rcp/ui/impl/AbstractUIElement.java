package de.sos.rcp.ui.impl;

import java.util.UUID;

import com.anchorage.docks.node.DockNode;

import de.sos.rcp.ui.IUIElement;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

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
	public void onClose() {	}
	
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
