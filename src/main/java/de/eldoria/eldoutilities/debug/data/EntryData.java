package de.eldoria.eldoutilities.debug.data;

public class EntryData {
    protected String name;
    protected String content;

    /**
     * Create a new debug entry
     *
     * @param name    Name of debug entry. This is the name displayed on the web page
     * @param content content of the debug entry.
     */
    public EntryData(String name, String content) {
        this.name = name;
        this.content = content;
    }
}
