package studio.thevipershow.vtc;

import java.io.File;
import java.util.Objects;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * This an abstract implementation of the Configuration.
 * This class defines all of Configuration methods allowing
 * you to save your resource and get the file of the configuration.
 * It is essential that the type argument of this class is the same
 * of your plugin.
 *
 * @param <T> Your plugin type.
 */
public abstract class PluginConfiguration<T extends JavaPlugin> implements Configuration {

    @Getter
    protected final T javaPlugin;
    protected final File configurationFile;
    protected final String configurationFilename;

    /**
     * Constructor for abstract class:
     *
     * @param javaPlugin            Your plugin instance.
     * @param configurationFilename The name of the configuration, including extension.
     */
    protected PluginConfiguration(@NotNull T javaPlugin, @NotNull String configurationFilename) {
        this.javaPlugin = Objects.requireNonNull(javaPlugin, "You have provided a null plugin instance!");
        this.configurationFilename = Objects.requireNonNull(configurationFilename, "You have provided a null configuration filename!");
        this.configurationFile = new File(javaPlugin.getDataFolder(), configurationFilename);
    }

    /**
     * Searches between the embedded resources for the configuration file,
     * and saves it into the server's directory.
     * This method should fail silently and do nothing when the configuration
     * is already present in the plugin's server folder.
     *
     * @param replace True if the old config should be replaced entirely, false otherwise.
     */
    @Override
    public void exportResource(boolean replace) {
        this.javaPlugin.saveResource(this.configurationFilename, replace);
    }

    /**
     * Get the file associated with this configuration.
     * The file may not exist because the resource has been
     * embedded and hasn't been exported to the server still.
     *
     * @return The file reference to the Configuration file.
     */
    @Override
    public @NotNull File getFile() {
        return this.configurationFile;
    }
}
