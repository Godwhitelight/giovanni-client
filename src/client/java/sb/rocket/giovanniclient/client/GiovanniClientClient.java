package sb.rocket.giovanniclient.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import org.lwjgl.glfw.GLFW;

import io.github.notenoughupdates.moulconfig.common.IMinecraft;
import io.github.notenoughupdates.moulconfig.managed.ManagedConfig;
import sb.rocket.giovanniclient.client.TestConfig;

import java.io.File;

public class GiovanniClientClient implements ClientModInitializer {
    private ManagedConfig config;

    @Override
    public void onInitializeClient() {
        // Create managed config
        config = ManagedConfig.create(
                new File("config/giovanniclient/config.json"),
                TestConfig.class
        );

        // Register keybinding
        KeyBinding openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.giovanniclient.open_config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.giovanniclient"
        ));

        // Register tick handler
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openKey.wasPressed()) {
                MinecraftClient.getInstance().send(() -> {
                    var editor = config.getEditor();
                    IMinecraft.instance.openWrappedScreen(editor);
                });
            }
        });
    }
}