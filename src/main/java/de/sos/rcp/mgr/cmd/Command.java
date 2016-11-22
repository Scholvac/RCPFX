package de.sos.rcp.mgr.cmd;


public interface Command {
	
	public boolean execute();
	
	
	
	public static interface UndoCommand 
	{
		public void undo();
	}

	
}
