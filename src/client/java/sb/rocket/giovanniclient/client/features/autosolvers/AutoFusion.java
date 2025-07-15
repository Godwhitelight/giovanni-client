package sb.rocket.giovanniclient.client.features.autosolvers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import sb.rocket.giovanniclient.client.config.ConfigManager;
import sb.rocket.giovanniclient.client.util.Utils;

import java.util.Random;

public class AutoFusion {
    private enum State {
        FUSION_BOX,
        CONFIRM_FUSION,
        NONE
    }

    // You may ask me, what are these numbers based on? You see, that's a great question, basically
    private final int START_DELAY_MIN = 234; // ms
    private final int START_DELAY_MAX = 678;


    private final AutoSolversConfig cfg = ConfigManager.getConfig().asc;
    private final Random rng = new Random();

    private AutoFusion.State currentState = State.NONE;

    private long startDelay = -1, endDelay = -1, clickDelay = -1;

    /** Call once during client init **/
    public static void register() {
        AutoFusion inst = new AutoFusion();
        ScreenEvents.AFTER_INIT.register((mc, screen, w, h) ->
                inst.onScreenInit(mc, screen));
        ClientTickEvents.END_CLIENT_TICK.register(inst::onTick);
    }

    // old onGuiOpen
    private void onScreenInit(MinecraftClient client, Screen screen) {
        if (!cfg.autoExperimentsAccordion.AUTOEXPERIMENTS_TOGGLE)
            return;

        // we only care about chest‐style GUIs
        if (!(screen instanceof GenericContainerScreen))
            return;

        String chestName = screen
                .getTitle().getString();
        if (chestName.contains("Fusion Box")) {
            Utils.debug("Fusion Box detected");
            currentState = State.FUSION_BOX;
        } else if (chestName.contains("Confirm Fusion")) {
            Utils.debug("Confirm Fusion detected");
            currentState = State.CONFIRM_FUSION;
        } else currentState = State.NONE;
    }

    private void onTick(MinecraftClient client) {

        if (!cfg.autoFusionAccordtion.AUTOFUSION
                || currentState == State.NONE
                || client.player == null) {
            return;
        }

        if (!(client.currentScreen instanceof GenericContainerScreen)) {
            currentState = State.NONE;
            return;
        }

        ScreenHandler handler = client.player.currentScreenHandler;

        long rightNow = System.currentTimeMillis();

        // Small random startup delay (200-600ms)
        if (startDelay == -1) {
            startDelay = rightNow + new Random().nextInt(START_DELAY_MAX - START_DELAY_MIN) + START_DELAY_MIN;
            Utils.debug("Start delay: " + (startDelay - rightNow));
        }

        if (rightNow < startDelay) return;

        switch (currentState) {
            case State.FUSION_BOX -> repeatFusion(client, handler, rightNow);
            case State.CONFIRM_FUSION -> confirmFusion(client, handler, rightNow);
            default -> currentState = State.NONE;
        }
    }

    private void repeatFusion(MinecraftClient client,
                              ScreenHandler handler,
                              long rightNow) {
        DefaultedList<Slot> chest = handler.slots;
        int slot = 47;

        if (chest.get(slot).getStack().getItem().toString() .equals("minecraft:player_head")) {
            if (clickDelay == -1) {
                clickDelay =  rightNow + rng.nextInt(cfg.autoFusionAccordtion.AUTOFUSION_CLICK_DELAY_MAX - cfg.autoFusionAccordtion.AUTOFUSION_CLICK_DELAY_MIN) + cfg.autoFusionAccordtion.AUTOFUSION_CLICK_DELAY_MIN;
                Utils.debug("Click delay: " + (clickDelay-rightNow) + "ms");
            }

            if (rightNow > clickDelay) {
                clickSlot(client, handler, slot);
                clickDelay = -1;
            }
        }


    }

    private void confirmFusion(MinecraftClient client,
                               ScreenHandler handler,
                               long rightNow) {
        DefaultedList<Slot> chest = handler.slots;
        int slot = 33;
        if (chest.get(slot).getStack().getItem().toString() .equals("minecraft:lime_terracotta")) {
            if (clickDelay == -1) {
                clickDelay =  rightNow + rng.nextInt(cfg.autoFusionAccordtion.AUTOFUSION_CLICK_DELAY_MAX - cfg.autoFusionAccordtion.AUTOFUSION_CLICK_DELAY_MIN) + cfg.autoFusionAccordtion.AUTOFUSION_CLICK_DELAY_MIN;
                Utils.debug("Click delay: " + (clickDelay-rightNow) + "ms");
            }

            if (rightNow > clickDelay) {
                clickSlot(client, handler, slot);
                clickDelay = -1;
            }
        }
    }

    private void clickSlot(MinecraftClient client,
                           ScreenHandler handler,
                           int slot) {
        assert client.interactionManager != null;
        client.interactionManager.clickSlot(
                handler.syncId,
                slot,
                0,
                SlotActionType.PICKUP,
                client.player
        );
    }

}
