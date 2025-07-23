package sb.rocket.giovanniclient.client.features.updater;

import moe.nea.libautoupdate.PotentialUpdate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sb.rocket.giovanniclient.client.GiovanniClientClient;
import sb.rocket.giovanniclient.client.features.AbstractFeature;
import sb.rocket.giovanniclient.client.util.Utils;

import java.util.Random;

public class StartupMessageFeature extends AbstractFeature {

    private boolean messageSent = false;

    @Override
    public void onWorldLoad(MinecraftClient client) {
        if (!messageSent && !UpdateManager.updateScheduled) {
            sendUpdateMessage();
            messageSent = true;
        } else {
            if (new Random().nextInt(5) == 1 && !UpdateManager.updateScheduled)
                sendUpdateMessage();
        }
    }

    public static void sendUpdateMessage() {
        PotentialUpdate pendingUpdate = GiovanniClientClient.UPDATE_MANAGER.getPendingUpdate();

        if (pendingUpdate != null) {
            String version = pendingUpdate.getUpdate().getVersionName();

            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.inGameHud != null && client.inGameHud.getChatHud() != null) {

                MutableText fullMessage = Text.literal("\n\n\n===== ")
                        .setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true))
                        .append(Text.literal("GIOVANNI CLIENT").setStyle(Style.EMPTY.withColor(Formatting.AQUA).withBold(true)))
                        .append(Text.literal(" =====\n").setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true)))

                        .append(Text.literal("\nNEW UPDATE AVAILABLE! ").setStyle(Style.EMPTY.withColor(Formatting.RED).withBold(true)))
                        .append(Text.literal("Version: ").setStyle(Style.EMPTY.withColor(Formatting.WHITE)))
                        .append(Text.literal(version).setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true)))
                        .append(Text.literal("\n"))

                        .append(Text.literal("\nTo install: Run ").setStyle(Style.EMPTY.withColor(Formatting.GRAY).withBold(false)))
                        .append(Text.literal("/giovanni").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(false).withUnderline(true).withClickEvent(new ClickEvent.RunCommand("/giovanni"))))
                        .append(Text.literal(", then click 'Download Now'. The update will apply on your next game launch.").setStyle(Style.EMPTY.withColor(Formatting.GRAY).withBold(false)))

                        .append(Text.literal("\n\n=========================").setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true)));

                client.inGameHud.getChatHud().addMessage(fullMessage);

            } else {
                Utils.LOGGER.info("Attempted to send update message before chat was ready. Update version: {}", version);
            }
        } else {
            Utils.LOGGER.debug("No pending update found, skipping update message.");
        }
    }
}