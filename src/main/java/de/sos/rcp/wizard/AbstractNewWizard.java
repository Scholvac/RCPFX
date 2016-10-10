
package de.sos.rcp.wizard;

import java.io.File;
import java.util.Collection;

import org.controlsfx.dialog.WizardPane;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;

public abstract class AbstractNewWizard extends AbstractWizard {

	protected AbstractNewWizard(String name, String type) {
		super(name, type);
	}

	public class LocationPane extends WizardPane {
		
		TextField		mLocationText = null;
		TextField		mNameText = null;
				
		public LocationPane() {
			setHeaderText("Choose new file location and name");
			
			GridPane gp = new GridPane();
	        gp.setVgap(10);
	        gp.setHgap(10);
	        
	        gp.add(new Label("Location"), 0, 0);
	        String loc = new File(".").getAbsolutePath();
	        if (getSelection() != null && getSelection().firstValue() instanceof File)
	        	loc = ((File)getSelection().firstValue()).getAbsolutePath();
	        mLocationText = new TextField(loc);
	        mLocationText.setId("FileLocation");
	        gp.add(mLocationText, 1, 0);
	        Button browse = new Button("Browse");
	        browse.setOnAction(e->{
	        	DirectoryChooser directoryChooser = new DirectoryChooser();
	        	if (mLocationText.getText() != null)
	        		directoryChooser.setInitialDirectory(new File(mLocationText.getText()));
                File selectedDirectory = directoryChooser.showDialog(null);
                mLocationText.setText(selectedDirectory.getAbsolutePath());
	        });
	        gp.add(browse, 2, 0);
	        
	        gp.add(new Label("Filename"), 0, 1);
	        mNameText = new TextField();
	        mNameText.setId("FileName");
	        gp.add(mNameText, 1, 1);
	        
	        setContent(gp);
		}
	}

	/**
	 * Returns a new wizard page, containing a directory and file name chooser
	 * writes the properties: 
	 * - FileLocation : path where the new file should be stored (directory)
	 * - FileName : name of the new file
	 * @return
	 */
	protected LocationPane getLocationPane(){
		return new LocationPane();
	}
}
