package de.sos.rcp.ui.views.general.workspace;


import java.io.File;
import java.util.Collection;

import de.sos.rcp.RCPApplication;
import de.sos.rcp.mgr.WindowManager;
import de.sos.rcp.ui.editor.EditorFileAssociation;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class WorkspaceControl extends VBox
{

	private ContextMenu mContextMenu = new ContextMenu();
	private TreeView<File> fileView;

	public WorkspaceControl(ObjectProperty<File> rootDir){
		fileView = new TreeView<File>(new SimpleFileTreeItem(rootDir.get()));
		fileView.setCellFactory(new Callback<TreeView<File>, TreeCell<File>>() {				
			@Override
			public TreeCell<File> call(TreeView<File> param) {
				return new TreeCell<File>(){
					@Override
					protected void updateItem(File item, boolean empty) {
						super.updateItem(item, empty);
						if (item != null && empty == false){
							setText(item.getName());
						}else
							setText(null);
					}
				};
			}
		});
		fileView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
			@Override
			public void handle(ContextMenuEvent event) {
				SimpleFileTreeItem item = (SimpleFileTreeItem) fileView.getSelectionModel().getSelectedItem();
				buildContextMenu(item);
			}
		});
		fileView.setContextMenu(mContextMenu);
		getChildren().add(fileView);
		
		VBox.setVgrow(fileView, Priority.ALWAYS);
	}

	protected void buildContextMenu(SimpleFileTreeItem item) {
		mContextMenu.getItems().clear();
		File file = item.getValue();
		if (file.isFile()){
			Menu ow = createOpenWithMenu(file);
			if (ow != null)
				mContextMenu.getItems().add(ow);
		}else{
			Menu wm = createNewWizardMenu(file);
			if (wm != null)
				mContextMenu.getItems().add(wm);
		}
	}

	private Menu createNewWizardMenu(File file) {
		Collection<String> newWizards = RCPApplication.getWizardManager().getWizardsByCategorie("New");
		if (newWizards != null && newWizards.isEmpty() == false){
			Menu m = new Menu("New");
			for (String w : newWizards){
				MenuItem item = new MenuItem(w);
				item.setOnAction(e->{
					RCPApplication.getWizardManager().showWizard("New", w, null);
				});
				m.getItems().add(item);
			}
			return m;
		}
		return null;
	}

	private Menu createOpenWithMenu(File file) {
		Collection<String> editors = EditorFileAssociation.getInstance().getEditorsForFile(file);
		if (editors != null && editors.isEmpty() == false){
			Menu m = new Menu("Open With");
			for (String edt : editors){
				MenuItem item = new MenuItem(edt);
				item.setOnAction(e->{
					RCPApplication.getInstance();
					WindowManager wm = RCPApplication.getWindowManager();
					wm.openFileEditor(edt, file);
				});
				m.getItems().add(item);
			}
			return m;
		}
		return null;
	}
	
	public TreeView<File> getTreeView() {
		return fileView;
	}
}
