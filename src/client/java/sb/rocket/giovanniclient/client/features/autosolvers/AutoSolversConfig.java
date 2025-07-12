package sb.rocket.giovanniclient.client.features.autosolvers;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class AutoSolversConfig {
    @Expose
    @ConfigOption(name = "AutoMelody", desc = "")
    @ConfigEditorBoolean
    public boolean autoMelodyToggle = false;
}