package de.eldoria.eldoutilities.utils;

import java.util.Optional;
import java.util.function.Function;

public class ArgumentUtils {
    /**
     * Get a string value or a default value when the index does not exists.
     *
     * @param arguments    array of string arguments.
     * @param index        index of the requested parameter
     * @param defaultValue default value which will be returned when the index does not exists.
     * @return string at index or default value if the index does not exists.
     */
    public String getOrDefault(String[] arguments, int index, String defaultValue) {
        String arg = get(arguments, index);
        return arg == null ? defaultValue : arg;
    }

    /**
     * Get the index from the string array .
     *
     * @param arguments array of string arguments.
     * @param index     index of the requested parameter
     * @return string or null if the index does not exists
     */
    public String get(String[] arguments, int index) {
        if (arguments.length > index) return arguments[index];
        return null;
    }


    /**
     * Get the index parsed.
     *
     * @param arguments array of string arguments.
     * @param index     index of the requested parameter
     * @param parse     function to parse the string.
     * @param <T>       type of optional return value
     * @return the string of the index after the parse function was applied or a empty optional when the index was not found.
     */
    public <T> Optional<T> get(String[] arguments, int index, Function<String, T> parse) {
        if (arguments.length > index) {
            parse.apply(arguments[index]);
        }
        return Optional.empty();
    }

    /**
     * Gets a optional parameter.
     * Will return the default value when the index is not inside the array.
     *
     * @param arguments    array of string arguments.
     * @param index        index of the requested parameter
     * @param defaultValue default value which will be delivered when the index does not exists.
     * @param parse        function to parse the string.
     * @param <T>          type of the returned parameter.
     * @return parsed string value at index or default value if the index does not exists
     */
    public <T> T getOptionalParameter(String[] arguments, int index, T defaultValue, Function<String, T> parse) {
        String arg = get(arguments, index);
        if (arg == null) return defaultValue;
        return parse.apply(arg);
    }
}
