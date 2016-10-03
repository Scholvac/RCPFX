package com.anchorage.docks.layout;

import java.util.HashMap;

public class DockableStorage {

	public enum ContainerType {
		Node, Station, SubStation, SINGLE, SPLIT, TAB
	}

	public String parentID;
	public String uniqueID;
	public int index;
	public ContainerType type;
	public HashMap<String, Object> userData = new HashMap<String, Object>();

}
