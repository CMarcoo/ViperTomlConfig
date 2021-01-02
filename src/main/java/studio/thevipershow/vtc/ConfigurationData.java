package studio.thevipershow.vtc;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Data for a configuration enum entry.
 */
public interface ConfigurationData<P extends JavaPlugin> {

    /**
     * Get the class of the toml section config.
     *
     * @return Its class.
     */
    Class<? extends TomlSectionConfiguration<P, ?>> getTomlSectionClass();

    /**
     * Get the section enum of this class.
     *
     * @param <T> The enum type.
     * @return The class of the enum section .
     */
    <T extends Enum<T> & SectionType> Class<? extends T> getSectionClass();

    /**
     * Get the name of this configuration.
     *
     * @return The name of this configuration.
     */
    String getConfigurationName();
}
