package de.sos.rcp.ui.editor;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.reactfx.Subscription;

import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.node.events.DockNodeEvent;

import de.sos.rcp.RCPApplication;
import de.sos.rcp.action.AbstractAction;
import de.sos.rcp.mgr.cmd.Command;
import de.sos.rcp.ui.impl.AbstractEditor;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public abstract class AbstractFileEditor extends AbstractEditor {

	private File 			mFile;

	private boolean			mDirty = true;
	
	private Consumer<Command> 	mMarkDirtyConsumer = new Consumer<Command>() { //need a variable to register and deregister the consumer		
		@Override
		public void accept(Command t) {
			markDirty();
		}
	};
	
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

	private Subscription mMarkDirtySubscription;
	
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
		//FIXME: Not called anymore
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
		
		//unsubscribe from change events, as long as we have no focus
		if (mMarkDirtySubscription != null){
			mMarkDirtySubscription.unsubscribe();
			mMarkDirtySubscription = null;
		}
	}

	protected void onEditorActivated() {
		mSaveAction.activate();
		mSaveAsAction.activate();
		
		//subscribe for editing commands, that did modify the underlying model. 
		//at this point, we do not care about the actual change but use the command only to 
		//indicate if we got some changes or not, to mark the editor dirty. 
		mMarkDirtySubscription = RCPApplication.getCommandManager().successionEnds(Duration.ofMillis(100)).subscribe(mMarkDirtyConsumer);
	}

	@Override
	public void onClose() {
		mSaveAction.deactivate();
		mSaveAsAction.deactivate();
	}


	public void setFile(File file) {
		mFile = file;
		if (mFile != null)
			mSaveAction.setLabel("Save");
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
			mSaveAction.enable(b);
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
			fc.setSelectedExtensionFilter(new ExtensionFilter("Save File", getFileExtensions()));
		File result = fc.showSaveDialog(null);
		if (result != null){
			if (result.isFile() && result.getParentFile().isDirectory() && !result.getParentFile().exists())
				result.getParentFile().mkdirs();
			saveAs(result);
		}
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
