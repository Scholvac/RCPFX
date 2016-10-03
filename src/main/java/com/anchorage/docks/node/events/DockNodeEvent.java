package com.anchorage.docks.node.events;

import com.anchorage.docks.node.DockNode;

import javafx.event.Event;
import javafx.event.EventType;

public class DockNodeEvent extends Event {

	
	public static final EventType<DockNodeEvent> 		ANY 					= new EventType<DockNodeEvent>(Event.ANY, "DockNodes");
	public static final EventType<DockNodeEvent> 		DOCK_NODE_CLOSING 		= new EventType<DockNodeEvent>(ANY, "Dock node closing");
	public static final EventType<DockNodeEvent>		DOCK_NODE_DOCKED		= new EventType<DockNodeEvent>(ANY, "DockNode docking");
	
	public static final EventType<DockNodeEvent> 		FOCUS		 			= new EventType<DockNodeEvent>(ANY, "DockNode Focus change");
	public static final EventType<DockNodeEvent> 		BRING_TO_FRONT 			= new EventType<DockNodeEvent>(FOCUS, "DockNode brought to front");
	public static final EventType<DockNodeEvent> 		BRING_TO_BACK 			= new EventType<DockNodeEvent>(FOCUS, "DockNode brought to back");
	
	
	private DockNode mNode;
	
	public DockNodeEvent(DockNode node, EventType<? extends Event> eventType) {
		super(eventType);
		mNode = node;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
		
	
	public DockNode getNode(){ return mNode; }
	
}