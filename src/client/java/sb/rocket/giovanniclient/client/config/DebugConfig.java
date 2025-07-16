package sb.rocket.giovanniclient.client.config;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class DebugConfig {
    @Expose
    @ConfigOption(name = "Toggle Debug Mode", desc = "rip 2 your chat")
    @ConfigEditorBoolean
    public boolean DEBUG = false;
}
