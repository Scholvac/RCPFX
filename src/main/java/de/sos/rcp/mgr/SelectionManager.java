package de.sos.rcp.mgr;

import org.reactfx.EventSource;
import de.sos.rcp.mgr.selection.Selection;

public class SelectionManager extends EventSource<Selection>{

		
	private Selection mLastSelection;

	public void initialize() {
	}

	public <T> void fireSelection(T value) {
		push(mLastSelection = new Selection<T>(value));
	}

	public Selection last() {
		return mLastSelection;
	}
	
	

}
