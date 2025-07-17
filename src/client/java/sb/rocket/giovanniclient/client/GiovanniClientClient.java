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
import sb.rocket.giovanniclient.client.config.ConfigManager;
import sb.rocket.giovanniclient.client.features.FeatureManager;

public class GiovanniClientClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager::shutdown));
        MinecraftClient mc = MinecraftClient.getInstance();

        KeyBinding openKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.giovanniclient.open_config",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_K,
                        "category.giovanniclient"
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(tickClient -> {
            while (openKey.wasPressed())
                tickClient.execute(ConfigManager::openConfigScreen);

            if (ConfigManager.shouldOpenFromCommand) {
                ConfigManager.shouldOpenFromCommand = false;
                tickClient.execute(ConfigManager::openConfigScreen);
            }
        });

        registerClientCommand(); /* /giovanni and aliases */

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            ConfigManager.init();

            FeatureManager.registerAll();
        });
    }

    private void registerClientCommand() {

        String[] aliases = {"giovanni", "giovanniclient", "gio", "giocli", "giova"};


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
}