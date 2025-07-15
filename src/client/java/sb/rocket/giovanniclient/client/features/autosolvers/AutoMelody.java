package sb.rocket.giovanniclient.client.features.autosolvers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import sb.rocket.giovanniclient.client.config.ConfigManager;

import java.util.ArrayList;
import java.util.List;

public class AutoMelody {
    private boolean inHarp = false;
    private final List<Item> lastInventory = new ArrayList<>();
    private int counter = 0;
    private int lastClickedSlot = -1;

    /** Call once during client init: */
    public static void register() {
        AutoMelody instance = new AutoMelody();

        // Detect when any "container" GUI opens
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) ->
                instance.onScreenInit(screen)
        );

        // Run logic every client tick
        ClientTickEvents.END_CLIENT_TICK.register(client ->
                instance.onTick(client)
        );
    }

    private void onScreenInit(Screen screen) {
        if (!(screen instanceof GenericContainerScreen gui)) return;
        String title = gui.getTitle().getString();
        if (title.startsWith("Harp -")) {
            lastInventory.clear();
            inHarp = true;
        }
    }

    private void onTick(MinecraftClient client) {
        if (client.player == null) return;

        // two-tick spacing
        if (++counter % 2 == 0) return;

        if (!inHarp || !ConfigManager.getConfig().asc.AUTOMELODY_TOGGLE) return;

        Screen current = client.currentScreen;
        if (!(current instanceof GenericContainerScreen gui)) {
            inHarp = false;
            return;
        }
        String title = gui.getTitle().getString();
        if (!title.startsWith("Harp -")) {
            inHarp = false;
            return;
        }

        ScreenHandler handler = client.player.currentScreenHandler;
        List<Item> thisInventory = new ArrayList<>();
        for (int i = 0; i < handler.slots.size(); i++) {
            ItemStack stack = handler.slots.get(i).getStack();
            if (!stack.isEmpty()) thisInventory.add(stack.getItem());
        }

        if (!lastInventory.toString().equals(thisInventory.toString())) {
            for (int i = 0; i < handler.slots.size(); i++) {
                ItemStack stack = handler.slots.get(i).getStack();
                if (stack.isOf(Blocks.QUARTZ_BLOCK.asItem())) {
                    client.interactionManager.clickSlot(
                            handler.syncId,
                            i,
                            0,
                            //ConfigManager.getConfig().asc.AUTOMELODY_CLICKTYPE,
                            SlotActionType.CLONE,
                            client.player
                    );
                    lastClickedSlot = i;
                    break;
                }
            }
        }

        lastInventory.clear();
        lastInventory.addAll(thisInventory);
    }
}