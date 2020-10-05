package de.eldoria.eldoutilities.localization;

import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Compact localizer class.
 * <p>
 * Easy to use and fully automatic setup and updating of locales.
 * <p>
 * Requires to have at least one default locale and one fallback locale in the resources. Use the {@link
 * #Localizer(Plugin, String, String, String, Locale, String...)} constructor for initial setup. This will create
 * missing files and updates existing files.
 * <p>
 * You can change the currently used locale every time via {@link #setLocale(String)}.
 * <p>
 * The localizer also allows to use locales which are not included in the ressources folder.
 */
public class Localizer {

    private final ResourceBundle fallbackLocaleFile;
    private final Plugin plugin;
    private final String localesPath;
    private final String localesPrefix;
    private final String[] includedLocales;
    private final Pattern localePattern = Pattern.compile("([a-zA-Z]{2})_([a-zA-Z]{2})\\.properties");
    private ResourceBundle localeFile;

    /**
     * Create a new localizer instance.
     * <p>
     * This instance will create locale files, which are provided in the resources directory.
     * <p>
     * After this it will updates all locale files inside the locales directory. For this the ref keys from the internal
     * default locale file will be used.
     * <p>
     * After a update check and a update if needed it will load the provided language or the fallback language if the
     * provided language does not exists.
     *
     * @param plugin          instance of plugin
     * @param language        language which should be used if existent
     * @param localesPath     path of the locales directory
     * @param localesPrefix   prefix of the locale files
     * @param fallbackLocale  fallbackLocale
     * @param includedLocales internal provided locales
     */
    public Localizer(Plugin plugin, String language, String localesPath,
                     String localesPrefix, Locale fallbackLocale, String... includedLocales) {
        this.plugin = plugin;
        this.localesPath = localesPath;
        this.localesPrefix = localesPrefix;
        this.includedLocales = includedLocales;
        fallbackLocaleFile = ResourceBundle.getBundle(localesPrefix, fallbackLocale);
        createOrUpdateLocaleFiles();
        setLocale(language);
    }

    /**
     * Change the locale to the language. If the locale is not present the fallback locale will be used.
     *
     * @param language language to be used
     */
    public void setLocale(String language) {
        String localeFile = localesPrefix + "_" + language + ".properties";

        try {
            this.localeFile = new PropertyResourceBundle(
                    Files.newInputStream(Paths.get(plugin.getDataFolder().toString(), localesPath, localeFile)));
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Could not load locale file " + Paths.get(localesPath, localeFile).toString(), e);
            this.localeFile = fallbackLocaleFile;
        }
    }

    /**
     * Translates a String with Placeholders. Can handle multiple messages with replacements. Add replacements in the
     * right order.
     *
     * @param key          Key of message
     * @param replacements Replacements in the right order.
     *
     * @return Replaced Messages
     */
    public String getMessage(String key, Replacement... replacements) {
        String result = null;
        if (localeFile.containsKey(key)) {
            result = localeFile.getString(key);
            if (result.isEmpty()) {
                if (fallbackLocaleFile.containsKey(key)) {
                    result = fallbackLocaleFile.getString(key);
                }
            }
        } else if (fallbackLocaleFile.containsKey(key)) {
            result = fallbackLocaleFile.getString(key);
        }

        if (result == null) {
            plugin.getLogger().warning("Key " + key + " is missing in fallback file.");
            return "";
        }

        for (Replacement replacement : replacements) {
            result = replacement.invoke(result);
        }

        return result;
    }

    private void createOrUpdateLocaleFiles() {
        Path messages = Paths.get(plugin.getDataFolder().toString(), localesPath);

        // Make sure that the messages directory exists.
        if (!messages.toFile().exists()) {
            boolean mkdir = messages.toFile().mkdir();
            if (!mkdir) {
                plugin.getLogger().log(Level.WARNING, "Failed to create locale directory.");
                return;
            }
        }


        // Create the property files if they do not exists.
        for (String includedLocale : includedLocales) {
            String filename = localesPrefix + "_" + includedLocale + ".properties";

            File localeFile = Paths.get(messages.toString(), filename).toFile();
            if (localeFile.exists()) {
                continue;
            }

            StringBuilder builder = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    plugin.getResource(filename), StandardCharsets.UTF_8))) {
                String line = bufferedReader.readLine();
                while (line != null) {
                    builder.append(line).append("\n");
                    line = bufferedReader.readLine();
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Could not load resource " + filename + ".", e);
            } catch (NullPointerException e) {
                plugin.getLogger().log(Level.WARNING, "Locale " + includedLocale + " could not be loaded but should exists.", e);
                continue;
            }

            try (OutputStreamWriter outputStream = new OutputStreamWriter(new FileOutputStream(localeFile), StandardCharsets.UTF_8)) {
                outputStream.write(builder.toString());

            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to create default message file " + localeFile.getName() + ".", e);
                continue;
            }
            plugin.getLogger().info("Created default locale " + filename);
        }

        List<File> localeFiles = new ArrayList<>();

        // Load all property files
        try (Stream<Path> message = Files.list(messages)) {
            for (Path path : message.collect(Collectors.toList())) {
                // skip directories. why should they be there anyway?
                if (path.toFile().isDirectory()) {
                    continue;
                }

                // Lets be a bit nice with the formatting. not everyone knows the ISO.
                if (path.toFile().getName().matches(localesPrefix + "_[a-zA-Z]{2}_[a-zA-Z]{2}\\.properties")) {
                    localeFiles.add(path.toFile());
                } else {
                    // Notify the user that he did something weird in his messages directory.
                    plugin.getLogger().info(path.toString() + " is not a valid message file. Skipped.");
                }
            }
        } catch (IOException e) {
            // we will try to continue with the successfull loaded files. If there are none thats not bad.
            plugin.getLogger().log(Level.WARNING, "Failed to load message files.");
        }

        // get the default pack to have a set of all needed keys. Hopefully its correct.
        ResourceBundle defaultBundle = ResourceBundle.getBundle(localesPrefix);

        if (defaultBundle == null) {
            // How should we update withour a reference?
            plugin.getLogger().warning("No reference locale found. Please report this to the application owner");
            return;
        }

        // Update keys of existing files.
        for (File file : localeFiles) {

            // Load ref sheet.
            Enumeration<String> defaultKeys = defaultBundle.getKeys();

            // try to search for a included updated version.
            Locale currLocale = extractLocale(file.getName());
            ResourceBundle refBundle;

            if (currLocale != null) {
                refBundle = ResourceBundle.getBundle(localesPrefix, currLocale);
                if (refBundle == null) {
                    refBundle = defaultBundle;
                } else {
                    // Check if the found bundle is not the default one.
                    if (refBundle.getLocale().getLanguage().trim().isEmpty()) {
                        refBundle = null;
                    }
                }
            } else {
                plugin.getLogger().warning("Could not determine locale code of file " + file.getName());
                refBundle = defaultBundle;
            }

            // TODO: Preserve commands for properties.
            // load the external property file.
            Map<String, String> treemap = new TreeMap<>(String::compareToIgnoreCase);
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                String line = bufferedReader.readLine();
                while (line != null) {
                    String[] split = line.split("=", 2);
                    if (split.length == 2) {
                        treemap.put(split[0], split[1]);
                    }
                    line = bufferedReader.readLine();
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Could not update locale " + file.getName() + ".", e);
            }

            Set<String> keys = treemap.keySet();

            boolean updated = false;
            // check if ref key is in locale
            while (defaultKeys.hasMoreElements()) {
                String currKey = defaultKeys.nextElement();
                if (keys.contains(currKey)) continue;
                String value = "";
                if (refBundle != null) {
                    value = refBundle.containsKey(currKey) ? refBundle.getString(currKey) : "";
                }
                // Add the property with the value if it exists in a internal file.
                treemap.put(currKey, value);
                plugin.getLogger().info("Added: " + currKey + "=" + value.replace("\n", "\\n"));
                updated = true;
            }

            // Write to file if updated.
            if (updated) {
                try (OutputStreamWriter outputStream = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                    outputStream.write("# File automatically updated at "
                            + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "\n");
                    for (Map.Entry<String, String> entry : treemap.entrySet()) {
                        outputStream.write(entry.getKey() + "=" + entry.getValue().replace("\n", "\\n") + "\n");
                    }
                } catch (IOException e) {
                    plugin.getLogger().log(Level.WARNING, "Could not update locale " + file.getName() + ".", e);
                    continue;
                }
                plugin.getLogger().info("Updated locale " + file.getName() + ". Please check you translation.");
            } else {
                plugin.getLogger().info("Locale " + file.getName() + " is up to date.");
            }
        }
    }

    private Locale extractLocale(String filename) {
        Matcher matcher = localePattern.matcher(filename);
        if (matcher.find()) {
            return new Locale(matcher.group(1), matcher.group(2));
        }
        String[] s = filename.split("_", 2);
        if (s.length == 2 && localesPrefix.equalsIgnoreCase(s[0])) return new Locale(s[1]);
        return null;
    }

    /**
     * Get currently registered locales.
     *
     * @return array of available locales.
     */
    public String[] getIncludedLocales() {
        return includedLocales;
    }
}
