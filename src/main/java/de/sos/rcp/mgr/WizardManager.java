package de.sos.rcp.mgr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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
}
