package sb.rocket.giovanniclient.client.features.autosolvers;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDropdown;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import net.minecraft.screen.slot.SlotActionType;

public class AutoSolversConfig {

    @Expose
    @ConfigOption(name = "AutoMelody", desc = "")
    @ConfigEditorBoolean
    public boolean AUTOMELODY_TOGGLE = false;

    @Expose
    @ConfigOption(name = "Click Type", desc = "Changes how to interact with the items in the harp.")
    @ConfigEditorDropdown
    public SlotActionType AUTOMELODY_CLICKTYPE; // good for experiments too

    @Expose
    @ConfigOption(name = "AutoExperiments", desc = "")
    @ConfigEditorBoolean
    public boolean AUTOEXPERIMENTS_TOGGLE = false;

    @Expose
    @ConfigOption(name = "click delay min", desc = "")
    @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
    public int AUTOEXPERIMENTS_CLICK_DELAY_MIN = 500;

    @Expose
    @ConfigOption(name = "click delay max", desc = "")
    @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
    public int AUTOEXPERIMENTS_CLICK_DELAY_MAX = 700;

    public enum MetaphysicalSerum {
        ONE,   // ordinal = 0
        TWO,   // ordinal = 1
        THREE; // ordinal = 2

        /** Returns 1,2,3 instead of 0,1,2 */
        public int toInt() {
            return this.ordinal() + 1;
        }
    }

    @Expose
    @ConfigOption(name = "metaphisical serum", desc = "")
    @ConfigEditorDropdown
    public MetaphysicalSerum METAPHYSICAL_SERUM = MetaphysicalSerum.THREE;

    @Expose
    @ConfigOption(name = "auto quit experiment", desc = "")
    @ConfigEditorBoolean
    public boolean AUTOEXPERIMENTS_AUTOQUIT = false;

}