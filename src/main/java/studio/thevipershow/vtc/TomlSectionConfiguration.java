package studio.thevipershow.vtc;

import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.var;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is an implementation of AbstractTomlConfiguration that allows you to read, store, and modify parsed values.
 * It uses a enum dependency approach so you can simply write the nodes in an enum with their type, and you will
 * be able to automatically load them.
 *
 * @param <T> Your plugin type.
 * @param <S> The enum type with all of your config sections.
 */
@Getter
public abstract class TomlSectionConfiguration<T extends JavaPlugin, S extends Enum<S> & SectionType> extends AbstractTomlConfiguration<T> implements ValuesLoader<S> {

    private final Map<S, Object> configurationValues;
    protected final Class<? extends S> enumTypeClass;
    protected final Set<S> enumClassValues;

    /**
     * Constructor for abstract class:
     *
     * @param javaPlugin            Your plugin instance.
     * @param configurationFilename The name of the configuration, including extension.
     * @param enumTypeClass         The class of the Enum containing all of the sections for this config.
     */
    public TomlSectionConfiguration(@NotNull T javaPlugin, @NotNull String configurationFilename, @NotNull Class<? extends S> enumTypeClass) {
        super(javaPlugin, configurationFilename);
        this.enumTypeClass = Objects.requireNonNull(enumTypeClass, "Provided an invalid section enum class for this config.");
        this.enumClassValues = new HashSet<>();
        enumClassValues.addAll(ImmutableSet.copyOf(enumTypeClass.getEnumConstants()));
        this.configurationValues = new HashMap<>();
    }

    /**
     * Use the sections in the specified enum
     * to load all of the values from the config.
     * Null values will throw NPEs.
     */
    @Override
    public void loadAllValues() {
        this.enumClassValues.forEach(this::loadValue);
    }

    /**
     * Load a single value.
     *
     * @param s The value to load.
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public void loadValue(@NotNull S s) {
        var stringData = s.getStringData();
        var read = Objects.requireNonNull(tomlParseResult.get(stringData), String.format("Reading field %s from config %s returned null.", stringData, super.configurationFilename));
        if (read != null) {
            this.configurationValues.put(s, read);
        }
    }

    /**
     * Get the value from the configuration using one of its enum values.
     * This method perform additional check before casting. It is exceptions safe.
     *
     * @param value       The enum that represents the config field.
     * @param returnClass The type of data that should be returned by this config
     *                    (note that you should never use primitive classes!)
     * @param <Q>         The type of return.
     * @return The value obtained from the config cast to the return type
     * @throws IllegalArgumentException If the cast is unsuccessful.
     */
    @Override
    public <Q> Q getConfigValue(@NotNull S value, @NotNull Class<? extends Q> returnClass) {
        if (configurationValues.containsKey(value)) {
            var obtained = configurationValues.get(value);
            if (returnClass.isAssignableFrom(obtained.getClass()))
                return Objects.requireNonNull((Q) obtained);
            else
                throw new IllegalArgumentException(String.format("The return type for %s inside config %s was %s but it has been tried to be cast to %s.",
                        value.getStringData(), getClass().getSimpleName(), obtained.getClass().getSimpleName(), returnClass.getSimpleName()));
        } else {
            return null;
        }
    }


    /**
     * Get the value from the configuration using one of its enum values.
     *
     * @param value The enum that represents the config field.
     * @param <Q>   The type of return (will throw UnsupportedCastException if invalid).
     * @return The value obtained from the config cast to the return type.
     */
    @Override
    @Nullable
    public <Q> Q getConfigValue(@NotNull S value) {
        if (configurationValues.containsKey(value)) {
            var obtained = configurationValues.get(value);
            return Objects.requireNonNull((Q) obtained);
        } else {
            return null;
        }
    }
}
