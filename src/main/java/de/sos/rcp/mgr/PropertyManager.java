package de.sos.rcp.mgr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import com.thoughtworks.xstream.XStream;

public class PropertyManager {
	
	private File mPropertyFile;
	
	HashMap<String, Object>		mValues = new HashMap<String, Object>();

	public PropertyManager(File path){
		mPropertyFile = path;
		load();
	}

	private void load() {
		if (mPropertyFile != null && mPropertyFile.exists()){
			XStream xs = new XStream();
			try {
				mValues = (HashMap<String, Object>) xs.fromXML(new FileInputStream(mPropertyFile));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void save(){
		save(mPropertyFile);
	}
	public void save(File file){
		try {
			XStream xs = new XStream();
			if (file.getAbsoluteFile().getParentFile().exists() == false)
				file.getAbsoluteFile().getParentFile().mkdirs();
			xs.toXML(mValues, new FileOutputStream(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public <T> T get(String name, T defaultValue) {
		if (mValues.containsKey(name)){
			return (T) mValues.get(name);
		}
		mValues.put(name, defaultValue);
		return defaultValue;
	}

	public <T> void put(String key, T value) {
		mValues.put(key, value);
	}

	public boolean hasProperty(String string) {
		return mValues.containsKey(string);
	}
	public <T> T get(String name){
		return (T)mValues.get(name);
	}
}
