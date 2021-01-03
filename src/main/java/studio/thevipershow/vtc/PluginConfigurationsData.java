package studio.thevipershow.vtc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import lombok.var;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Your plugin configurations data.
 * MUST BE ANNOTATED WITH YOUR MAIN PLUGIN TYPE.
 *
 * @param <P> Your plugin type.
 */
@RequiredArgsConstructor
@Getter
public final class PluginConfigurationsData<P extends JavaPlugin> {

    private final P javaPlugin;
    @Setter
    private boolean consoleDebuggingInfo = true;
    private final Map<ConfigurationData<P>, TomlSectionConfiguration<P, ?>> loadedTomlConfigs = new HashMap<>();

    private TomlSectionConfiguration<P, ?> tryBuild(final Class<?>[] constructorArgs, ConfigurationData<P> configurationData, Object... initargs) {
        try {
            Class<? extends TomlSectionConfiguration<P, ?>> tomlConfClass = configurationData.getTomlSectionClass();
            Constructor<? extends TomlSectionConfiguration<P, ?>> tomlConfConstructor = tomlConfClass.getConstructor(constructorArgs);
            return tomlConfConstructor.newInstance(initargs);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String numberOrderedPrint(LinkedList<String> strings) {
        val builder = new StringBuilder();
        strings.iterator().forEachRemaining(str -> builder.append("\n- ").append(str));
        return builder.toString();
    }

    /**
     * Load all of the configurations from an enum class
     * that contains all of the available configurations.
     *
     * @param configurationsEnumClass The class for the enum.
     * @param <T>                     The type of your plugin.
     */
    public final <T extends Enum<T> & ConfigurationData<P>> void loadAllConfigs(@NotNull Class<? extends T> configurationsEnumClass) {
        var logger = javaPlugin.getLogger();

        val configEnumConstants = configurationsEnumClass.getEnumConstants();
        var loadedConfigs = 0;
        val amountToLoad = configEnumConstants.length;
        val unloadedConfigNames = new LinkedList<String>();

        if (consoleDebuggingInfo) {
            logger.info(String.format("Starting to load configs for Plugin \"%s\".", javaPlugin.getName()));
            if (amountToLoad != 0) {
                logger.info(String.format("%d configurations are expected to be loaded.", configEnumConstants.length));
            }
        }

        for (final T configType : configEnumConstants) {
            val start = System.nanoTime();
            val configName = configType.name();
            Class<? extends SectionType> section = configType.getSectionClass();

            if (consoleDebuggingInfo) {
                logger.info("Loading config " + configName);
            }

            val CONSTRUCTOR_ATTEMPTS = new Class<?>[][]{{javaPlugin.getClass()}, {javaPlugin.getClass(), String.class}, {javaPlugin.getClass(), String.class, configType.getSectionClass()}};
            val INITARGS_ATTEMPTS = new Object[][]{{javaPlugin}, {javaPlugin, configType.getConfigurationName()}, {javaPlugin, configName, section}};

            TomlSectionConfiguration<P, ?> instance = null;

            if (CONSTRUCTOR_ATTEMPTS.length != INITARGS_ATTEMPTS.length) {
                throw new RuntimeException("Constructor and Initargs attempts should have equal size.");
            }

            for (int uwu = 0; uwu < 3; uwu++) {
                if (instance != null) {
                    break;
                }
                instance = tryBuild(CONSTRUCTOR_ATTEMPTS[uwu], configType, INITARGS_ATTEMPTS[uwu]);
            }

            if (instance == null) {
                unloadedConfigNames.offerLast(configType.getConfigurationName());
            } else {
                this.loadedTomlConfigs.put(configType, instance);
                loadedConfigs++;
            }

            if (consoleDebuggingInfo)
                logger.info(String.format("Finished loading config in %.3f milliseconds.", Math.ceil((System.nanoTime() - start) / 1E6)));
        }

        if (amountToLoad == 0 || !consoleDebuggingInfo) return;

        if (amountToLoad != loadedConfigs) {
            logger.warning(String.format("Plugin only loaded %d configs out of %d!", loadedConfigs, amountToLoad));
            logger.warning("Configurations that did not load are listed below:");
            logger.warning(numberOrderedPrint(unloadedConfigNames));
        } else {
            logger.info(String.format("All %d configs have been loaded correctly :)", loadedConfigs));
        }
    }

    /**
     * Get all of the currently loaded configs in this plugin configuration data instance,
     * it exports them (overriding or not) and stores them into data map.
     *
     * @param replaceAll Whether if previous config with identical names should be replaced.
     */
    public final void exportAndLoadAllLoadedConfigs(boolean replaceAll) {
        for (final TomlSectionConfiguration<P, ?> configuration : loadedTomlConfigs.values()) {
            configuration.exportResource(replaceAll);
            configuration.storeContent();
            configuration.loadAllValues();
        }
    }

    /**
     * Get the configuration file from the loaded data.
     * The method may return null if you haven't loaded
     * that specific configuration.
     *
     * @param sectionEnum The config enum entry.
     * @param <T>         The Section type.
     * @param <S>         .
     * @return The config if found or null.
     */
    @Nullable
    public final <S extends Enum<S> & ConfigurationData<P>, Q extends Enum<Q> & SectionType, T extends TomlSectionConfiguration<P, Q>> T getConfig(@NotNull S sectionEnum) {
        return (T) this.loadedTomlConfigs.get(sectionEnum);
    }
}
