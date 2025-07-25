// src/main/java/sb/rocket/giovanniclient/client/command/ScoreboardCommand.java
package sb.rocket.giovanniclient.client.util;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import net.minecraft.command.CommandRegistryAccess;
import sb.rocket.giovanniclient.client.util.FabricScoreboardUtils;

import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class ScoreboardCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("sidebar")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();

                    ScoreboardObjective sidebarObjective = FabricScoreboardUtils.getSidebarObjective(client);

                    if (sidebarObjective == null) {
                        context.getSource().sendFeedback(Text.literal("No scoreboard sidebar currently displayed."));
                        return 0;
                    }

                    // *** IMPORTANT CHANGE: Call getFormattedScoreboardLines which returns List<Text> ***
                    List<Text> lines = FabricScoreboardUtils.getFormattedScoreboardLines(sidebarObjective);

                    if (lines.isEmpty()) {
                        context.getSource().sendFeedback(Text.literal("Sidebar scoreboard is empty or only contains non-displayable entries."));
                        return 0;
                    }

                    context.getSource().sendFeedback(Text.literal("--- Scoreboard Sidebar ---"));
                    for (Text line : lines) { // Iterate over Text objects
                        context.getSource().sendFeedback(Text.of(line.getString())); // Send the Text object directly to chat
                    }
                    context.getSource().sendFeedback(Text.literal("--------------------------"));

                    return 1;
                })
        );
    }
}