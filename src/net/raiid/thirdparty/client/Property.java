package net.raiid.thirdparty.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class Property {

	private Properties properties;
	private OutputStream propertiesWriter;
	private String propertiesFileName;

	public Property(String fileName) {
		this.propertiesFileName = fileName;
		this.properties = new Properties();
		try {
			if (new File(this.propertiesFileName).exists()) {
				InputStream propertiesReader = new FileInputStream(this.propertiesFileName);
				this.properties.load(propertiesReader);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public boolean containsKey(String key) {
		return this.properties.containsKey(key);
	}
	public void put(String key, Object value) {
		this.properties.setProperty(key, value == null ? "" : value.toString());
		this.save();
	}
	public String get(String key) {
		return (String)this.properties.get(key);
	}
	public int getInt(String key) {
		return Integer.parseInt(this.get(key));
	}
	public boolean getBoolean(String key) {
		String bool = this.get(key);
		if (bool != null)
			return Boolean.parseBoolean(bool);
		return false;
	}

	public String get(String key, String def) {
		if (this.containsKey(key))
			return this.get(key);
		this.put(key, def);
		return def;
	}

	public int getInt(String key, int def) {
		if (this.containsKey(key))
			return this.getInt(key);
		this.put(key, def);
		return def;
	}

	public boolean getBoolean(String key, boolean def) {
		if (this.containsKey(key))
			return this.getBoolean(key);
		this.put(key, def);
		return def;
	}

	private void save() {
		try {
			this.propertiesWriter = new FileOutputStream(this.propertiesFileName);
			this.properties.store(this.propertiesWriter, null);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
