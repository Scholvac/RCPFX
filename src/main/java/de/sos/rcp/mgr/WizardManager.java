package de.sos.rcp.mgr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import de.sos.rcp.RCPApplication;
import de.sos.rcp.action.AbstractAction;
import de.sos.rcp.log.RCPLog;
import de.sos.rcp.wizard.IWizard;

public class WizardManager {

	private class WizardDescription {
		String 						name;
		String						categorie;
		Class<? extends IWizard>	wizardClass;
	}
	
	private HashMap<String, List<WizardDescription>>		mWizardDescriptions = new HashMap<String, List<WizardDescription>>();
	
	public void register(String name, String categorie, Class<? extends IWizard> clazz){
		WizardDescription wd = new WizardDescription();
		wd.name = name; wd.categorie = categorie; wd.wizardClass = clazz;
		if (mWizardDescriptions.containsKey(categorie) == false)
			mWizardDescriptions.put(categorie, new ArrayList<WizardDescription>());
		mWizardDescriptions.get(categorie).add(wd);
	}
	
	
	public Collection<String> getWizardsByCategorie(String categorie){
		ArrayList<String> out = new ArrayList<String>();
		List<WizardDescription> l = mWizardDescriptions.get(categorie);
		if (l != null){
			for (WizardDescription wd : l)
				out.add(wd.name);
		}
		return out;
	}


	public void initialize() {
		Collection<String> newWizards = getWizardsByCategorie("New");
		for (String w : newWizards){
			RCPApplication.getMenuManager().addMenuAction("File.New."+w, new AbstractAction(w) {
				
				@Override
				public void execute() {
					showWizard("New", w, null);
				}
			});
		}
		
	}


	public void showWizard(String categorie, String type, Object userdata) {
		if (mWizardDescriptions.containsKey(categorie) == false){
			RCPLog.error("Could not find Wizard Categorie: " + categorie);
			return ;
		}
		for (WizardDescription wd : mWizardDescriptions.get(categorie)){
			if (wd.name.equals(type)){
				showWizard(wd, userdata);
				return ;
			}
		}
	}


	private void showWizard(WizardDescription wd, Object userdata) {
		try {
			IWizard wizard = wd.wizardClass.newInstance();
			wizard.setUserData(userdata);
			wizard.show();
		} catch (InstantiationException | IllegalAccessException e) {
			RCPLog.error("Failed to show wizard: " + wd.name + " Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
