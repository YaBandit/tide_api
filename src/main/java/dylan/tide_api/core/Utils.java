package dylan.tide_api.core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

public class Utils {

    public static ConfigReader handleArgs(String[] args) throws Exception {

	final Properties props = new Properties();
	props.load(new FileInputStream("log4j.properties"));
	PropertyConfigurator.configure(props);

	if (args.length < 2) {
	    throw new Exception("Insufficient program arguments. Must specify componentId and config file location");
	}

	final String[] compIdParts = args[0].split("=");

	if (compIdParts.length != 2 || !compIdParts[0].equals(Globals.COMPONENT_ID)) {
	    throw new Exception("Component Id is not specifed in the first program argument");
	}

	final String[] configLocParts = args[1].split("=");

	if (configLocParts.length != 2 || !configLocParts[0].equals(Globals.CONFIG)) {
	    throw new Exception("Config file location is not specifed in the second program argument");
	}

	final ConfigReader config = new ConfigReader(compIdParts[1], configLocParts[1]);

	return config;
    }

    public static List<String> readFile(String fileLoc) throws IOException {

	final List<String> lines = new ArrayList<>();

	final BufferedReader br = new BufferedReader(new FileReader(fileLoc));

	try {

	    String line = br.readLine();

	    while (line != null) {

		lines.add(line);

		line = br.readLine();

	    }

	} finally {

	    br.close();

	}

	return lines;

    }

}
