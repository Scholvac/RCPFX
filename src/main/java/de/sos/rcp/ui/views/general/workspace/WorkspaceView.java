package de.sos.rcp.ui.views.general.workspace;

import java.io.File;

import com.anchorage.docks.node.DockNode.DockPosition;

import de.sos.rcp.RCPApplication;
import de.sos.rcp.mgr.WindowManager;
import de.sos.rcp.ui.impl.AbstractView;
import de.sos.rcp.ui.views.general.console.ConsoleView;
import javafx.scene.Node;
import javafx.stage.Stage;

public class WorkspaceView extends AbstractView 
{

	static void register(){
		register(RCPApplication.getInstance().getWindowManager());
	}
	
	public static void register(WindowManager windowManager) {
		windowManager.registerView(WorkspaceView.class, "Workspace", "General", DockPosition.LEFT);		
	}

	private WorkspaceControl mControl;
	
	public WorkspaceView() {
		super("Workspace", "Workspace");
	}
	
	@Override
	public Node getFXNode(Stage mPrimaryStage) {
		if (mControl == null)
			mControl = new WorkspaceControl(new File("workspace"));
		return mControl;
	}

}
