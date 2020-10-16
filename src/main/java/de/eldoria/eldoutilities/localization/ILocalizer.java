package de.eldoria.eldoutilities.localization;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public interface ILocalizer {
    Map<Class<? extends Plugin>, ILocalizer> LOCALIZER = new HashMap<>();
    ILocalizer DEFAULT = new DummyLocalizer();

    void setLocale(String language);

    String getMessage(String key, Replacement... replacements);

    String[] getIncludedLocales();

    static ILocalizer getPluginLocalizer(Plugin plugin) {
        return getPluginLocalizer(plugin.getClass());
    }

    static ILocalizer getPluginLocalizer(Class<? extends Plugin> plugin) {
        return LOCALIZER.getOrDefault(plugin, DEFAULT);
    }

    /**
     * Create a new localizer instance with default values.
     * <p>
     * The message path and prefix will be messages and the fallback language the "en_US" locale.
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
     * @param includedLocales internal provided locales
     */
    static ILocalizer create(Plugin plugin, String language,
                             String... includedLocales) {
        return create(plugin, language, "messages", "messages", Locale.US, includedLocales);
    }

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
    static ILocalizer create(Plugin plugin, String language, String localesPath,
                             String localesPrefix, Locale fallbackLocale, String... includedLocales) {
        ILocalizer localizer = new Localizer(plugin, localesPath, localesPrefix, fallbackLocale, includedLocales);
        localizer.setLocale(language);
        LOCALIZER.put(plugin.getClass(), localizer);
        return localizer;
    }
}
