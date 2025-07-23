package sb.rocket.giovanniclient.client.features.updater;

import moe.nea.libautoupdate.*;
import net.minecraft.client.MinecraftClient;
import sb.rocket.giovanniclient.client.GiovanniClientClient;
import sb.rocket.giovanniclient.client.features.AbstractFeature;
import sb.rocket.giovanniclient.client.util.Utils;

import java.util.concurrent.CompletableFuture;

import static sb.rocket.giovanniclient.client.GiovanniClientClient.MOD_VERSION_CODE;

public class UpdateManager extends AbstractFeature {

    // You might want to make this a singleton or inject it depending on your overall architecture.
    // For simplicity, we'll keep it as a regular class instance.

    private PotentialUpdate pendingUpdate = null;
    public static boolean updateScheduled = false;

    public PotentialUpdate getPendingUpdate() {
        return pendingUpdate;
    }

    /**
     * Initiates an asynchronous check for updates.
     * Informs the user about the result via Utils.chat().
     * Stores the potential update if available.
     *
     * @return A CompletableFuture that completes with the PotentialUpdate result.
     */
    public CompletableFuture<PotentialUpdate> checkForUpdate() {
        Utils.log("Checking for updates...");

        UpdateContext updateContext = new UpdateContext(
                UpdateSource.gistSource("GiovanniClient", "2570c325b01b3a20637b7d5855afe71d"),
                UpdateTarget.replaceJar(GiovanniClientClient.class),
                CurrentVersion.of(MOD_VERSION_CODE),
                "giovanni-client"
        );

        CompletableFuture<PotentialUpdate> future = updateContext.checkUpdate("giovanniclient-update-checker");

        future.thenAccept(potentialUpdate -> {
            if (potentialUpdate.isUpdateAvailable()) {
                this.pendingUpdate = potentialUpdate; // Store for later download
                Utils.chat("Update available! Click 'Download Now' to install it.");
            } else {
                this.pendingUpdate = null; // Clear if no update
                Utils.chat("No update available. You are on the latest version.");
            }
        }).exceptionally(ex -> {
            System.err.println("Failed to check for update: " + ex.getMessage());
            Utils.chat("Failed to check for updates: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        });

        return future; // Return the future if calling code needs to chain
    }

    /**
     * Launches the download and installation of a given PotentialUpdate.
     * Informs the user about the result via Utils.chat().
     *
     * @param potentialUpdate The update to launch.
     * @return A CompletableFuture that completes when the update process is finished.
     */
    public CompletableFuture<Void> launchUpdate(PotentialUpdate potentialUpdate) {
        if (potentialUpdate == null) {
            Utils.chat("Error: No update object provided for download.");
            return CompletableFuture.completedFuture(null);
        }

        System.out.println("BANANAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        Utils.chat("Downloading update...");

        CompletableFuture<Void> future = potentialUpdate.launchUpdate();

        future.thenRun(() -> {
            Utils.chat("Update downloaded! It will be applied on next game restart.");
            updateScheduled = true;
            this.pendingUpdate = null; // Clear after successful launch
        }).exceptionally(ex -> {
            System.err.println("Failed to launch update: " + ex.getMessage());
            Utils.chat("Failed to download update: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        });
        return future;
    }

    @Override
    public void onWorldLoad(MinecraftClient client) {
        Utils.log("we joined a world");
        new Thread(() -> {
            try {
                Thread.sleep(1000); // This sleep is on the new thread, won't freeze game
                Utils.debug("MyFeatureWithSeparateThread: Delay completed on separate thread.");

                // IMPORTANT: If you need to interact with Minecraft's state,
                // you must enqueue it to the main thread!
                MinecraftClient.getInstance().execute(() -> {
                    Utils.debug("MyFeatureWithSeparateThread: Executing Minecraft API call on main thread.");
                    if (client.player != null) {
                        // Example: Send a message to the player
                        Utils.chat("hello from delayed task");
                    }
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupted status
                Utils.debug("MyFeatureWithSeparateThread: Thread interrupted during delay.");
            }
        }, "MyMod-DelayedJoinTask").start(); // Give your thread a name for debugging
    }
}