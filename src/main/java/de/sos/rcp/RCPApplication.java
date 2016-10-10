package de.sos.rcp;

import java.io.File;

import com.anchorage.docks.stations.DockStation;
import com.anchorage.system.AnchorageSystem;

import de.sos.rcp.action.AbstractAction;
import de.sos.rcp.log.RCPLog;
import de.sos.rcp.mgr.LayoutManager;
import de.sos.rcp.mgr.MenuManager;
import de.sos.rcp.mgr.PropertyManager;
import de.sos.rcp.mgr.SelectionManager;
import de.sos.rcp.mgr.WindowManager;
import de.sos.rcp.mgr.WizardManager;
import de.sos.rcp.ui.editors.text.TextFileEditor;
import de.sos.rcp.ui.views.general.console.ConsoleView;
import de.sos.rcp.ui.views.general.properties.PropertiesView;
import de.sos.rcp.ui.views.general.workspace.WorkspaceView;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class RCPApplication extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}

	private static RCPApplication theInstance;
	public static RCPApplication getInstance(){return theInstance;}
	
	
	public static SelectionManager getSelectionManager() {
		return getInstance().mSelectionManager;
	}
	public static WindowManager getWindowManager() {
		return getInstance().mWindowManager;
	}

	public static MenuManager getMenuManager() {
		return getInstance().mMenuManager;
	}
	public static PropertyManager getPropertyManager(){
		return getInstance().mPropertyManager;
	}
	public static WizardManager getWizardManager() {
		return getInstance().mWizardManager;
	}
	

	private DockStation mRootStation;
	
	private MenuManager			mMenuManager;
	private LayoutManager		mLayoutManager;
	private WindowManager		mWindowManager;
	private SelectionManager 	mSelectionManager;
	private PropertyManager		mPropertyManager;
	private WizardManager		mWizardManager;

	public RCPApplication() {
		theInstance = this;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		mRootStation = AnchorageSystem.createStation();
		
		mPropertyManager = new PropertyManager(new File("settings.conf"));
		
		mSelectionManager = new SelectionManager();
		mWindowManager = new WindowManager();
		mLayoutManager = new LayoutManager();
		mMenuManager = new MenuManager();
		mWizardManager = new WizardManager();
		
		initialize();
		
		mSelectionManager.initialize();
		mWizardManager.initialize();
		mWindowManager.initialize(mRootStation, primaryStage);
		mLayoutManager.initialize();
		mMenuManager.initialize();
		
		
		VBox sceneRoot = new VBox(mMenuManager.getMenuBar(), mMenuManager.getToolBar(), mRootStation);
		VBox.setVgrow(mRootStation, Priority.ALWAYS);
		
		Scene scene = new Scene(sceneRoot,  1024, 768);

		AnchorageSystem.installDefaultStyle();

		primaryStage.setTitle("AnchorFX SubStation");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				saveState();
			}
		});
	}



	protected void initialize() {
		mMenuManager.addMenuAction("File.Quit", new AbstractAction("Quit"){ @Override
		public void execute(){ quitApplication(); }});
		
		ConsoleView.register(mWindowManager);
		WorkspaceView.register(mWindowManager);
		PropertiesView.register(mWindowManager);
		
		TextFileEditor.register(mWindowManager);
	}
	protected void saveState() {
		if (mPropertyManager.get("Application.SaveState", true)){
			try{mWindowManager.close();}catch(Exception e){e.printStackTrace();}
			try{mLayoutManager.close();}catch(Exception e){e.printStackTrace();}
			try{mPropertyManager.save();}catch(Exception e){e.printStackTrace();}
		}
	}
	public void quitApplication() {
		RCPLog.info("Close application");
		saveState();
		System.exit(0);		
	}


	



	
}
