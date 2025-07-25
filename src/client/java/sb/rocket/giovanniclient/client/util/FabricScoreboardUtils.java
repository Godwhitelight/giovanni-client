// src/main/java/sb/rocket/giovanniclient/client/util/FabricScoreboardUtils.java
package sb.rocket.giovanniclient.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.text.MutableText; // Used for building formatted text
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FabricScoreboardUtils {

    /**
     * Cleans a string by removing only non-printable ASCII control characters (0-31 and 127).
     * This version does NOT remove Minecraft's color or formatting codes (e.g., §c, §l).
     * @param input The input string.
     * @return The cleaned string.
     */
    public static String stripNonPrintableAscii(String input) {
        StringBuilder cleaned = new StringBuilder();
        for (char c : input.toCharArray()) {
            if ((int) c >= 32 && (int) c < 127) { // Only keep printable ASCII
                cleaned.append(c);
            }
        }
        return cleaned.toString();
    }


    /**
     * Retrieves and processes scoreboard lines for a given ScoreboardObjective,
     * returning them as formatted Text components to preserve colors and styles.
     * @param objective The scoreboard objective to read from.
     * @return A list of formatted Text components representing the scoreboard lines.
     */
    public static List<Text> getFormattedScoreboardLines(ScoreboardObjective objective) {
        List<Text> lines = new ArrayList<>();
        if (objective == null) {
            return lines;
        }

        Scoreboard scoreboard = objective.getScoreboard();
        if (scoreboard == null) {
            return lines;
        }

        Collection<ScoreboardEntry> entries = scoreboard.getScoreboardEntries(objective);

        List<ScoreboardEntry> filteredAndSortedEntries = entries.stream()
                .filter(entry -> entry != null && entry.owner() != null && !entry.owner().startsWith("#"))
                .sorted(Comparator.comparingInt(ScoreboardEntry::value).reversed())
                .collect(Collectors.toList());

        List<ScoreboardEntry> relevantEntries;
        if (filteredAndSortedEntries.size() > 15) {
            relevantEntries = filteredAndSortedEntries.subList(filteredAndSortedEntries.size() - 15, filteredAndSortedEntries.size());
        } else {
            relevantEntries = filteredAndSortedEntries;
        }

        // Add the objective's display name as the first line, preserving its formatting
        if (objective.getDisplayName() != null) {
            lines.add(objective.getDisplayName());
        }

        for (ScoreboardEntry entry : relevantEntries) {
            Team team = scoreboard.getScoreHolderTeam(entry.owner());
            MutableText lineText;

            if (team != null) {
                // Combine prefix, owner name, and suffix as Text components
                lineText = Text.empty();
                if (team.getPrefix() != null) {
                    lineText.append(team.getPrefix());
                }
                lineText.append(Text.literal(entry.owner())); // Owner is a String, make it Text
                if (team.getSuffix() != null) {
                    lineText.append(team.getSuffix());
                }
            } else {
                // If no team, just the owner's name as Text
                lineText = Text.literal(entry.owner());
            }
            lines.add(lineText);
        }

        return lines;
    }


    /**
     * Gets the currently displayed sidebar scoreboard objective based on InGameHud logic.
     * @param client The MinecraftClient instance.
     * @return The ScoreboardObjective for the sidebar, or null if none found.
     */
    public static ScoreboardObjective getSidebarObjective(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            return null;
        }

        Scoreboard scoreboard = client.world.getScoreboard();
        if (scoreboard == null) {
            return null;
        }

        ScoreboardObjective scoreboardObjective = null;
        Team team = scoreboard.getScoreHolderTeam(client.player.getNameForScoreboard());
        if (team != null) {
            ScoreboardDisplaySlot scoreboardDisplaySlot = ScoreboardDisplaySlot.fromFormatting(team.getColor());
            if (scoreboardDisplaySlot != null) {
                scoreboardObjective = scoreboard.getObjectiveForSlot(scoreboardDisplaySlot);
            }
        }

        return scoreboardObjective != null ? scoreboardObjective : scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
    }


    // --- Deprecated / Raw String Reading Methods (for internal string comparisons if needed) ---
    // These methods return raw strings without Minecraft formatting.
    // Use them if you need to compare raw text content without colors/styles.

    /**
     * Retrieves and processes scoreboard lines for a given ScoreboardObjective,
     * returning them as unformatted raw strings.
     * @param objective The scoreboard objective to read from.
     * @return A list of unformatted raw strings representing the scoreboard lines.
     * @deprecated Use {@link #getFormattedScoreboardLines(ScoreboardObjective)} for displaying,
     *             or for comparisons, consider using a cleaned version of the Text component directly.
     */
    @Deprecated
    public static List<String> getRawScoreboardLines(ScoreboardObjective objective) {
        List<String> lines = new ArrayList<>();
        if (objective == null) {
            return lines;
        }

        Scoreboard scoreboard = objective.getScoreboard();
        if (scoreboard == null) {
            return lines;
        }

        Collection<ScoreboardEntry> entries = scoreboard.getScoreboardEntries(objective);

        List<ScoreboardEntry> filteredAndSortedEntries = entries.stream()
                .filter(entry -> entry != null && entry.owner() != null && !entry.owner().startsWith("#"))
                .sorted(Comparator.comparingInt(ScoreboardEntry::value).reversed())
                .collect(Collectors.toList());

        List<ScoreboardEntry> relevantEntries;
        if (filteredAndSortedEntries.size() > 15) {
            relevantEntries = filteredAndSortedEntries.subList(filteredAndSortedEntries.size() - 15, filteredAndSortedEntries.size());
        } else {
            relevantEntries = filteredAndSortedEntries;
        }

        if (objective.getDisplayName() != null) {
            lines.add(objective.getDisplayName().getString()); // .getString() removes Minecraft formatting
        }

        for (ScoreboardEntry entry : relevantEntries) {
            Team team = scoreboard.getScoreHolderTeam(entry.owner());
            if (team != null) {
                String prefix = team.getPrefix() != null ? team.getPrefix().getString() : "";
                String suffix = team.getSuffix() != null ? team.getSuffix().getString() : "";
                lines.add(prefix + entry.owner() + suffix);
            } else {
                lines.add(entry.owner());
            }
        }

        return lines;
    }


    /**
     * Checks if any *raw* scoreboard line from the sidebar contains the given string.
     * This uses unformatted strings for comparison.
     * @param searchString The string to search for.
     * @return True if found, false otherwise.
     */
    public static boolean scoreboardContainsRaw(String searchString) {
        MinecraftClient client = MinecraftClient.getInstance();
        ScoreboardObjective sidebarObjective = getSidebarObjective(client);
        if (sidebarObjective == null) return false;

        List<String> scoreboardLines = getRawScoreboardLines(sidebarObjective);
        for (String line : scoreboardLines) {
            if (line.contains(searchString)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the first *raw* scoreboard line from the sidebar that contains the given string.
     * This uses unformatted strings.
     * @param searchString The string to search for.
     * @return The matching line, or null if not found.
     */
    public static String getRawLineThatContains(String searchString) {
        MinecraftClient client = MinecraftClient.getInstance();
        ScoreboardObjective sidebarObjective = getSidebarObjective(client);
        if (sidebarObjective == null) return null;

        for (String line : getRawScoreboardLines(sidebarObjective)) {
            if (line.contains(searchString)) {
                return line;
            }
        }
        return null;
    }
}