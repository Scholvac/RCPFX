package de.sos.rcp.mgr;

import java.io.File;
import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.reactfx.EventSource;
import org.w3c.dom.events.UIEvent;

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
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.Stage;


/**
 * Pushes WindowManagerEvent, when a new IView or IEditor has been created
 * this allows the application to configure a new created instance 
 * <code>
 * getWindowManager().filter(e->{return e.clazz == ModelEditor.class;}).subscribe(s-> {
			ModelEditor me = (ModelEditor)s.element;
			... //do something with the editor
		});
 * </code>
 * @author sschweigert
 *
 */
public class WindowManager extends EventSource<WindowManager.WindowManagerEvent>{

	public enum WindowManagerEventType {
		OPEN_EDITOR, 
		OPEN_VIEW, 
		CLOSE_EDITOR,
		CLOSE_VIEW,
		ACTIVATE_EDITOR,
		DEACTIVATE_EDITOR
	}
	public static class WindowManagerEvent {
		public final WindowManagerEventType			eventType;
		public final Class<? extends IUIElement> 	clazz;
		public final String		 					type;
		public final IUIElement						element;
		public WindowManagerEvent(WindowManagerEventType evt, Class<? extends IUIElement> cl, String type, IUIElement element) {
			super();
			eventType = evt;
			this.clazz = cl;
			this.type = type;
			this.element = element;
		}
	}
	private class UIDescriptor {
		String						group;
		String						type;
	}
	private class ViewDescriptor extends UIDescriptor {
		Class<? extends IView>		clazz;
		DockPosition				preferedPosition = DockPosition.CENTER;
		
		@Override
		public String toString() {
			return "View: " + type + " Group: " + group + " Class: " + clazz;
		}
	}
	
	private class EditorDescriptor extends UIDescriptor {
		Class<? extends IEditor>		clazz;
		
		@Override
		public String toString() {
			return "Editor: " + type + " Group: " + group + " Class: " + clazz;
		}
	}

	private static final String OPEN_EDITOR_FILE_LIST 				= "WindowManager.OpenEditorFileList";
	private static final String WINDOW_LOCATION_PROPERTY_X 			= "WindowManager.Scene.Location.X";
	private static final String WINDOW_LOCATION_PROPERTY_Y 			= "WindowManager.Scene.Location.Y";
	private static final String WINDOW_LOCATION_PROPERTY_W 			= "WindowManager.Scene.Location.W";
	private static final String WINDOW_LOCATION_PROPERTY_H 			= "WindowManager.Scene.Location.H";
	
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
		
		RCPApplication.getInstance();
		MenuManager mmgr = RCPApplication.getMenuManager();
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
		
		RCPApplication.getInstance();
		List<String> openList = RCPApplication.getPropertyManager().get(OPEN_EDITOR_FILE_LIST, new ArrayList<String>());
		if (openList.isEmpty() == false){
			for (String str : openList){
				try{
					String[] tmp = str.split("#");
					String type = tmp[0];
					File file = new File(tmp[1]);
					openFileEditor(type, file);
				}catch(Exception e){
					RCPLog.error("Failed to load file: " + str + " with error: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
		double x = RCPApplication.getPropertyManager().get(WINDOW_LOCATION_PROPERTY_X, -1.0);
		double y = RCPApplication.getPropertyManager().get(WINDOW_LOCATION_PROPERTY_Y, -1.0);
		double w = RCPApplication.getPropertyManager().get(WINDOW_LOCATION_PROPERTY_W, 1024.0);
		double h = RCPApplication.getPropertyManager().get(WINDOW_LOCATION_PROPERTY_H, 768.0);
		
		if (x > 0 && y > 0){
			mPrimaryStage.setX(x);
			mPrimaryStage.setY(y);
		}
		mPrimaryStage.setWidth(w);
		mPrimaryStage.setHeight(h);
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
					WindowManagerEventType t = WindowManagerEventType.CLOSE_VIEW;
					if (element instanceof IEditor){
						t = WindowManagerEventType.CLOSE_EDITOR;
					}
					try{
						push(new WindowManagerEvent(t, element.getClass(), element.getType(), element));
					}catch(Exception e){
						RCPLog.error(e.getMessage());
						e.printStackTrace();
					}
				}else if (event.getEventType() == DockNodeEvent.BRING_TO_FRONT && element instanceof IEditor){
					WindowManagerEventType t = WindowManagerEventType.ACTIVATE_EDITOR;
					try{
						push(new WindowManagerEvent(t, element.getClass(), element.getType(), element));
					}catch(Exception e){
						RCPLog.error(e.getMessage());
						e.printStackTrace();
					}
				}else if (event.getEventType() == DockNodeEvent.BRING_TO_BACK && element instanceof IEditor){
					WindowManagerEventType t = WindowManagerEventType.DEACTIVATE_EDITOR;
					try{
						push(new WindowManagerEvent(t, element.getClass(), element.getType(), element));
					}catch(Exception e){
						RCPLog.error(e.getMessage());
						e.printStackTrace();
					}
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
			this.push(new WindowManagerEvent(WindowManagerEventType.OPEN_VIEW, vd.clazz, vd.type, view));
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
		ViewDescriptor ex = null;
		for (ViewDescriptor ex_vd : mViewDescriptors.get(group))
			if (ex_vd.type.equals(type)){
				ex = ex_vd;break;
			}
		if (ex != null){
			RCPLog.debug("Replace View Descriptor: " + ex + " with: " + vd);
			mViewDescriptors.get(group).remove(ex);
		}
		mViewDescriptors.get(group).add(vd);
	}
	public void registerEditor(Class<? extends IEditor> class1, String type, String group) {
		EditorDescriptor vd = new EditorDescriptor();
		vd.clazz = class1; vd.type = type; vd.group = group;
		if (mEditorDescriptors.get(group) == null)
			mEditorDescriptors.put(group, new ArrayList<>());
		EditorDescriptor ex = null;
		for (EditorDescriptor ex_vd : mEditorDescriptors.get(group))
			if (ex_vd.type.equals(type)){
				ex = ex_vd;break;
			}
		if (ex != null){
			RCPLog.debug("Replace Editor Descriptor: " + ex + " with: " + vd);
			mEditorDescriptors.get(group).remove(ex);
		}
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
			this.push(new WindowManagerEvent(WindowManagerEventType.OPEN_EDITOR, ed.clazz, ed.type, editor));
			
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
		RCPApplication.getInstance();
		RCPApplication.getPropertyManager().put(OPEN_EDITOR_FILE_LIST, fileEditors);
		
		RCPApplication.getPropertyManager().put(WINDOW_LOCATION_PROPERTY_X, mRootStation.getScene().getWindow().getX());
		RCPApplication.getPropertyManager().put(WINDOW_LOCATION_PROPERTY_Y, mRootStation.getScene().getWindow().getY());
		RCPApplication.getPropertyManager().put(WINDOW_LOCATION_PROPERTY_W, mRootStation.getScene().getWindow().getWidth());
		RCPApplication.getPropertyManager().put(WINDOW_LOCATION_PROPERTY_H, mRootStation.getScene().getWindow().getHeight());
	}

	public void dockEditor(DockNode dn) {
		dn.dock(mEditorStation, DockPosition.CENTER);
	}

}
