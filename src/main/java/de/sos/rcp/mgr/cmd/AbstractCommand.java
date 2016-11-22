package de.sos.rcp.mgr.cmd;

public abstract class AbstractCommand implements Command {
	private String mName = null;
	private UndoCommand	mUndoCmd = null;
	public AbstractCommand(String name, UndoCommand undo){
		mName = name; mUndoCmd = undo;
	}
	public AbstractCommand(String name){
		this(name, null);
	}
	public String getName(){ return mName; }
	public boolean canUndo(){ return mUndoCmd != null; }
	public UndoCommand getUndoCommand() { return mUndoCmd;}
	public void setUndoCommand(UndoCommand cmd){ mUndoCmd = cmd;}		
}