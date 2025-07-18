package sb.rocket.giovanniclient.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import sb.rocket.giovanniclient.client.config.ConfigManager; // Keep this import
import sb.rocket.giovanniclient.client.features.FeatureManager; // Keep this import
import sb.rocket.giovanniclient.client.features.updater.UpdateManager;
import sb.rocket.giovanniclient.client.util.Utils;

public class GiovanniClientClient implements ClientModInitializer {
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static final String MODID = "giovanniclient";
    public static final String MOD_VERSION_NAME = "0.0.1-SNAPSHOT";
    public static final int MOD_VERSION_CODE = 0x00001;

    // Only UpdateManager should be an instance if it holds state (like pendingUpdate)
    // ConfigManager and FeatureManager are assumed to be static utility classes.
    public static final UpdateManager UPDATE_MANAGER = new UpdateManager();

    @Override
    public void onInitializeClient() {
        // Correctly handling shutdown hook for ConfigManager - call static method directly
        Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager::shutdown));

        // register keybind to open the config
        KeyBinding openGioCliConfigKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("Open Config", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K,"GiovanniClient"));
        ClientTickEvents.END_CLIENT_TICK.register(tickClient -> {
            while (openGioCliConfigKey.wasPressed())
                tickClient.execute(ConfigManager::openConfigScreen); // Call static method directly

            if (ConfigManager.shouldOpenFromCommand) { // Access static field directly
                ConfigManager.shouldOpenFromCommand = false; // Access static field directly
                tickClient.execute(ConfigManager::openConfigScreen); // Call static method directly
            }
        });

        // register /giovanni command and aliases
        registerClientCommand();

        // init the mod
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            ConfigManager.init(); // Call static method directly
            FeatureManager.registerAll(); // Call static method directly

            // Example of using UPDATE_MANAGER if needed on startup
            // if (ConfigManager.getMainConfig().getAboutConfig().getAutoUpdateCheckOnStartup()) {
            //     UPDATE_MANAGER.checkForUpdate();
            // }

            Utils.chat("GiovanniClient initialized successfully! Version: " + MOD_VERSION_NAME);
        });
    }

    private void registerClientCommand() {
        String[] aliases = {"giovanni", "giovanniclient", "gio", "giocli", "giova", "zoo"};

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            for (String alias : aliases)
                dispatcher.register(ClientCommandManager.literal(alias)
                        .executes(context -> {
                            ConfigManager.openConfigScreenFromCommand(); // Call static method directly
                            return 1;
                        })
                );
        });
    }
}