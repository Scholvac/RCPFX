package com.anchorage.docks.layout;

import com.anchorage.docks.containers.SingleDockContainer;
import com.anchorage.docks.containers.interfaces.DockContainableComponent;
import com.anchorage.docks.containers.interfaces.DockContainer;
import com.anchorage.docks.containers.subcontainers.DockSplitterContainer;
import com.anchorage.docks.containers.subcontainers.DockTabberContainer;
import com.anchorage.docks.layout.DockableStorage.ContainerType;
import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.stations.DockStation;
import com.anchorage.docks.stations.DockSubStation;
import javafx.scene.Node;

public class LayoutWriter {
	
	


	
	public static LayoutData saveStation(DockStation station, INodeSerializer ident){
		LayoutData ld = new LayoutData();
		collectContent(station, ident, ld);
		return ld;
	}

	private static void collectContent(DockStation station, INodeSerializer ident, LayoutData ld) {
		for (DockNode dn : station.getNodes()){
			DockContainer container = dn.getParentContainer();
			storeContainer(container, ld);
			
			DockableStorage ds = new DockableStorage();
			ds.uniqueID = dn.getUniqueID();
			ds.parentID = container.getUniqueID();
			ds.index = container.indexOf(dn);
			ds.type = getType(dn);
			Object identifier = ident.createIdentifier(dn);
			ds.userData.put("Ident", identifier);
			
			if (ds.type == ContainerType.SubStation){
				LayoutWriter ss = new LayoutWriter();
				LayoutData sld = LayoutWriter.saveStation(((DockSubStation)dn).getSubStation(), ident);
				ds.userData.put("SubStation", sld);
			}
			if (identifier != null)
				ld.storage.put(ds.uniqueID, ds);
		}
	}

	private static void storeContainer(DockContainer dc, LayoutData ld) {
		if (dc == null || ld.storage.containsKey(dc.getUniqueID()))
			return ;
		
		DockableStorage ds = new DockableStorage();
		ds.uniqueID = dc.getUniqueID();
		DockContainer container = dc.getParentContainer();
		int idx = container != null ? container.indexOf((Node)dc) : -1;
		ds.parentID = container != null ? container.getUniqueID() : null;
		ds.index = idx;
		ds.type = getType(dc);
		
		if (ds.type == ContainerType.SPLIT){
			DockSplitterContainer dsc = (DockSplitterContainer)dc;
			ds.userData.put("Orientation", dsc.getOrientation());
			ds.userData.put("DividerPos", dsc.getDividerPositions());
		}
		
		ld.storage.put(ds.uniqueID, ds);
		if (container != null)
			storeContainer(container, ld);
	}

	private static ContainerType getType(DockContainableComponent dc) {
		if (dc instanceof DockStation)
			return ContainerType.Station;
		if (dc instanceof DockSubStation)
			return ContainerType.SubStation;
		if (dc instanceof DockNode)
			return ContainerType.Node;
		if (dc instanceof SingleDockContainer)
			return ContainerType.SINGLE;
		if (dc instanceof DockTabberContainer)
			return ContainerType.TAB;
		if (dc instanceof DockSplitterContainer)
			return ContainerType.SPLIT;
		return null;
	}


	
	
}
