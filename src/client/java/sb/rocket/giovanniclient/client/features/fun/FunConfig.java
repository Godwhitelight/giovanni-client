package sb.rocket.giovanniclient.client.features.fun;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class FunConfig {
    @Expose
    @ConfigOption(name = "Fake Ironman", desc = "Tony Stark")
    @ConfigEditorBoolean
    public boolean FAKE_IRONMAN_TOGGLE = false;
    public final String FAKE_IRONMAN_PREFIX = "♲: ";
}
