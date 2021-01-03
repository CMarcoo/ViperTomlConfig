package studio.thevipershow.vtc;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PluginsConfigurationsManager {

    private static final PluginsConfigurationsManager instance = new PluginsConfigurationsManager();

    public static synchronized PluginsConfigurationsManager getInstance() {
        return instance;
    }

    @Getter
    private final Map<JavaPlugin, PluginConfigurationsData<?>> pluginConfigDataMap = new HashMap<>();

    /**
     * This method loads your configurations data using a config list enum.
     *
     * @param yourPlugin  Your plugin instance.
     * @param <P>         Your plugin type.
     * @param <T>         The enum config type.
     * @return Your plugin data.
     */
    public final <P extends JavaPlugin, T extends Enum<T> & SectionType> @NotNull PluginConfigurationsData<P> loadPluginData(@NotNull P yourPlugin) {
        var pluginData = new PluginConfigurationsData<P>(Objects.requireNonNull(yourPlugin, "Your plugin instance was null!"));
        this.pluginConfigDataMap.putIfAbsent(yourPlugin, pluginData);
        return pluginData;
    }

    /**
     * Get your registered plugin configs data.
     *
     * @param yourPlugin Your plugin instance.
     * @param <P>        The type of your plugin.
     * @return Your data if loaded, null otherwise.
     */
    @Nullable
    public final <P extends JavaPlugin> PluginConfigurationsData<P> getPluginData(@NotNull Class<PluginConfigurationsData<P>> type, @NotNull P yourPlugin) {
        if (this.pluginConfigDataMap.containsKey(Objects.requireNonNull(yourPlugin, "Your plugin instance was null!")))
            return type.cast(this.pluginConfigDataMap.get(yourPlugin));
        else
            return null;
    }
}
