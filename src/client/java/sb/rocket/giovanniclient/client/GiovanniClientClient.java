package sb.rocket.giovanniclient.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import io.github.notenoughupdates.moulconfig.managed.ManagedConfig;
import sb.rocket.giovanniclient.client.config.ConfigManager;
import sb.rocket.giovanniclient.client.features.autosolvers.AutoExperiments;
import sb.rocket.giovanniclient.client.features.autosolvers.AutoMelody;

public class GiovanniClientClient implements ClientModInitializer {
    private ManagedConfig config;

    @Override
    public void onInitializeClient() {
        // 1) load or create your config
        ConfigManager.init();

        // 2) register your AutoMelody handlers.
        //    internally it will no-op if autoMelodyToggle == false
        AutoMelody.register();
        AutoExperiments.register();

        // 3) register your keybind to open the moulconfig screen
        KeyBinding openKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.giovanniclient.open_config",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_K,
                        "category.giovanniclient"
                )
        );
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openKey.wasPressed()) {
                MinecraftClient.getInstance()
                        .execute(ConfigManager::openConfigScreen);
            }
        });

        // 4) clean‐up save thread
        Runtime.getRuntime().addShutdownHook(
                new Thread(ConfigManager::shutdown)
        );
    }
}