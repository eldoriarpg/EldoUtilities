package de.eldoria.eldoutilities.utils;

/**
 * This class contains methods to parse string to enums
 */
public final class EnumUtil {
    private EnumUtil() {
        throw new UnsupportedOperationException("This is a utility class!");
    }

    /**
     * Searches for a enum value by string with a case insensitive search.
     *
     * @param string enum as string value
     * @param values enum values.
     * @param <T>    type of enum.
     *
     * @return enum value or null if no mathing value was found.
     */
    public static <T extends Enum<T>> T parse(String string, Class<T> values) {
        return parse(string, values, false);
    }

    /**
     * Searches for a enum value by string with a case insensitive search.
     *
     * @param string       enum as string value
     * @param values       enum values.
     * @param stripStrings if true underscores will be removed before checking
     * @param <T>          type of enum.
     *
     * @return enum value or null if no mathing value was found.
     */
    public static <T extends Enum<T>> T parse(String string, Class<T> values, boolean stripStrings) {
        for (T value : values.getEnumConstants()) {
            if (string.equalsIgnoreCase(stripStrings ? value.name().replace("_", "") : value.name())) {
                return value;
            }
        }
        return null;
    }
}
