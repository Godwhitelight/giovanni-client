package sb.rocket.giovanniclient.client;

import moe.nea.libautoupdate.PotentialUpdate;
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
import sb.rocket.giovanniclient.client.config.ConfigManager;
import sb.rocket.giovanniclient.client.features.FeatureManager;
import sb.rocket.giovanniclient.client.features.updater.UpdateManager;
import sb.rocket.giovanniclient.client.util.Utils; // Keep this import

import java.util.concurrent.CompletableFuture;

public class GiovanniClientClient implements ClientModInitializer {
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static final String MODID = "giovanniclient";
    public static final String MOD_VERSION_NAME = "0.0.1-SNAPSHOT";
    public static final int MOD_VERSION_CODE = 0x00001;

    public static final UpdateManager UPDATE_MANAGER = new UpdateManager();

    @Override
    public void onInitializeClient() {
        Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager::shutdown));

        KeyBinding openGioCliConfigKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("Open Config", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K,"GiovanniClient"));
        ClientTickEvents.END_CLIENT_TICK.register(tickClient -> {
            while (openGioCliConfigKey.wasPressed())
                tickClient.execute(ConfigManager::openConfigScreen);

            if (ConfigManager.shouldOpenFromCommand) {
                ConfigManager.shouldOpenFromCommand = false;
                tickClient.execute(ConfigManager::openConfigScreen);
            }
        });

        registerClientCommand();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            ConfigManager.init();
            Utils.init(ConfigManager.getConfig().dc);

            FeatureManager.registerAll();

            autoUpdateStuff();

            Utils.debug("GiovanniClient initialized successfully! Version: " + MOD_VERSION_NAME);
        });
    }

    private void registerClientCommand() {
        String[] aliases = {"giovanni", "giovanniclient", "gio", "giocli", "giova", "zoo"};

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            for (String alias : aliases)
                dispatcher.register(ClientCommandManager.literal(alias)
                        .executes(context -> {
                            ConfigManager.openConfigScreenFromCommand();
                            return 1;
                        })
                );
        });
    }

    private void autoUpdateStuff() {
        System.out.println("doing update stuff!");

        boolean autoCheck = ConfigManager.getConfig().about.AUTO_CHECK_FOR_UPDATES;
        boolean autoDownload = ConfigManager.getConfig().about.AUTO_UPDATE;

        if (autoCheck) {
            CompletableFuture<PotentialUpdate> checkFuture = UPDATE_MANAGER.checkForUpdate();

            if (autoDownload) {
                checkFuture.thenAccept(potentialUpdate -> {
                    System.out.println("" + potentialUpdate + " isav" + potentialUpdate.isUpdateAvailable());
                    if (potentialUpdate != null && potentialUpdate.isUpdateAvailable()) {
                        UPDATE_MANAGER.launchUpdate(potentialUpdate);
                    }
                }).exceptionally(ex -> {
                    Utils.error("Error during auto-update check chain: ", ex);
                    return null;
                });
            }
        }
    }
}