package sb.rocket.giovanniclient.client.config;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.Config;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorButton;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import sb.rocket.giovanniclient.client.GiovanniClientClient; // Import your main class
import sb.rocket.giovanniclient.client.util.Utils;

public class AboutConfig extends Config {
    // Cooldown for check button
    private long lastCheckButtonClickTime = 0;
    private final long CHECK_COOLDOWN_MILLIS = 5000; // 5 seconds

    // Cooldown for download button
    private long lastDownloadButtonClickTime = 0;
    private final long DOWNLOAD_COOLDOWN_MILLIS = 5000; // 5 seconds

    // We no longer need a direct instance here, we'll use the static one from GiovanniClientClient
    // private final UpdateManager updateManager = new UpdateManager();

    @Expose
    @ConfigOption(name = "Check for Updates", desc = "Manually check if an update is available.")
    @ConfigEditorButton(buttonText = "Check Now")
    public Runnable checkUpdateButton = () -> {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastCheckButtonClickTime < CHECK_COOLDOWN_MILLIS) {
            long remainingSeconds = (CHECK_COOLDOWN_MILLIS - (currentTime - lastCheckButtonClickTime)) / 1000 + 1;
            Utils.chat("Update check is on cooldown! Please wait " + remainingSeconds + " seconds.");
            return;
        }

        // Delegate the check to the global UpdateManager instance
        GiovanniClientClient.UPDATE_MANAGER.checkForUpdate();

        lastCheckButtonClickTime = currentTime; // Reset cooldown
    };

    @Expose
    @ConfigOption(name = "Download Update", desc = "Download and install the available update (only if a check found one).")
    @ConfigEditorButton(buttonText = "Download Now")
    public Runnable downloadUpdateButton = () -> {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastDownloadButtonClickTime < DOWNLOAD_COOLDOWN_MILLIS) {
            long remainingSeconds = (DOWNLOAD_COOLDOWN_MILLIS - (currentTime - lastDownloadButtonClickTime)) / 1000 + 1;
            Utils.chat("Download is on cooldown! Please wait " + remainingSeconds + " seconds.");
            return;
        }

        // Get the pending update from the global UpdateManager
        if (GiovanniClientClient.UPDATE_MANAGER.getPendingUpdate() == null) {
            Utils.chat("No update available to download. Please check for updates first.");
            return;
        }

        // Delegate the download to the global UpdateManager
        GiovanniClientClient.UPDATE_MANAGER.launchUpdate(GiovanniClientClient.UPDATE_MANAGER.getPendingUpdate());

        lastDownloadButtonClickTime = currentTime; // Reset cooldown
    };
}