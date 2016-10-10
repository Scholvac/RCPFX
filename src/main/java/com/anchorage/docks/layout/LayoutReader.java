package com.anchorage.docks.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import com.anchorage.docks.containers.SingleDockContainer;
import com.anchorage.docks.containers.interfaces.DockContainableComponent;
import com.anchorage.docks.containers.interfaces.DockContainer;
import com.anchorage.docks.containers.subcontainers.DockSplitterContainer;
import com.anchorage.docks.containers.subcontainers.DockTabberContainer;
import com.anchorage.docks.layout.DockableStorage.ContainerType;
import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.stations.DockStation;
import com.anchorage.docks.stations.DockSubStation;
import com.anchorage.system.AnchorageSystem;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Tab;

public class LayoutReader {
	
	public static class DockableRestore {
		String						uniqueID;
		int							index;
		ContainerType				type;
		
		DockContainableComponent	component;
		DockContainableComponent	parent;
	}
	
	
	static class RuntimeData {
		LayoutData							ld;
		INodeDeserializer					identifier;
		DockStation							rootStation;
		HashMap<String, DockableRestore>	elements = new HashMap<String, DockableRestore>();
		public ArrayList<DockNode> 			existingNodes = new ArrayList<DockNode>();
	}
	
	
	public static void restore(DockStation station, LayoutData ld, INodeDeserializer identifier){
		RuntimeData rd = new RuntimeData();
		rd.identifier = identifier;
		rd.ld = ld;
		rd.rootStation = station;
		rd.existingNodes.addAll(station.getNodes());		
		
		ArrayList<DockNode> existingNodes = new ArrayList<DockNode>();
		existingNodes.addAll(station.getNodes());
		//undock all, to destroy the current layout - remaining nodes will be closed later
		for (DockNode dn : existingNodes) dn.undock();
		
		
		//create new the new nodes
		
		for (DockableStorage ds : ld.storage.values()){
			DockableRestore dr = create(ds, rd);
			if (dr.component instanceof DockNode)
				((DockNode)dr.component).stationProperty().set(station);
		}
		
		DockableRestore station_dr = null;
		for (DockableRestore dr : rd.elements.values()) if (dr.type == ContainerType.Station) { station_dr = dr; break;}
		
		if (station_dr != null)
			rebuild(station_dr, rd);
		
		//handle undocked nodes, that has not been re-docked somewhere
		//since we don't know if they are still needed, let the application decide about
		identifier.handleRemainingNodes(existingNodes);
	}

	private static void rebuild(DockableRestore dr, RuntimeData rd) {
		ArrayList<DockableRestore> children = collectChildren(dr.component, rd);
		Collections.sort(children, new Comparator<DockableRestore>() {
			@Override
			public int compare(DockableRestore o1, DockableRestore o2) {
				return Integer.compare(o1.index, o2.index);
			}
		});
		List content = getContent(dr);
		for (DockableRestore c : children){
			rebuild(c, rd);
			if (dr.type == ContainerType.TAB){
				String title = "";
				if (c.component instanceof DockNode)
					title = ((DockNode)c.component).getContent().titleProperty().get();
				Tab t = new Tab(title);
				t.setContent((Node) c.component);
				content.add(t);
			}else
				content.add(c.component);
			c.component.setParentContainer((DockContainer) dr.component);
			
		}
	}

	private static List getContent(DockableRestore dr) {
		switch(dr.type){
		case SINGLE:
		case Station:
			return ((SingleDockContainer)dr.component).getChildren();
		case TAB:
			return ((DockTabberContainer)dr.component).getTabs();
		case SPLIT:
			return ((DockSplitterContainer)dr.component).getItems();
		}
		return null;
	}

	private static ArrayList<DockableRestore> collectChildren(DockContainableComponent parent, RuntimeData rd) {
		ArrayList<DockableRestore> out = new ArrayList<DockableRestore>();
		for (DockableRestore dr : rd.elements.values()){
			if (dr.parent == parent)
				out.add(dr);
		}
		return out;
	}

	private static DockableRestore create(DockableStorage ds, RuntimeData rd) {
		if (rd.elements.containsKey(ds.uniqueID))
			return rd.elements.get(ds.uniqueID);
		DockableRestore dr = null;
		switch(ds.type){
		case Node:
			dr = createNode(ds, rd);break;
		case SubStation:
			dr = createSubStation(ds, rd);break;
		case SINGLE:
			dr = createSingleContainer(ds, rd);break;
		case SPLIT:
			dr = createSplitContainer(ds, rd);break;
		case TAB:
			dr = createTabSplitContainer(ds, rd);break;
		case Station:
			dr = createStation(ds, rd);break;
		}
		rd.elements.put(dr.uniqueID, dr);
		return dr;
	}

	

	private static DockableRestore createStation(DockableStorage ds, RuntimeData rd) {
		DockableRestore dr = new DockableRestore();
		fill(ds, dr, rd);
		dr.component = rd.rootStation;
		return dr;
	}

	private static DockableRestore createTabSplitContainer(DockableStorage ds, RuntimeData rd) {
		DockTabberContainer sdc = new DockTabberContainer();
		DockableRestore dr = new DockableRestore();
		fill(ds, dr, rd);
		dr.component = sdc;
		return dr;
	}
	private static DockableRestore createSplitContainer(DockableStorage ds, RuntimeData rd) {
		DockSplitterContainer sdc = new DockSplitterContainer();
		sdc.setOrientation((Orientation) ds.userData.get("Orientation"));
		sdc.setDividerPositions((double[]) ds.userData.get("DividerPos"));
		DockableRestore dr = new DockableRestore();
		fill(ds, dr, rd);
		dr.component = sdc;
		return dr;
	}
	private static DockableRestore createSingleContainer(DockableStorage ds, RuntimeData rd) {
		SingleDockContainer sdc = new SingleDockContainer();
		DockableRestore dr = new DockableRestore();
		fill(ds, dr, rd);
		dr.component = sdc;
		return dr;
	}
	private static DockableRestore createSubStation(DockableStorage ds, RuntimeData rd) {
		Object id = ds.userData.get("Ident");
		DockNode dn = rd.identifier.createDockNode(id, rd.existingNodes);
		DockSubStation dss = null;
		if (dn instanceof DockSubStation){
			dss = (DockSubStation)dn;
		}else{
			dss = AnchorageSystem.createSubStation(rd.rootStation, "");
		}
		DockableRestore dr = new DockableRestore();
		fill(ds, dr, rd);
		dr.component = dss;
		dn.stationProperty().set(rd.rootStation);
		rd.rootStation.add(dn);

		LayoutData ld = (LayoutData) ds.userData.get("SubStation");
		LayoutReader reader = new LayoutReader();
		LayoutReader.restore(dss.getSubStation(), ld, rd.identifier);
		
		return dr;
	}
	private static DockableRestore createNode(DockableStorage ds, RuntimeData rd) {
		Object id = ds.userData.get("Ident");
		DockNode dn = rd.identifier.createDockNode(id, rd.existingNodes);
		
		DockableRestore dr = new DockableRestore();
		fill(ds, dr, rd);
		dr.component = dn;
		dn.stationProperty().set(rd.rootStation);
		rd.rootStation.add(dn);
		return dr;
	}

	private static void fill(final DockableStorage ds, DockableRestore dr, RuntimeData rd) {
		dr.index = ds.index;
		dr.type = ds.type;
		dr.uniqueID = ds.uniqueID;
		if (ds.parentID != null){
			dr.parent = create(rd.ld.storage.get(ds.parentID), rd).component;
		}
	}
}
