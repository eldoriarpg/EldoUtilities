package de.eldoria.eldoutilities.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TextUtilTest {

    @Test
    void countChars() {
    }

    @Test
    void cropText() {
    }

    @Test
    void prettyDuration() {
        Assertions.assertEquals("1:00", TextUtil.prettyDuration(60));
        Assertions.assertEquals("0:59", TextUtil.prettyDuration(59));
        Assertions.assertEquals("1:01", TextUtil.prettyDuration(61));
        Assertions.assertEquals("1:00:00", TextUtil.prettyDuration(60*60));
    }
}