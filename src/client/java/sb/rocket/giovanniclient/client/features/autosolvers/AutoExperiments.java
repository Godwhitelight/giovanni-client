package sb.rocket.giovanniclient.client.features.autosolvers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import sb.rocket.giovanniclient.client.config.ConfigManager;
import sb.rocket.giovanniclient.client.util.Utils;

import java.util.*;

public class AutoExperiments {
    private enum ExperimentType {
        CHRONOMATRON,
        ULTRASEQUENCER,
        SUPERPAIRS, // not implemented
        END,
        NONE
    }

    // You may ask me, what are these numbers based on? You see, that's a great question, basically
    private final int START_DELAY_MIN = 234; // ms
    private final int START_DELAY_MAX = 678;

    private final int END_DELAY_MIN = 777;
    private final int END_DELAY_MAX = 3333;

    private final AutoSolversConfig cfg = ConfigManager.getConfig().asc;
    private final Random rng = new Random();

    private ExperimentType currentExperiment = ExperimentType.NONE;
    private final ArrayList<Integer> chronomatronOrder = new ArrayList<>(28);
    private final HashMap<Integer, Integer> ultrasequencerOrder = new HashMap<>();

    private int lastAdded = 0, clicks = 0;
    private long startDelay = -1, endDelay = -1, clickDelay = -1;
    private boolean sequenceAdded = false;

    /** Call once during client init **/
    public static void register() {
        AutoExperiments inst = new AutoExperiments();
        ScreenEvents.AFTER_INIT.register((mc, screen, w, h) ->
                inst.onScreenInit(mc, screen));
        ClientTickEvents.END_CLIENT_TICK.register(inst::onTick);
    }

    // old onGuiOpen
    private void onScreenInit(MinecraftClient client, Screen screen) {
        if (!cfg.AUTOEXPERIMENTS_TOGGLE) {
            clearAll();
            return;
        }
        // we only care about chest‐style GUIs
        if (!(screen instanceof GenericContainerScreen)) {
            clearAll();
            return;
        }

        String chestName = screen
                .getTitle().getString();
        if (chestName.startsWith("Chronomatron (")) {
            Utils.debug("Chronomatron detected");
            currentExperiment = ExperimentType.CHRONOMATRON;
        } else if (chestName.startsWith("Ultrasequencer (")) {
            Utils.debug("Ultrasequencer detected");
            currentExperiment = ExperimentType.ULTRASEQUENCER;
        } else if (chestName.startsWith("Superpairs(")) {
            Utils.debug("Superpairs detected");
            currentExperiment = ExperimentType.SUPERPAIRS;
        } else if (chestName.contains("Over")) {
            Utils.debug("Experiment over.");
            currentExperiment = ExperimentType.END;
        } else clearAll();
    }

    private void onTick(MinecraftClient client) {

        if (!cfg.AUTOEXPERIMENTS_TOGGLE
                || currentExperiment == ExperimentType.NONE
                || client.player == null) {
            return;
        }

        if (!(client.currentScreen instanceof GenericContainerScreen)) {
            clearAll();
            return;
        }
        ScreenHandler handler = client.player.currentScreenHandler;
        ItemStack center = handler.slots.get(49).getStack();

        long rightNow = System.currentTimeMillis();

        // Small random startup delay (200-600ms)
        if (startDelay == -1) {
            startDelay = rightNow + new Random().nextInt(START_DELAY_MAX - START_DELAY_MIN) + START_DELAY_MIN;
            Utils.debug("Start delay: " + (startDelay - rightNow));
        }

        if (rightNow < startDelay) return;

        // DEBUG: what *is* in the centre slot?
        Utils.debug("Center slot: item="
                + center.getItem().toString()
                + "  hasGlint=" + center.hasEnchantments());

        switch (currentExperiment) {
            case CHRONOMATRON -> tickChrono(client, handler, rightNow);
            case ULTRASEQUENCER -> tickUltra(client, handler, rightNow);
            case END -> tickEnd(client, handler, rightNow);
            default -> {}
        }
    }

    private void tickEnd(MinecraftClient client, ScreenHandler handler, long rightNow) {
        if (endDelay == -1) {
            endDelay =  rightNow + new Random().nextInt(END_DELAY_MAX - END_DELAY_MIN) + END_DELAY_MIN;
            Utils.debug("End delay: " + (endDelay-rightNow) + "ms");
        }

        if (rightNow > endDelay && cfg.AUTOEXPERIMENTS_AUTOQUIT) {
            // if (container.getSlot(11).getStack().getItem() == Items.skull) {
            client.player.closeHandledScreen();
            endDelay = -1;
            currentExperiment = ExperimentType.NONE;
        }
    }

