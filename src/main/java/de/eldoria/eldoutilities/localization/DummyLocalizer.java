package de.eldoria.eldoutilities.localization;

import java.util.Map;

public class DummyLocalizer implements ILocalizer {
    @Override
    public void setLocale(String language) {

    }

    @Override
    public String getMessage(String key, Replacement... replacements) {
        return key;
    }

    @Override
    public String[] getIncludedLocales() {
        return new String[0];
    }

    @Override
    public void addLocaleCodes(Map<String, String> runtimeLocaleCodes) {
    }
}
