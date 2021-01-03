package studio.thevipershow.vtc;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface used to implement values loading
 * and multi values loading.
 *
 * @param <T> The type of values that will be loaded.
 */
public interface ValuesLoader<T> {

    /**
     * Load all of the currently available values.
     */
    void loadAllValues();

    /**
     * Load a single value.
     *
     * @param value The value to load.
     */
    void loadValue(@NotNull T value);

    /**
     * Get the value from the configuration using one of its enum values.
     *
     * @param value  The enum that represents the config field.
     * @param returnClass The type of data that should be returned by this config
     *                   (note that you should never use primitive classes!)
     * @param <S>        The type of return.
     * @throws IllegalArgumentException If the cast is unsuccessful.
     * @return The value obtained from the config cast to the return type
     */
    @Nullable
    <S> S getConfigValue(@NotNull T value, @NotNull Class<? extends S> returnClass);

    @Nullable
    <S> S getConfigValue(@NotNull Class<S> type, @NotNull T value);
}
