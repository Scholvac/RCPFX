package de.sos.rcp.mgr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Optional;

import com.anchorage.docks.layout.INodeDeserializer;
import com.anchorage.docks.layout.INodeSerializer;
import com.anchorage.docks.layout.LayoutData;
import com.anchorage.docks.layout.LayoutReader;
import com.anchorage.docks.layout.LayoutWriter;
import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.stations.DockStation;
import com.thoughtworks.xstream.XStream;

import de.sos.rcp.RCPApplication;
import de.sos.rcp.action.AbstractAction;
import de.sos.rcp.log.RCPLog;
import de.sos.rcp.ui.IEditor;
import de.sos.rcp.ui.IView;
import javafx.scene.control.TextInputDialog;

public class LayoutManager implements INodeSerializer, INodeDeserializer {

	
	private static final String CURRENT_LAYOUT_PROPERTY 				= "LayoutManager.CurrentLayout";
	private static final String LAYOUT_DIRECTORY_PROPERTY 				= "LayoutManager.Directory";
	private File 	mLayoutDirectory;
	private File	mCurrentLayout = null;



	public void initialize() {
		RCPApplication.getInstance();
		mLayoutDirectory = RCPApplication.getPropertyManager().get(LAYOUT_DIRECTORY_PROPERTY, new File("layouts"));
		reloadLayouts();
		RCPApplication.getInstance();
		RCPApplication.getMenuManager().addMenuAction("Windows.Layouts.Save", new AbstractAction("Save Layout") {
			@Override
			public void execute() {
				saveCurrentLayout();
				reloadLayouts();
			}
		});
		RCPApplication.getInstance();
		if (RCPApplication.getPropertyManager().hasProperty(CURRENT_LAYOUT_PROPERTY)){
			RCPApplication.getInstance();
			openLayout(RCPApplication.getPropertyManager().get(CURRENT_LAYOUT_PROPERTY));
		}
	}
	


	void reloadLayouts(){
		
		String[] files = mLayoutDirectory.list(new FilenameFilter() {			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".layout");
			}
		});
		if (files != null){
			for (String n : files){
				File f = new File(mLayoutDirectory + "/" + n);
				n = n.replace(".layout", "");
				RCPApplication.getInstance();
				RCPApplication.getMenuManager().addMenuAction("Windows.Layouts."+n, new AbstractAction("Open Layout") {
					@Override
					public void execute() {
						openLayout(f);
					}
				});
			}
		}
	}
	
	
	public void openLayout(File f) {
		if (f == null)
			return ;
		RCPLog.info("Loading Layout: " + f);
		
		XStream xs = new XStream();
		try {
			LayoutData ld = (LayoutData) xs.fromXML(new FileInputStream(f));
			RCPApplication.getInstance();
			DockStation station = RCPApplication.getWindowManager().getRootStation();
			LayoutReader.restore(station, ld, this);
			mCurrentLayout = f;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}



	protected void saveCurrentLayout() {		
		TextInputDialog dialog = new TextInputDialog("default");
		dialog.setTitle("Save Perspective");
		dialog.setHeaderText("Save the current active perspective");
		dialog.setContentText("Please enter a name:");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			RCPApplication.getInstance();
			WindowManager wmgr = RCPApplication.getWindowManager();
			LayoutData ld = LayoutWriter.saveStation(wmgr.getRootStation(), this);
			File outFile = new File(mLayoutDirectory + "/" + result.get() + ".layout");
			if (outFile.getParentFile().exists() == false)
				outFile.getParentFile().mkdirs();
			XStream xs = new XStream();
			try {
				RCPLog.info("Save Current Layout to : " + outFile);
				xs.toXML(ld, new FileOutputStream(outFile));
				mCurrentLayout = outFile;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	@Override
	public Object createIdentifier(DockNode dn) {
		RCPApplication.getInstance();
		if (dn == RCPApplication.getWindowManager().getEditorStation()){
			return "#EditorStation#";
		}else{
			Object userData = dn.getUserData();
			if (userData != null && userData instanceof IView){
				return ((IView)userData).getType();
			}
		}
		return null;
	}



	@Override
	public DockNode createDockNode(Object identifier, ArrayList<DockNode> existingNodes) {
		if (identifier instanceof String == false)
			return null;
		String id = (String)identifier;
		for (DockNode existing : existingNodes){
			if (existing.getUserData() != null && existing.getUserData() instanceof IView){
				IView v = (IView)existing.getUserData();
				if (v.getType() != null && v.getType().equals(id))
					return existing;
			}
		}
		if (id.equals("#EditorStation#")) {
			RCPApplication.getInstance();
			RCPApplication.getInstance();
			return RCPApplication.getWindowManager().getEditorStation();
		} else
			return RCPApplication.getWindowManager().createDockNodeByType(id);
	}

	@Override
	public void handleRemainingNodes(ArrayList<DockNode> existingNodes) {
		for (DockNode dn : existingNodes){
			if (dn.getUserData() != null && dn.getUserData() instanceof IEditor){
				RCPApplication.getInstance();
				RCPApplication.getWindowManager().dockEditor(dn);
			}
		}
	}


	public void close() {
		RCPApplication.getInstance();
		PropertyManager pm = RCPApplication.getPropertyManager();
		pm.put(CURRENT_LAYOUT_PROPERTY, mCurrentLayout);
	}

}
