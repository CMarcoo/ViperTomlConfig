package studio.thevipershow.vtc;


import java.io.File;
import java.io.IOException;
import java.util.Objects;
import lombok.Getter;
import lombok.var;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

/**
 * This class is an implementation of AbstractPluginConfiguration and ConfigurationContent.
 * Its purpose is to provide a good feature set for your TOML configurations, providing:
 * - file checking {@link #checkFileStatus(File)}
 * - parsing {@link #storeContent()}
 *
 * @param <T> Your plugin type.
 */
public abstract class AbstractTomlConfiguration<T extends JavaPlugin> extends AbstractPluginConfiguration<T> implements ConfigurationContent {

    @Getter
    protected TomlParseResult tomlParseResult;

    private static void checkFileStatus(@NotNull File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException(String.format("The file at location %s did not exist.", file.getAbsolutePath()));
        } else if (!file.canRead()) {
            throw new IllegalArgumentException(String.format("The file at location %s could not be read.", file.getAbsolutePath()));
        } else if (file.isDirectory()) {
            throw new IllegalArgumentException(String.format("The file at location %s should not have been a directory.", file.getAbsolutePath()));
        } else if (!file.getName().endsWith(".toml")) {
            throw new IllegalArgumentException(String.format("The file at location %s should have been a TOML file.", file.getAbsolutePath()));
        }
    }

    /**
     * Constructor for abstract class:
     *
     * @param javaPlugin            Your plugin instance.
     * @param configurationFilename The name of the configuration, including extension.
     */
    public AbstractTomlConfiguration(@NotNull T javaPlugin, @NotNull String configurationFilename) {
        super(javaPlugin, configurationFilename);
    }

    /**
     * Try and parse the data from the configuration
     * in order to save it.
     */
    @Override
    public void storeContent() {
        checkFileStatus(configurationFile);
        var path = configurationFile.toPath();
        try {
            this.tomlParseResult = Objects.requireNonNull(Toml.parse(path), "Could not parse TOML correctly for file " + configurationFilename);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @return True if any content has been parsed,
     * false if no data is available
     */
    @Override
    public boolean isContentAvailable() {
        return this.tomlParseResult != null;
    }

    /**
     * Delete all the content that is, or is not,
     * present in the current parsed data.
     * This will make {@link #isContentAvailable()}
     * return false until other changes are made.
     */
    @Override
    public void deleteContent() {
        this.tomlParseResult = null;
    }
}
