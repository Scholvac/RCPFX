package de.sos.rcp.action;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.Node;

public abstract class AbstractAction implements IAction {

	private String mLabel = null;
	
	
	private ReadOnlyBooleanWrapper	mActive = new ReadOnlyBooleanWrapper(true);
	private ReadOnlyBooleanWrapper	mEnabled = new ReadOnlyBooleanWrapper(true);
	
	public AbstractAction(String label) {
		mLabel = label;
	}
	@Override
	public String getLabel() {
		return mLabel;
	}
	
	@Override
	public Node getIcon() {
		return null;
	}
	
	public void activate() {
		mActive.set(true);
	}
	public void deactivate(){
		mActive.set(false);
	}
	public void activate(boolean b) {
		if (b) activate();
		else deactivate();
	}
	@Override
	public ReadOnlyBooleanWrapper activeProperty(){ return mActive;}
	
	public void enable() {
		mEnabled.set(true);
	}
	public void disable(){
		mEnabled.set(false);
	}
	@Override
	public ReadOnlyBooleanWrapper enableProperty(){ return mEnabled;}
	public void setLabel(String string) {
		mLabel = string;		
	}
	
	

}
