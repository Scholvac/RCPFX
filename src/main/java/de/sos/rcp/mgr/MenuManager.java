package de.sos.rcp.mgr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.sos.rcp.action.AbstractAction;
import de.sos.rcp.action.IAction;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;

public class MenuManager {

	
	private class MenuAction {
		String 		path;
		String		before;
		String 		after;
		IAction		action;
	}

	private ToolBar			mToolbar;
	private MenuBar 		mMenuBar;
	
	private List<MenuAction>		mMenuActions = new ArrayList<MenuManager.MenuAction>();
	private List<MenuAction>		mToolbarActions = new ArrayList<MenuManager.MenuAction>();
	
	public ToolBar getToolBar() {
		return mToolbar;
	}

	public MenuBar getMenuBar() {
		return mMenuBar;
	}

	public void initialize() {
		mMenuBar = new MenuBar();
		mToolbar = new ToolBar();
		
		rebuildMenuBar();
		rebuildToolbar();
	}

	
	public void addMenuAction(String path, IAction action){
		addMenuAction(path, null, null, action);
	}
	public void addMenuAction(String path, String before, String after, IAction action){
		MenuAction ma = new MenuAction();
		ma.action = action; ma.before = before; ma.after = after; ma.path = path;
		mMenuActions.add(ma);
		action.activeProperty().addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				rebuildMenuBar();
			}
		});
		if (mMenuBar != null)
			rebuildMenuBar();
	}

	public void addToolbarAction(String path, IAction action){
		addToolbarAction(path, null, null, action);
	}
	public void addToolbarAction(String path, String before, String after, IAction action){
		MenuAction ma = new MenuAction();
		ma.action = action; ma.before = before; ma.after = after; ma.path = path;
		mToolbarActions.add(ma);
		action.activeProperty().addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				rebuildToolbar();
			}
		});
		if (mToolbar != null)
			rebuildToolbar();
	}

	
	
	private List<MenuAction> getSortedActions(List<MenuAction> list) {
		ArrayList<MenuAction> actions = new ArrayList<MenuAction>();
		actions.addAll(list);
		Collections.sort(actions, new Comparator<MenuAction>() {
			@Override
			public int compare(MenuAction o1, MenuAction o2) {
				String p1 = o1.path;
				String p2 = o2.path;
				if (o2.after != null && o1.path.contains(o2.after))
					return -1;
				if (o1.before != null && o2.path.contains(o1.before))
					return 1;
				return p1.compareTo(p2);
			}
		});
		return actions;
	}
	
	List<String> removeFirstSegment(List<String> qn){
		qn.remove(0);
		return qn;
	}
	List<String> removeLastSegment(List<String> qn){
		qn.remove(qn.size()-1);
		return qn;
	}
	private List<String> qualifiedName(String path) {
		ArrayList<String> out =new ArrayList<String>();
		out.addAll(Arrays.asList(path.split("\\.")));
		return out;
	}
	private String lastSegment(List<String> qn){
		return qn.get(qn.size()-1);
	}
	private Menu getMenu(List<String> qn) {
		String f = qn.get(0);
		for (Menu m : mMenuBar.getMenus()){
			if (m.getText().equals(f)){
				if (qn.size() == 1)
					return m;
				else
					return getMenu(removeFirstSegment(qn), m);
			}
		}
		Menu menu = new Menu(f);
		mMenuBar.getMenus().add(menu);
		if (qn.size() == 1)
			return menu;
		return getMenu(removeFirstSegment(qn), menu);
	}

	private Menu getMenu(ContextMenu cm, List<String> qn) {
		String f = qn.get(0);
		for (MenuItem m : cm.getItems()){
			if (m.getText().equals(f)){
				if (qn.size() == 1)
					return (Menu)m;
				else
					return getMenu(removeFirstSegment(qn), (Menu)m);
			}
		}
		Menu menu = new Menu(f);
		cm.getItems().add(menu);
		if (qn.size() == 1)
			return menu;
		return getMenu(removeFirstSegment(qn), menu);
	}
	
	private Menu getMenu(List<String> qn, Menu parent) {
		String f = qn.get(0);
		for (MenuItem m : parent.getItems()){
			if (m.getText().equals(f)){
				if (qn.size() == 1)
					return (Menu)m;
				else
					return getMenu(removeFirstSegment(qn), (Menu) m);
			}
		}
		Menu menu = new Menu(f);
		parent.getItems().add(menu);
		if (qn.size() == 1)
			return menu;
		return getMenu(removeFirstSegment(qn), menu);
	}
	
	
	public void populate(ContextMenu cm) {
		cm.getItems().clear();
		
		List<MenuAction> actions = getSortedActions(mMenuActions);
		for (final MenuAction a : actions){
			if (a.action.activeProperty().get() == false)
				continue;
			String path = a.path;
			List<String> qn = qualifiedName(path);
			MenuItem item = new MenuItem(lastSegment(qn));
			item.setOnAction(new EventHandler<ActionEvent>() {				
				@Override
				public void handle(ActionEvent event) {
					a.action.execute();
				}
			});
			a.action.enableProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					item.setDisable(!newValue);
				}
			});
			
			if (qn.size() == 1){
				cm.getItems().add(item);
			}else{
				Menu menu = getMenu(cm, removeLastSegment(qn));
				menu.getItems().add(item);
			}
		}
	}
	

	public void rebuildMenuBar() {
		if (mMenuBar == null)
			return ;
		mMenuBar.getMenus().clear();
		List<MenuAction> actions = getSortedActions(mMenuActions);
		for (final MenuAction a : actions){
			if (a.action.activeProperty().get() == false)
				continue;
			String path = a.path;
			List<String> qn = qualifiedName(path);
			Menu menu = getMenu(removeLastSegment(qn));
			MenuItem item = new MenuItem(lastSegment(qualifiedName(path)));
			item.setOnAction(new EventHandler<ActionEvent>() {				
				@Override
				public void handle(ActionEvent event) {
					a.action.execute();
				}
			});
			a.action.enableProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					item.setDisable(!newValue);
				}
			});
			menu.getItems().add(item);
		}
	}
	

	public void rebuildToolbar() {
		if (mToolbar == null)
			return ;
		mToolbar.getItems().clear();
		List<MenuAction> actions = getSortedActions(mToolbarActions);
		for (MenuAction a : actions){
			if (a.action.activeProperty().get() == false)
				continue;
			Node icon = a.action.getIcon();
			String label = a.action.getLabel();
			Button btn = new Button(label, icon);
			btn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					a.action.execute();
				}
			});
			a.action.enableProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					btn.setDisable(!newValue);
				}
			});
			mToolbar.getItems().add(btn);			
		}
		
	}

	public void removeAction(AbstractAction action) {
		removeMenuAction(action);
		removeToolbarAction(action);
	}

	public void removeToolbarAction(AbstractAction action) {
		if (removeAction(mToolbarActions, action))
			rebuildToolbar();
	}

	public void removeMenuAction(AbstractAction action) {
		if (removeAction(mMenuActions, action))
			rebuildMenuBar();
	}

	private boolean removeAction(List<MenuAction> list, AbstractAction action) {
		for (MenuAction e : list){
			if (e.action == action){
				list.remove(e);
				return true;
			}
		}
		return false;
	}

	
}
