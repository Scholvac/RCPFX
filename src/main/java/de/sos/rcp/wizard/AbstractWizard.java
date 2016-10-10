package de.sos.rcp.wizard;

import java.util.Collection;
import java.util.Optional;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.Wizard.LinearFlow;
import org.controlsfx.dialog.WizardPane;

import de.sos.rcp.RCPApplication;
import de.sos.rcp.log.RCPLog;
import de.sos.rcp.mgr.selection.Selection;
import javafx.scene.control.ButtonType;

public abstract class AbstractWizard implements IWizard
{

	private Selection			mSelection = null;
	private String				mTitle = null;
	private String				mType = null;
	
	protected AbstractWizard(String name, String type) {
		mTitle = name;
		mType = type;
		mSelection = RCPApplication.getSelectionManager().last();
	}
	
	@Override
	public void show() {
		Wizard w = new Wizard(null);
		w.setTitle(getTitle());
		
		Collection<WizardPane> pages = getPages();
		LinearFlow lf = new LinearFlow(pages);
		w.setFlow(lf);
		
		Optional<ButtonType> result = w.showAndWait();
	
		if (result.isPresent()){
			if (result.get() == ButtonType.FINISH){
				RCPLog.info("Finished wizard");
				onFinish(w);
			}
		}
	}

	
	protected abstract Collection<WizardPane> getPages();
	
	/**
	 * Called after the wizard has finished, using the FINISH button
	 * @param w
	 */
	protected void onFinish(Wizard w) {
		// TODO Auto-generated method stub
		
	}

	public String getTitle() { return mTitle; }
	public String getType(){ return mType; }
	protected Selection getSelection() { return mSelection;}
}
