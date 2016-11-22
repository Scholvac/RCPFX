package de.sos.rcp.mgr;

import org.reactfx.EventSource;

import de.sos.rcp.mgr.selection.Selection;


/**
 * The SelectionManager is responsible to distribute selections from various sources. 
 * For this purpose it uses the reactfx framework and acts as EventSource. 
 * To register a new listener use the Consumer<Selection> interface on the event source
 * @example
 * RCPApplication.getSelectionManager().subscribe(new Consumer<Selection>() {
		@Override
		public void accept(Selection t) {
			...
		}
	});
	
	OR with java 8+
	
	RCPApplication.getSelectionManager().subscribe(e->{
	...
	});
 * 
 * @author sschweigert
 *
 */
public class SelectionManager extends EventSource<Selection>{

		
	private Selection mLastSelection;

	public void initialize() {
	}

	public <T> void fireSelectionWithSource(Object source, T ...values){
		push(mLastSelection=new Selection<T>(source, values));
	}
	public <T> void fireSelection(T ...value) {
		push(mLastSelection = new Selection<T>(value));
	}

	public Selection last() {
		return mLastSelection;
	}
	
	

}
