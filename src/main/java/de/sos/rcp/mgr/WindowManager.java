package de.sos.rcp.mgr;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.UIDefaults;

import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.node.DockNode.DockPosition;
import com.anchorage.docks.node.events.DockNodeEvent;
import com.anchorage.docks.stations.DockStation;
import com.anchorage.docks.stations.DockSubStation;
import com.anchorage.system.AnchorageSystem;

import de.sos.rcp.RCPApplication;
import de.sos.rcp.action.AbstractAction;
import de.sos.rcp.log.RCPLog;
import de.sos.rcp.ui.IEditor;
import de.sos.rcp.ui.IUIElement;
import de.sos.rcp.ui.IView;
import de.sos.rcp.ui.editor.AbstractFileEditor;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.Stage;

public class WindowManager {

	private class UIDescriptor {
		String						group;
		String						type;
	}
	private class ViewDescriptor extends UIDescriptor {
		Class<? extends IView>		clazz;
		DockPosition				preferedPosition = DockPosition.CENTER;
	}
	
	private class EditorDescriptor extends UIDescriptor {
		Class<? extends IEditor>		clazz;
	}

	private static final String OPEN_EDITOR_FILE_LIST = "WindowManager.OpenEditorFileList";
	
	private DockStation 								mRootStation;
	private DockSubStation 								mEditorStation;
	private Stage 										mPrimaryStage;
	
	private HashMap<String, List<ViewDescriptor>> 		mViewDescriptors = new HashMap<String, List<ViewDescriptor>>(); //[group, descriptor]
	private HashMap<String, List<EditorDescriptor>> 	mEditorDescriptors = new HashMap<String, List<EditorDescriptor>>(); //[group, descriptor]
	

	

	public void initialize(DockStation station, Stage primaryStage) {
		mRootStation = station;
		mPrimaryStage = primaryStage;
		mEditorStation = AnchorageSystem.createSubStation(station, "Editor");
		mEditorStation.disableDockCapability();
		mEditorStation.dock(station, DockPosition.CENTER);
		
		MenuManager mmgr = RCPApplication.getInstance().getMenuManager();
		//Register menus to open a new View
		for (String g : mViewDescriptors.keySet()){
			String path = "Windows.Views."+g;
			for (ViewDescriptor vd : mViewDescriptors.get(g)){
				mmgr.addMenuAction(path+"."+vd.type, new AbstractAction(vd.type) {
					@Override
					public void execute() {
						openView(vd);
					}
				});
			}
		}
		
		List<String> openList = RCPApplication.getInstance().getPropertyManager().get(OPEN_EDITOR_FILE_LIST, new ArrayList<String>());
		if (openList.isEmpty() == false){
			for (String str : openList){
				String[] tmp = str.split("#");
				String type = tmp[0];
				File file = new File(tmp[1]);
				openFileEditor(type, file);
			}
		}
		
	}
	
	protected DockNode createDockNode(IUIElement element){
		DockNode dock = AnchorageSystem.createDock(element.getTitle(), element.getFXNode(mPrimaryStage));
		dock.setUserData(element);
		element.setDockNode(dock);
		dock.addEventHandler(DockNodeEvent.ANY, new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				if (event.getEventType() == DockNodeEvent.DOCK_NODE_CLOSING) {
					element.onClose();
				}
			};
		});
		
		return dock;
	}

	private DockNode createDockNode(ViewDescriptor vd) {
		RCPLog.debug("Create new View: " + vd.type);
		try {
			IView view = vd.clazz.newInstance();
			if (view == null){
				RCPLog.error("Failed to create view instance: " + vd.type);
				return null;
			}
			DockNode dock = createDockNode(view);
			//TODO: register additional listener for activate/deactivate/focus/...
			return dock;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected void openView(ViewDescriptor vd) {
		DockNode dock = createDockNode(vd);
		if (dock != null)
			dock.dock(mRootStation, vd.preferedPosition);
	}

	public void registerView(Class<? extends IView> class1, String type, String group) {
		registerView(class1, type, group, DockPosition.CENTER);
	}
	public void registerView(Class<? extends IView> class1, String type, String group, DockPosition position) {
		ViewDescriptor vd = new ViewDescriptor();
		vd.clazz = class1; vd.type = type; vd.group = group;
		vd.preferedPosition = position;
		if (mViewDescriptors.get(group) == null)
			mViewDescriptors.put(group, new ArrayList<>());
		mViewDescriptors.get(group).add(vd);
	}
	public void registerEditor(Class<? extends IEditor> class1, String type, String group) {
		EditorDescriptor vd = new EditorDescriptor();
		vd.clazz = class1; vd.type = type; vd.group = group;
		if (mEditorDescriptors.get(group) == null)
			mEditorDescriptors.put(group, new ArrayList<>());
		mEditorDescriptors.get(group).add(vd);
	}

	public DockStation getRootStation() {
		return mRootStation;
	}

	public DockSubStation getEditorStation() {
		return mEditorStation;
	}

	public DockNode createDockNodeByType(String type) {
		return createDockNode(getViewDescriptionByType(type));
	}

	

	private ViewDescriptor getViewDescriptionByType(String type) {
		for (Entry<String, List<ViewDescriptor>> e : mViewDescriptors.entrySet()){
			for (ViewDescriptor vd : e.getValue())
				if (vd.type.equals(type))
					return vd;
		}
		return null;
	}

	public void openFileEditor(String edt, File file) {
		//create the instance
		try {
			RCPLog.info("Open File Editor: " + edt + " for file: " + file);
			EditorDescriptor ed = getEditorDescriptorByType(edt);
			IEditor editor = ed.clazz.newInstance();
			DockNode dn = createDockNode(editor);
			dn.dock(mEditorStation, DockPosition.CENTER);
			
			if (editor instanceof AbstractFileEditor){
				AbstractFileEditor afe = (AbstractFileEditor)editor;
				afe.setFile(file);
				afe.load();
			}
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private EditorDescriptor getEditorDescriptorByType(String edt) {
		for (Entry<String, List<EditorDescriptor>> e : mEditorDescriptors.entrySet()){
			for (EditorDescriptor ed : e.getValue())
				if (ed.type.equals(edt))
					return ed;
		}
		return null;
	}

	
	public void close() {
		List<String> fileEditors = new ArrayList<String>();
		for (DockNode dn : mEditorStation.getSubStation().getNodes()){
			if (dn.getUserData() != null && dn.getUserData() instanceof AbstractFileEditor){
				String f = ((AbstractFileEditor)dn.getUserData()).getFile().getAbsolutePath();
				String t = ((AbstractFileEditor)dn.getUserData()).getType();
				fileEditors.add(t+"#"+f);
			}
		}
		RCPApplication.getInstance().getPropertyManager().put(OPEN_EDITOR_FILE_LIST, fileEditors);
	}

	public void dockEditor(DockNode dn) {
		dn.dock(mEditorStation, DockPosition.CENTER);
	}

}
