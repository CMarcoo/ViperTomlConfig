package studio.thevipershow.vtc;

import java.io.File;
import org.jetbrains.annotations.NotNull;

/**
 * The Configuration interface:
 * This interface represents any configuration file.
 * The configuration itself may still be embedded into the JAR
 * or already exported into the server (see {@link #exportResource(boolean)}).
 * It provides several methods to obtain file information, save resources, or
 * interact with them.
 */
public interface Configuration {

    /**
     * Searches between the embedded resources for the configuration file,
     * and saves it into the server's directory.
     * This method should fail silently and do nothing when the configuration
     * is already present in the plugin's server folder.
     *
     * @param replace True if the old config should be replaced entirely, false otherwise.
     */
    void exportResource(boolean replace);

    /**
     * Get the file associated with this configuration.
     * The file may not exist because the resource has been
     * embedded and hasn't been exported to the server still.
     *
     * @return The file reference to the Configuration file.
     */
    @NotNull File getFile();
}
