package dylan.tide_api.data;

public class Version {

    private final int	  version;

    private final int	  releaseDate;

    private final boolean isValid;

    public Version(int version, int releaseDate, boolean isValid) {
	this.version = version;
	this.releaseDate = releaseDate;
	this.isValid = isValid;
    }

    public int getVersion() {
	return version;
    }

    public int getReleaseDate() {
	return releaseDate;
    }

    public boolean isValid() {
	return isValid;
    }

    @Override
    public String toString() {
	return "Version [version=" + version + ", releaseDate=" + releaseDate + ", isValid=" + isValid + "]";
    }

}
