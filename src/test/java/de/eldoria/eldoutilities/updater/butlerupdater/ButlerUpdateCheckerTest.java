package de.eldoria.eldoutilities.updater.butlerupdater;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.logging.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ButlerUpdateCheckerTest {
    public Plugin plugin = mock(Plugin.class);
    public PluginDescriptionFile descr = mock(PluginDescriptionFile.class);

    @BeforeEach
    public void buildAssets() {
        plugin = mock(Plugin.class);
        descr = mock(PluginDescriptionFile.class);
        when(descr.getVersion()).thenReturn("0.1");
        when(descr.getName()).thenReturn("TestPlugin");
        when(descr.getWebsite()).thenReturn("www.dummywebsite.de");
        when(plugin.getDescription()).then((Answer<PluginDescriptionFile>) invocation -> descr);
        when(plugin.getName()).thenReturn("TestPlugin");
        when(plugin.getLogger()).thenReturn(Logger.getAnonymousLogger());
        when(plugin.getDataFolder()).thenReturn(new File(home() + "/TestPlugin"));
    }

    @Test
    public void downloadTest() throws IOException {
        new ButlerUpdateChecker(new ButlerUpdateData(plugin, "someperm", false, true, 3, "https://plugins.eldoria.de"));
        File newUpdate = new File(Paths.get(home(), "update").toString());
        Assertions.assertTrue(newUpdate.exists());
        Assertions.assertTrue(new File(newUpdate.getAbsolutePath() + "/TestPlugin.jar").exists());

        File update = new File(Paths.get(home(), "update").toString());
        if (update.exists()) {
            Files.walk(update.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            assert !update.exists();
        }
    }

    @Test
    public void latestTest() {
        when(descr.getVersion()).thenReturn("0.2");
        new ButlerUpdateChecker(new ButlerUpdateData(plugin, "someperm", false, true, 3, "https://plugins.eldoria.de"));
        File newUpdate = new File(Paths.get(home(), "update").toString());
        Assertions.assertFalse(newUpdate.exists());
        Assertions.assertFalse(new File(newUpdate.getAbsolutePath() + "/TestPlugin.jar").exists());
    }

    public static String home() {
        return new File(".").getAbsoluteFile().getParentFile().toString();
    }
}
