/*
 * Copyright 2015-2016 Alessio Vinerbi. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anchorage.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.anchorage.docks.layout.INodeDeserializer;
import com.anchorage.docks.layout.INodeSerializer;
import com.anchorage.docks.layout.LayoutData;
import com.anchorage.docks.layout.LayoutReader;
import com.anchorage.docks.layout.LayoutWriter;
import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.stations.DockStation;
import com.anchorage.docks.stations.DockSubStation;
import com.anchorage.system.AnchorageSystem;
import com.thoughtworks.xstream.XStream;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 *
 * @author Alessio
 */
public class AnchorFX_layout extends Application {

    private DockStation station;

	@Override
    public void start(Stage primaryStage) {

        station = AnchorageSystem.createStation();
        Scene scene = new Scene(station,  1024, 768);
        
        if (new File("Layout.xml").exists()){
        	XStream xstream = new XStream();
        	try {
				LayoutData ld = (LayoutData) xstream.fromXML(new FileInputStream(new File("Layout.xml")));
				
				LayoutReader.restore(station, ld, new INodeDeserializer() {
					
					@Override
					public DockNode createDockNode(Object identifier, ArrayList<DockNode> existingNodes) {
						if (((DockNodeIdentifier)identifier).type.equals("SubStation")){
							return AnchorageSystem.createSubStation(station, "SubStation");
						}
						DockNode subNode3 = AnchorageSystem.createDock(((DockNodeIdentifier)identifier).type, new TableView());
				        return subNode3;						
					}

					@Override
					public void handleRemainingNodes(ArrayList<DockNode> existingNodes) {
						//Don't need them anymore
					}
				});
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }else{      

	        DockNode node1 = AnchorageSystem.createDock("Node 1", new TableView());
	        node1.dock(station, DockNode.DockPosition.CENTER);
	        DockNode node2 = AnchorageSystem.createDock("Node 2", new TableView());
	        node2.dock(station, DockNode.DockPosition.CENTER);
	        DockNode node3 = AnchorageSystem.createDock("Node 3", new TableView());
	        node3.dock(station, DockNode.DockPosition.BOTTOM);
	
	        
	        DockSubStation subStation = AnchorageSystem.createSubStation(station, "SubStation");
	        subStation.dock(station, DockNode.DockPosition.LEFT,0.7);
	        subStation.disableDockCapability();
	
	        DockNode subNode1 = AnchorageSystem.createDock("subNode 1", new TableView());
	        subNode1.dock(subStation, DockNode.DockPosition.LEFT);
	
	        DockNode subNode2 = AnchorageSystem.createDock("subNode 2", new TableView());
	        subNode2.dock(subStation, DockNode.DockPosition.LEFT);
	        
	        DockNode subNode3 = AnchorageSystem.createDock("subNode 3", new TableView());
	        subNode3.dock(subStation, DockNode.DockPosition.BOTTOM);
        }
        

        AnchorageSystem.installDefaultStyle();

        primaryStage.setTitle("AnchorFX SubStation");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(l->{
        	saveLayout("Layout.xml");
        });
        
    }

	
    private void saveLayout(String file) {
		System.out.println("Save Layout to " + file);
		
		INodeSerializer identifier = new INodeSerializer() {
			
			@Override
			public Object createIdentifier(DockNode dn) {
				DockNodeIdentifier dni = new DockNodeIdentifier();
				dni.name = dn.getId();
				dni.type = dn.getContent().titleProperty().get();
				return dni;
			}
		};
		LayoutWriter ss = new LayoutWriter();
		LayoutData ld = LayoutWriter.saveStation(station, identifier);
		try{
			XStream stream = new XStream();
			stream.toXML(ld, new FileOutputStream(new File(file)));
		}catch(Exception e){
			e.printStackTrace();
		}
//		StationLayout sl = new StationLayout();
//		sl.saveStation(station, identifier);
//		
//		try {
//			XStream stream = new XStream();
//			stream.toXML(sl, new FileOutputStream(new File(file)));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	/**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
