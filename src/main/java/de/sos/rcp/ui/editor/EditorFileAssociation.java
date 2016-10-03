package de.sos.rcp.ui.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class EditorFileAssociation {
	private static EditorFileAssociation theInstance = new EditorFileAssociation();
	public static EditorFileAssociation getInstance() { return theInstance;}
	
	private HashMap<String, ArrayList<String>> 	mAssociations = new HashMap<>();
	
	private EditorFileAssociation(){}
	
	public void registerAssociation(String editor, String...suffixes){
		for (String suff : suffixes){
			if (mAssociations.containsKey(suff) == false)
				mAssociations.put(suff, new ArrayList<>());
			mAssociations.get(suff).add(editor);
		}
	}

	public static void register(String editor, String...strings) {
		getInstance().registerAssociation(editor, strings);
	}

	public Collection<String> getEditorsForFile(File file) {
		int p = file.getName().lastIndexOf('.');
		if (p < 0) return new ArrayList<>();
		String suffix = file.getName().substring(p+1);
		if (mAssociations.containsKey(suffix))
			return Collections.unmodifiableCollection(mAssociations.get(suffix));
		return new ArrayList<>();
	}
}
