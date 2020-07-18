package de.eldoria.eldoutilities.crossversion;

public class UnsupportedVersionException extends RuntimeException {
    public UnsupportedVersionException(ServerVersion version) {
        super("Version " + version.name());
    }
}
