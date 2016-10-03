package com.anchorage.docks.layout;

import java.util.ArrayList;

import com.anchorage.docks.node.DockNode;

public interface INodeDeserializer {
	public DockNode createDockNode(Object identifier, ArrayList<DockNode> existingNodes);

	/**
	 * Handle remaining nodes, that has not been re-docked somewhere
	 * if not handled, they will be lost after this call
	 * @param existingNodes
	 */
	public void handleRemainingNodes(ArrayList<DockNode> existingNodes);
}
