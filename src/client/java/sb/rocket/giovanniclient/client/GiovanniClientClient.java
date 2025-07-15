package sb.rocket.giovanniclient.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import sb.rocket.giovanniclient.client.config.ConfigManager;
import sb.rocket.giovanniclient.client.features.autosolvers.AutoExperiments;
import sb.rocket.giovanniclient.client.features.autosolvers.AutoFusion;
import sb.rocket.giovanniclient.client.features.autosolvers.AutoMelody;

public class GiovanniClientClient implements ClientModInitializer {

    // Declare the keybinding as a field so it can be accessed by the tick event
    private static KeyBinding openKey; // Made static for simpler access, or could be instance field

    @Override
    public void onInitializeClient() {
        // The only thing that is truly safe to do immediately is register a shutdown hook.
        Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager::shutdown));

        // 1) Register your keybind (THIS GOES HERE!)
        // Keybindings should be registered during ClientModInitializer.onInitializeClient()
        // or any other time before GameOptions has been initialized (which happens very early).
        openKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.giovanniclient.open_config",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_K,
                        "category.giovanniclient"
                )
        );

        // 2) Register the listener that USES the keybind.
        // This can also be done directly in onInitializeClient() as ClientTickEvents
        // doesn't directly touch game options, but its execution waits for ticks.
        ClientTickEvents.END_CLIENT_TICK.register(tickClient -> {
            while (openKey.wasPressed()) {
                // Use the client instance provided by the event, which is safe here.
                tickClient.execute(ConfigManager::openConfigScreen);
            }
        });


        // Everything else that touches Minecraft or its libraries and needs to wait for the client to be ready,
        // such as loading config or registering features that interact with the game world,
        // should remain inside CLIENT_STARTED.
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            // --- IT IS NOW SAFE TO INITIALIZE EVERYTHING ELSE ---

            // Load or create your config
            ConfigManager.init();

            // Register your features
            AutoMelody.register();
            AutoExperiments.register();
            AutoFusion.register();
        });
    }
}