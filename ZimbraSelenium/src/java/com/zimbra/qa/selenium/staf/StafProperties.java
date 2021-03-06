package com.zimbra.qa.selenium.staf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;


/**
 * @author Matt Rhoades
 *
 * StafProperties is used to generate config.properties specific to this STAF invocation.
 * The constructor reads the default conf/config.properties file, saves it to a Properties
 * object, then during save() will write the Properties to a temporary config.properties
 * file used by STAF for execution.
 * 
 * When using STAF, the Zimbra server is set according to the arguments passed to STAF
 * 
 */
public class StafProperties {
    private static Logger logger = Logger.getLogger(StafProperties.class);
    
    private static final String ConfigPropertiesComments = "Auto Generated By STAF";
    
    protected Properties properties = null;
    protected String propertiesFilename = null;
    
    public StafProperties(String filename) throws FileNotFoundException, IOException {
    	logger.info("New StafProperties from "+ filename);
    	
    	properties = new Properties();
    	properties.load(new FileReader(filename));
    	
    }
    
    public String setProperty(String key, String value) {
    	String original = (String)properties.get(key);
    	properties.setProperty(key, value);
    	return (original);
    }

    public String save(String foldername) throws IOException {
    	propertiesFilename = foldername + "/" + System.currentTimeMillis() + "config.properties";
    	
    	File f = new File(propertiesFilename);
    	f.getParentFile().mkdirs();
    	
    	properties.store(new FileWriter(propertiesFilename), ConfigPropertiesComments);
    	return (propertiesFilename);
    }
    
}
