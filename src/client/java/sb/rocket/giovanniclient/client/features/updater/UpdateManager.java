package sb.rocket.giovanniclient.client.features.updater;

import moe.nea.libautoupdate.*;
import sb.rocket.giovanniclient.client.GiovanniClientClient;
import sb.rocket.giovanniclient.client.util.Utils;

import java.util.concurrent.CompletableFuture;

import static sb.rocket.giovanniclient.client.GiovanniClientClient.MOD_VERSION_CODE;

public class UpdateManager {

    // You might want to make this a singleton or inject it depending on your overall architecture.
    // For simplicity, we'll keep it as a regular class instance.

    private PotentialUpdate pendingUpdate = null;

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
        Utils.chat("Checking for updates...");

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

        Utils.chat("Downloading update...");

        CompletableFuture<Void> future = potentialUpdate.launchUpdate();

        future.thenRun(() -> {
            Utils.chat("Update downloaded! It will be applied on next game restart.");
            this.pendingUpdate = null; // Clear after successful launch
        }).exceptionally(ex -> {
            System.err.println("Failed to launch update: " + ex.getMessage());
            Utils.chat("Failed to download update: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        });
        return future;
    }
}