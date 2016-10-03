package com.anchorage.docks.layout;

import com.anchorage.docks.node.DockNode;

public interface INodeSerializer{
	public Object createIdentifier(DockNode dn);
}
