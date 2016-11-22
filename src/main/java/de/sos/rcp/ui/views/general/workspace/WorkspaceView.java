package de.sos.rcp.ui.views.general.workspace;

import java.io.File;

import com.anchorage.docks.node.DockNode.DockPosition;
import de.sos.rcp.RCPApplication;
import de.sos.rcp.mgr.WindowManager;
import de.sos.rcp.ui.impl.AbstractView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

public class WorkspaceView extends AbstractView 
{

	static void register(){
		RCPApplication.getInstance();
		register(RCPApplication.getWindowManager());
	}
	
	public static void register(WindowManager windowManager) {
		windowManager.registerView(WorkspaceView.class, "Workspace", "General", DockPosition.LEFT);		
	}

	private static WorkspaceView	theInstance = null;
	public static WorkspaceView getInstance() { return theInstance; }
	
	private WorkspaceControl mControl;
	private ObjectProperty<File>	mRootDirectory = new SimpleObjectProperty<>(new File("workspace"));
	
	
	public WorkspaceView() {
		super("Workspace", "Workspace");
		theInstance = this;
	}
	
	public ObjectProperty<File> rootDirectory() { return mRootDirectory; }
	
	@Override
	public Node getFXNode(Stage mPrimaryStage) {
		if (mControl == null){
			mControl = new WorkspaceControl(rootDirectory());
			mControl.getTreeView().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<File>>() {
				@Override
				public void changed(ObservableValue<? extends TreeItem<File>> observable, TreeItem<File> oldValue, TreeItem<File> newValue) {
					RCPApplication.getSelectionManager().fireSelection(newValue.getValue());
				}
			});
		}
		return mControl;
	}

	

}
