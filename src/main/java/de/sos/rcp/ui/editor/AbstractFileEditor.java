package de.sos.rcp.ui.editor;

import java.io.File;
import java.util.List;
import java.util.UUID;

import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.node.events.DockNodeEvent;

import de.sos.rcp.RCPApplication;
import de.sos.rcp.action.AbstractAction;
import de.sos.rcp.ui.impl.AbstractEditor;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public abstract class AbstractFileEditor extends AbstractEditor {

	private File 			mFile;

	private boolean			mDirty = false;
	
	private AbstractAction	mSaveAction = new AbstractAction("Save") {
		@Override
		public void execute() {
			save();
		}
	};
	private AbstractAction	mSaveAsAction = new AbstractAction("Save As") {
		@Override
		public void execute() {
			saveAs();
		}
	};
	
	public AbstractFileEditor(File file, String type, String uuid) {
		super(file != null ? file.getName() : "", type, uuid);
		setFile(file);

		RCPApplication.getMenuManager().addMenuAction("File.Save", "File.Quit", "File.New", mSaveAction);
		RCPApplication.getMenuManager().addMenuAction("File.SaveAs", "File.Save", null, mSaveAsAction);
		
		RCPApplication.getMenuManager().addToolbarAction("Save", mSaveAction);
		RCPApplication.getMenuManager().addToolbarAction("SaveAs", mSaveAsAction);
	}

	

	public AbstractFileEditor(File file, String type) {
		this(file, type, UUID.randomUUID().toString());
	}

	public AbstractFileEditor(File file) {
		this(file, file.getName());
	}
	
	

	@Override
	public void setDockNode(DockNode dock) {
		super.setDockNode(dock);
		dock.addEventFilter(DockNodeEvent.FOCUS, new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				if (event.getEventType() == DockNodeEvent.BRING_TO_BACK)
					onEditorDeactivated();
				else if (event.getEventType() == DockNodeEvent.BRING_TO_FRONT)
					onEditorActivated();
			};
		});
	}
	
	protected void onEditorDeactivated() {
		mSaveAction.deactivate();
		mSaveAsAction.deactivate();
	}

	protected void onEditorActivated() {
		mSaveAction.activate();
		mSaveAsAction.activate();
	}

	@Override
	public void onClose() {
		mSaveAction.deactivate();
		mSaveAsAction.deactivate();
//		RCPApplication.getMenuManager().removeAction(mSaveAction);
//		RCPApplication.getMenuManager().removeAction(mSaveAsAction);
	}


	public void setFile(File file) {
		mFile = file;
		if (file != null)
			mDockNode.getContent().titleProperty().set(file.getName());
	}
	public File getFile() { return mFile; }
	
	@Override
	public String getTitle() {
		if (getFile() != null)
			return getFile().getName();
		return "unknown";
	}
	
	private void setDirty(boolean b){
		if (b != mDirty){
			String t = getTitle();
			if (b)
				t+="*";
			mDockNode.getContent().titleProperty().set(t);
			mSaveAction.activate(!b);
			mDirty = b;
		}		
	}
	
	public void markDirty(){
		setDirty(true);
	}
	
	@Override
	protected void onGainFocus() {
		// TODO Auto-generated method stub
		super.onGainFocus();
	}
	
	@Override
	protected void onLostFocus() {
		super.onLostFocus();
	};
	
	
	public void load(){
		doLoad();
		setDirty(false);
	}
	abstract protected void doLoad();
	
	protected void saveAs() {
		FileChooser fc = new FileChooser();
		if (mFile != null){
			fc.setInitialDirectory(mFile.getParentFile());
			fc.setInitialFileName(mFile.getName());
		}
		List<String> extensions = getFileExtensions();
		if (extensions != null && extensions.isEmpty() == false)
			fc.setSelectedExtensionFilter(new ExtensionFilter("", getFileExtensions()));
		File result = fc.showSaveDialog(null);
	}
	
	public void save(){
		saveAs(mFile);
	}
	protected void saveAs(File f) {
		mFile = f;
		doSave();
		setDirty(false);
	}
	abstract protected void doSave();
	
	
	
	protected List<String> getFileExtensions() {
		return null;
	}
	
	

}
