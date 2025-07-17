package sb.rocket.giovanniclient.client.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;

public abstract class AbstractFeature {
    public abstract void onScreenOpen(Screen screen);
    public abstract void onTick(MinecraftClient client);

    protected boolean isChestScreen(Screen screen) {
        return screen instanceof GenericContainerScreen;
    }

    protected boolean isPlayerInScreen(MinecraftClient client, String titlePrefix) {
        return client.currentScreen instanceof GenericContainerScreen screen
                && screen.getTitle().getString().startsWith(titlePrefix);
    }
}
