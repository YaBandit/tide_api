package dylan.tide_api.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Contains the data within a config file.
 * The data is strictly read only.
 * 
 * However, there should be a reload config option
 */
public class ConfigReader {

    private static final String	      GLOBAL	   = "Global";

    public static List<String>	      trueValues   = Collections.unmodifiableList(Arrays.asList("true", "y", "yes", "t"));

    private final String	      componentId;

    private final String	      fileLoc;

    /*
     * TODO: Pretty sure that these should be read only as opposed to concurrent hashMaps
     */
    private final Map<String, String> globalsMap   = new ConcurrentHashMap<>();

    private final Map<String, String> componentMap = new ConcurrentHashMap<>();

    public ConfigReader(String componentId, String fileLoc) throws IOException {

	this.componentId = componentId;
	this.fileLoc = fileLoc;

	populateConfig();

    }

    private void populateConfig() throws IOException {

	final List<String> lines = Utils.readFile(fileLoc);

	for (String line : lines) {

	    if (line.startsWith(GLOBAL)) {

		insertIntoMap(true, line);

	    } else if (line.startsWith(componentId)) {

		insertIntoMap(false, line);

	    }

	}

    }

    private void insertIntoMap(boolean isGlobal, String line) {

	final String rest = removeFirstParameter(line);

	final String[] parts = rest.split("=");

	if (parts.length != 2) {
	    return;
	}

	if (isGlobal) {
	    globalsMap.put(parts[0], parts[1]);
	} else {
	    componentMap.put(parts[0], parts[1]);
	}

    }

    public void reloadConfig() throws IOException {

	globalsMap.clear();
	componentMap.clear();

	populateConfig();

    }

    private String removeFirstParameter(String line) {

	int loc = line.indexOf(".");

	return line.substring(loc + 1, line.length());

    }

    public boolean isValuePresent(String key) {

	return isValuePresent(false, key);
    }

    public boolean isValuePresent(boolean isGlobalProperty, String key) {

	final String value = getStringValue(isGlobalProperty, key);

	return value == null ? false : true;
    }

    public String getStringValue(String key) {

	return getStringValue(false, key);
    }

    public String getStringValue(boolean isGlobalProperty, String key) {

	return isGlobalProperty ? globalsMap.get(key) : componentMap.get(key);

    }

    public int getIntValue(String key) {

	return getIntValue(false, key);
    }

    public int getIntValue(boolean isGlobalProperty, String key) {

	final String value = isGlobalProperty ? globalsMap.get(key) : componentMap.get(key);

	if (value == null) {
	    throw new RuntimeException("Unable to find int value for key: " + key);
	}

	return Integer.parseInt(value);

    }

    public double getDoubleValue(boolean isGlobalProperty, String key) {

	final String value = isGlobalProperty ? globalsMap.get(key) : componentMap.get(key);

	if (value == null) {
	    throw new RuntimeException("Unable to find double value for key: " + key);
	}

	return Double.parseDouble(value);

    }

    public boolean getBooleanValue(boolean isGlobalProperty, String key) {

	final String value = isGlobalProperty ? globalsMap.get(key) : componentMap.get(key);

	if (value == null) {
	    return false;
	}

	return trueValues.contains(value.toLowerCase());

    }

    public String getComponentId() {
	return componentId;
    }

    public String getFileLoc() {
	return fileLoc;
    }

}
