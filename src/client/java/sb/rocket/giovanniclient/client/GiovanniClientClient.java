package sb.rocket.giovanniclient.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import sb.rocket.giovanniclient.client.config.ConfigManager;
import sb.rocket.giovanniclient.client.features.FeatureManager;

public class GiovanniClientClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager::shutdown));

        KeyBinding openKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.giovanniclient.open_config",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_K,
                        "category.giovanniclient"
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(tickClient -> {
            while (openKey.wasPressed()) {
                tickClient.execute(ConfigManager::openConfigScreen);
            }
        });


        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            ConfigManager.init();

            FeatureManager.registerAll();
        });
    }
}