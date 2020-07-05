package de.eldoria.eldoutilities.utils;

public final class TextUtil {
    private TextUtil() {
        throw new UnsupportedOperationException("This is a utility class!");
    }

    public static int countChars(String string, char count) {
        int i = 0;
        for (char c : string.toCharArray()) {
            if (c == count) i++;
        }
        return i;
    }
}
