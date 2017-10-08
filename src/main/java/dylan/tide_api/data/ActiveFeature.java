package dylan.tide_api.data;

import java.util.List;

public class ActiveFeature {

    private final String	name;

    private final boolean	enabled;

    private final List<Version>	versions;

    public ActiveFeature(String name, boolean enabled, List<Version> versions) {
	this.name = name;
	this.enabled = enabled;
	this.versions = versions;
    }

    public String getName() {
	return name;
    }

    public boolean isEnabled() {
	return enabled;
    }

    public List<Version> getVersions() {
	return versions;
    }

    @Override
    public String toString() {
	return "ActiveFeature [name=" + name + ", enabled=" + enabled + "]";
    }

}
