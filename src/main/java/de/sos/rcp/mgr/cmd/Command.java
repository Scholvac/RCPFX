package de.sos.rcp.mgr.cmd;

import de.sos.rcp.RCPApplication;
import de.sos.rcp.log.RCPLog;
import de.sos.rcp.mgr.CommandManager;


public interface Command {
	
	public boolean execute();
	
	
	
	public static interface UndoCommand 
	{
		public void undo();
	}

	
}