    private void tickChrono(MinecraftClient client,
                            ScreenHandler handler,
                            long rightNow) {

        ItemStack itemStageFlag = handler.slots.get(49).getStack();
        DefaultedList<Slot> container = handler.slots;

        // LIGHTGEM is supposed to be glowstone btw. Wtf notch?
        if (itemStageFlag.isOf(Items.GLOWSTONE) &&
                !container.get(lastAdded).getStack().hasGlint()) {  // Changed from hasEnchantments to hasGlint
            sequenceAdded = false;
            if (chronomatronOrder.size() > (11 - cfg.METAPHYSICAL_SERUM.toInt())) {
                client.player.closeHandledScreen();
            }
        }

        // saves the sequence
        if (!sequenceAdded && itemStageFlag.isOf(Items.CLOCK)) {
            Utils.debug("salviamo");

            // Add debugging to see what's in the slots
            for (int i = 10; i <= 43; i++) {
                ItemStack stack = container.get(i).getStack();
                if (!stack.isEmpty()) {
                    Utils.debug("Slot " + i + ": item=" + stack.getItem() +
                            ", hasEnchantments=" + stack.hasEnchantments() +
                            ", hasGlint=" + stack.hasGlint() +
                            ", count=" + stack.getCount());
                }
            }

            boolean foundGlowing = false;
            for (int i = 10; i <= 43; i++) {
                ItemStack stack = container.get(i).getStack();
                if (!stack.isEmpty() && stack.hasGlint()) {  // Changed from hasEnchantments to hasGlint
                    chronomatronOrder.add(i);
                    Utils.debug("aggiungo " + i);
                    lastAdded = i;
                    sequenceAdded = true;
                    clicks = 0;
                    foundGlowing = true;
                    break;
                }
            }

            if (!foundGlowing) {
                Utils.debug("No glowing items found, might be waiting for sequence to appear");
                sequenceAdded = true;
            }
        }

        // clicks through the saved sequence, also has random delays
        if (sequenceAdded && itemStageFlag.isOf(Items.CLOCK) &&
                chronomatronOrder.size() > clicks) {

            if (clickDelay == -1) {
                clickDelay =  rightNow + new Random().nextInt(cfg.AUTOEXPERIMENTS_CLICK_DELAY_MAX - cfg.AUTOEXPERIMENTS_CLICK_DELAY_MIN) + cfg.AUTOEXPERIMENTS_CLICK_DELAY_MIN;
                Utils.debug("Note n" + (clicks+1) + ", Click delay: " + (clickDelay-rightNow) + "ms");
            }

            if (rightNow > clickDelay) {
                clickSlot(client, handler, chronomatronOrder.get(clicks));
                clicks++;
                clickDelay = -1;
            }
        }
    }

    private void tickUltra(MinecraftClient client,
                           ScreenHandler handler,
                           long rightNow) {

        Utils.debug("debug: siamo dentro l'ultra");

        ItemStack itemStageFlag = handler.slots.get(49).getStack();
        DefaultedList<Slot> container = handler.slots;

        // check to see if we're supposed to click or save the sequence
        if (itemStageFlag.isOf(Items.CLOCK))
            sequenceAdded = false;

        // saves the sequence and exists if we're done
        if (!sequenceAdded && itemStageFlag.isOf(Items.GLOWSTONE)) {
            if (!container.get(44).hasStack()) return;

            ultrasequencerOrder.clear();

            for (int slot = 9; slot <= 44; slot++) {
                Item currentItem = container.get(slot).getStack().getItem();
                if (currentItem instanceof DyeItem
                        || currentItem == Items.BONE_MEAL
                        || currentItem == Items.INK_SAC
                        || currentItem == Items.LAPIS_LAZULI
                        || currentItem == Items.COCOA_BEANS)
                    ultrasequencerOrder.put(container.get(slot).getStack().getCount() - 1, slot);
            }

            sequenceAdded = true;
            clicks = 0;
        }

        if (itemStageFlag.isOf(Items.CLOCK) &&
                ultrasequencerOrder.containsKey(clicks)) {

            if (clickDelay == -1) {
                clickDelay =  rightNow + new Random().nextInt(cfg.AUTOEXPERIMENTS_CLICK_DELAY_MAX - cfg.AUTOEXPERIMENTS_CLICK_DELAY_MIN) + cfg.AUTOEXPERIMENTS_CLICK_DELAY_MIN;
                Utils.debug("Note n" + (clicks+1) + ", Click delay: " + (clickDelay-rightNow) + "ms");
            }

            if (rightNow > clickDelay) {
                // exits once we're done
                if (ultrasequencerOrder.size() > (9 - cfg.METAPHYSICAL_SERUM.toInt()))
                    client.player.closeHandledScreen();

                Integer slotNumber = ultrasequencerOrder.get(clicks);
                if (slotNumber != null) {
                    clickSlot(client, handler, slotNumber);
                    clicks++;
                    clickDelay = -1;
                }
            }
        }

    }


    private void clearAll() {
        currentExperiment = ExperimentType.NONE;
        chronomatronOrder.clear();
        ultrasequencerOrder.clear();
        sequenceAdded = false;
        lastAdded = 0;
        clickDelay = -1;
        endDelay = -1;
        startDelay = -1;
    }


    private void clickSlot(MinecraftClient client,
                           ScreenHandler handler,
                           int slot) {
        assert client.interactionManager != null;
        client.interactionManager.clickSlot(
                handler.syncId,
                slot,
                0,
                cfg.AUTOMELODY_CLICKTYPE,
                client.player
        );
    }
}