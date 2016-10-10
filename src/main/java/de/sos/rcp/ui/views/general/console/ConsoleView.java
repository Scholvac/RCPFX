package de.sos.rcp.ui.views.general.console;

import com.anchorage.docks.node.DockNode.DockPosition;

import de.sos.rcp.RCPApplication;
import de.sos.rcp.mgr.WindowManager;
import de.sos.rcp.ui.IView;
import de.sos.rcp.ui.impl.AbstractView;
import javafx.scene.Node;
import javafx.stage.Stage;

public class ConsoleView extends AbstractView implements IView {
	
	
	static void register(){
		RCPApplication.getInstance();
		register(RCPApplication.getWindowManager());
	}
	
	public static void register(WindowManager windowManager) {
		windowManager.registerView(ConsoleView.class, "Console", "General", DockPosition.BOTTOM);		
	}

	private ConsoleControl		mControl = null;
	
	public ConsoleView(){
		super("Console", "Console");
	}

	@Override
	public Node getFXNode(Stage mPrimaryStage) {
		if (mControl == null){
			mControl = new ConsoleControl();
		}
		return mControl;
	}
	
	
}
