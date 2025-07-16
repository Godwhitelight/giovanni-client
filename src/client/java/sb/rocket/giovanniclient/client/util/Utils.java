package sb.rocket.giovanniclient.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sb.rocket.giovanniclient.client.config.ConfigManager;
import sb.rocket.giovanniclient.client.config.DebugConfig;

public class Utils {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final DebugConfig cfg = ConfigManager.getConfig().dc;

    public static void out(String s) {
        if (client != null && client.inGameHud != null) {

            Text prefix = Text.literal("Giovanni > ")
                    .setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE));

            Text message = Text.literal(s)
                    .setStyle(Style.EMPTY.withColor(Formatting.WHITE));

            client.inGameHud.getChatHud().addMessage(prefix.copy().append(message));
        }
    }

    public static void debug(String s) {
        if (cfg.DEBUG && client != null && client.inGameHud != null) {

            Text prefix = Text.literal("Giovanni DEBUG > ")
                .setStyle(Style.EMPTY.withColor(Formatting.RED));

            Text message = Text.literal(s)
                .setStyle(Style.EMPTY.withColor(Formatting.WHITE));

            client.inGameHud.getChatHud().addMessage(prefix.copy().append(message));
        }
    }
}
