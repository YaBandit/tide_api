package dylan.tide_api.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dylan.tide_api.Engine;
import dylan.tide_api.core.Utils;

public class DataManager {

    private static final Logger	      log	    = LoggerFactory.getLogger(DataManager.class);

    private static final String	      KEY_DATA_LOC  = "dataLoc";
    private static final String	      KEY_USERS_LOC = "usersLoc";

    private final List<ActiveFeature> activeFeatures;

    private final List<String>	      allowedUsers;

    public DataManager(Engine engine) throws Exception {

	final String fileLoc = engine.getConfig()
				     .getStringValue(KEY_DATA_LOC);

	log.info("Loading data from file: " + fileLoc);

	final List<String> lines = Utils.readFile(fileLoc);

	log.info("Found " + lines.size() + " lines");

	final List<ActiveFeature> afs = new ArrayList<>();

	boolean passedHeader = false;

	for (String line : lines) {

	    if (!passedHeader) {
		passedHeader = true;
		continue;
	    }

	    final String[] parts = line.split(",");

	    if (parts.length != 3) {
		throw new Exception("Data line imporoperly formatted: " + line);
	    }

	    final String name = parts[0];

	    final boolean isEnabled = parts[1].toUpperCase()
					      .startsWith("T");

	    final String[] versions = parts[2].split("\\|");

	    final List<Version> vl = new ArrayList<>();

	    for (String version : versions) {

		final String[] vParts = version.split(";");

		final Version v = new Version(Integer.parseInt(vParts[0]), Integer.parseInt(vParts[1]), vParts[2].toUpperCase()
														 .startsWith("T"));

		vl.add(v);

	    }

	    final ActiveFeature af = new ActiveFeature(name, isEnabled, Collections.unmodifiableList(vl));

	    afs.add(af);

	}

	activeFeatures = Collections.unmodifiableList(afs);

	final String userLoc = engine.getConfig()
				     .getStringValue(KEY_USERS_LOC);

	log.info("Loading data from file: " + userLoc);

	final List<String> users = Utils.readFile(userLoc);

	log.info("Found " + lines.size() + " users");

	allowedUsers = Collections.unmodifiableList(users);

    }

    public List<ActiveFeature> getActiveFeatures() {
	return activeFeatures;
    }

    public List<String> getAllowedUsers() {
	return allowedUsers;
    }

}
