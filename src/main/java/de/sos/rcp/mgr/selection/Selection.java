package de.sos.rcp.mgr.selection;

import org.controlsfx.control.PropertySheet.Item;

/**
 * Describes a selected element, that has somewhere been selected and most probabliy been published through the 
 * SelectionManager. 
 * A selection could have an optional source object that emits the selection and an unlimited number of selected elements. 
 * @author sschweigert
 *
 * @param <T>
 */
public class Selection<T> {

	private T[] 		mValues;
	private Object		mSource;
	
	public Selection(Object source, T... value){
		mValues = value;
		mSource = source;
	}
	
	public Selection(T... value){
		this(null, value);
	}
	
	public boolean isEmpty() { return mValues == null || mValues.length == 0; }
	public boolean empty() { return isEmpty(); } //c-style
	
	public Object getSource() { return mSource;} //may be null
	public Object source(){return getSource();} //c-style
	
	public T firstValue() { return isEmpty() ? null : mValues[0];}
	public T lastValue() { return isEmpty() ? null : mValues[mValues.length-1];}
	
	public T value(int idx){ 
		if (isEmpty() || idx < 0 || idx > mValues.length)
			return null;
		return mValues[idx];
	}

	public T[] getValues() {
		return mValues;
	}
	
}
