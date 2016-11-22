package de.sos.rcp.mgr;

import java.util.ArrayList;

import org.reactfx.EventSource;

import de.sos.rcp.RCPApplication;
import de.sos.rcp.log.RCPLog;
import de.sos.rcp.mgr.cmd.Command;
import javafx.application.Platform;
/**
 * Used to execute and publish commands
 * Commands will be stored to allow undo operations. 
 * How to use: 
 * create a new command and push it into the event source. The event source (e.g. the CommandManager) will take care that the command
 * is executed and published
 * @example: RCPApplication.getCommandManager().push(new Command(){ public void execute() { ... } });
 * @author sschweigert
 *
 */
public class CommandManager extends EventSource<Command>{

	
	private static ArrayList<Command>		mCommandList = new ArrayList<Command>();
	/**
	 * Sets the maximum number of stored commands
	 * if size < 0 an unlimited number of commands is stored
	 * default = 1000
	 */
	private static int 						mCommandListSize = 1000;
	
	/**
	 * Sets the maximum number of stored commands
	 * if size < 0 an unlimited number of commands is stored
	 * @param size
	 */
	public static void setCommandListSize(int size) { mCommandListSize = size; }
	public static int getCommandListSize(){ return mCommandListSize; }
	
	/**
	 * do the actual execution of the command 
	 * and notify all listeners if the command has been executed normaly
	 */
	@Override
	public void emit(Command value) {
		try{ 
			if (value.execute()){				
				mCommandList.add(value);
				if (mCommandListSize >= 0 && mCommandList.size() >= mCommandListSize)
					mCommandList.remove(0); //throw away the oldest command
				
	
		        super.emit(value);
			}
		}catch(Exception e){
			RCPLog.error("Failed to execute an command: " + value + " with error: " + e.getMessage());
			e.printStackTrace();
		}
    }
	/**
	 * Executes the command, if possible using the RCPApplication.getCommandManager instance. 
	 * if the instance is not set, the command is just executed
	 * @param abstractCommand
	 */
	public static void execute(Command cmd) {
		CommandManager cmdMgr = RCPApplication.getCommandManager();
		if (cmdMgr != null)
			if (Platform.isFxApplicationThread())
				cmdMgr.push(cmd);
			else
				Platform.runLater(new Runnable(){@Override
				public void run(){cmdMgr.push(cmd);}});
		else
			cmd.execute();
	}
	
}
